package com.example.spring_todo_list.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_todo_list.model.ToDo;
import com.example.spring_todo_list.service.ToDoService;

//@CrossOrigin(origins = "http://localhost:8080/")
@RestController
@RequestMapping("/api")
public class ToDoController {
    @Autowired
    ToDoService toDoService;

    @CrossOrigin
    @GetMapping("/todos")
    public ResponseEntity<HashMap<String, List>> getAllToDos(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long done,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) String sortByPriority,
            @RequestParam(required = false) String sortByDueDate,
    @RequestParam(required = false) Integer page) {
        try {
            List<ToDo> todos = new ArrayList<ToDo>();

            //time metrics
            List<ToDo> allTodos = new ArrayList<ToDo>(toDoService.findAll());
            Double lowAvg = (Double) toDoService.findAvgByPriority(allTodos, 0);
            Double medAvg = toDoService.findAvgByPriority(allTodos, 1);
            Double highAvg = toDoService.findAvgByPriority(allTodos, 2);
            Double totalAvg = toDoService.findAvg(allTodos);

            List<Double> averages = new ArrayList<Double>();
            averages.add(lowAvg);
            averages.add(medAvg);
            averages.add(highAvg);
            averages.add(totalAvg);

            HashMap<String, List> items = new HashMap<>();

            items.put("avgTimes",averages);

            //pagination variables
            int pageSize = 10; //would be nice to have the client send the page size desired
            if(page == null)
            {page = 0;}
            else if (page > 0) {
                page = page -1;
            }
            int offset = (page * pageSize);


            //first filter by name or return all todos
            if (name == null) {
                todos.addAll(toDoService.findAll());
            } else {
                todos.addAll(toDoService.findByNameContaining(name));
            }

            //next filter by done status
            if (done != null) {
                List<ToDo> temp = new ArrayList<ToDo>();
                if (done > 0) {
                    todos = toDoService.findByDone(todos,true);
                } else if (done == 0) {
                    todos = toDoService.findByDone(todos,false);
                }
            }

            //filter by priority
            if (priority != null && priority != -1) {
                todos = toDoService.findByPriority(todos, priority);
            }

            //sort cases
            if (validateSort(sortByPriority) && validateSort(sortByDueDate)) {
                //sort by both, starting on priority
                Comparator<ToDo> toDoComparator;
                if(sortByDueDate.equals("ASC") && sortByPriority.equals("ASC")) {
                    toDoComparator = Comparator.comparing(ToDo::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(ToDo::getPriority);
                } else if ( sortByDueDate.equals("DESC") && sortByPriority.equals("ASC")) {
                    toDoComparator = Comparator.comparing(ToDo::getDueDate, Comparator.nullsFirst(Comparator.naturalOrder())).reversed()
                            .thenComparing(ToDo::getPriority);
                } else if ( sortByDueDate.equals("ASC") && sortByPriority.equals("DESC")) {
                    toDoComparator = Comparator.comparing(ToDo::getDueDate, Comparator.nullsFirst(Comparator.naturalOrder()))
                            .thenComparing(ToDo::getPriority).reversed();
                } else {
                    toDoComparator = Comparator.comparing(ToDo::getDueDate, Comparator.nullsFirst(Comparator.naturalOrder())).reversed()
                            .thenComparing(ToDo::getPriority).reversed();
                }
                todos.sort(toDoComparator);
            } else if (validateSort(sortByPriority)) {
                switch (sortByPriority) {
                    case "ASC":
                        todos.sort(Comparator.comparing(ToDo::getPriority));
                        break;
                    case "DESC":
                        todos.sort(Comparator.comparing(ToDo::getPriority).reversed());
                        break;
                    default:
                        break;
                }
            } else if (validateSort(sortByDueDate)) {
                switch (sortByDueDate) {
                    case "ASC":
                        todos.sort(Comparator.comparing(ToDo::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())));
                        break;
                    case "DESC":
                        todos.sort(Comparator.comparing(ToDo::getDueDate, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
                        break;
                    default:
                        break;
                }
            }

            if (todos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            List<ToDo> pagedTodos = new ArrayList<ToDo>();
            int totalTodos = todos.size();
            double pages = Math.ceil((double)totalTodos/(double)pageSize);
            int totalPages = (int) pages;
            pagedTodos = todos.subList(offset, Math.min(((page+1) * pageSize), totalTodos));

            List<Integer> sizeAndPage = new ArrayList<Integer>();
            sizeAndPage.add(totalTodos);
            sizeAndPage.add(totalPages);

            items.put("results",pagedTodos);
            items.put("size", sizeAndPage);
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.toString());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean validateSort(String sort) {
        return sort != null && !sort.isEmpty();
    }

    @CrossOrigin
    @GetMapping("/todos/{id}")
    public ResponseEntity<ToDo> getToDoById(@PathVariable("id") long id) {
        ToDo todo = toDoService.findById(id);

        if (todo != null) {
            return new ResponseEntity<>(todo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @PostMapping("/todos")
    public ResponseEntity<ToDo> createToDo(@RequestBody ToDo todo) {
        try {
            ToDo _todo = toDoService
                    .save(new ToDo(todo.getName(), todo.getDueDate(), false, todo.getPriority()));
            return new ResponseEntity<>(_todo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PutMapping("/todos/{id}") //PUT to edit name, priority and dueDate
    public ResponseEntity<ToDo> updateToDo(@PathVariable("id") long id, @RequestBody ToDo todo) {
        ToDo _todo = toDoService.findById(id);

        if(_todo != null) {
            _todo.setName(todo.getName());
            _todo.setPriority(todo.getPriority());
            _todo.setDueDate(todo.getDueDate());
            _todo.setUrgency();
            return new ResponseEntity<>(toDoService.save(_todo), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @PutMapping("/todos/{id}/done") //PUT to edit name, priority and dueDate
    public ResponseEntity<ToDo> updateDone(@PathVariable("id") long id/*, @RequestBody ToDo todo*/) {
        ToDo _todo = toDoService.findById(id);

        if(_todo != null) {
            if(!_todo.isDone()) {
            _todo.setDone(true);
            return new ResponseEntity<>(toDoService.save(_todo), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @PutMapping("/todos/{id}/undone") //PUT to edit name, priority and dueDate
    public ResponseEntity<ToDo> updateUndone(@PathVariable("id") long id/*, @RequestBody ToDo todo*/) {
        ToDo _todo = toDoService.findById(id);

        if(_todo != null) {
            _todo.setDone(false);
            return new ResponseEntity<>(toDoService.save(_todo), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @DeleteMapping("/todos/{id}")
    public ResponseEntity<HttpStatus> deleteToDo(@PathVariable("id") long id) {
        try {
            toDoService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    @CrossOrigin
    @DeleteMapping("/todos")
    public ResponseEntity<HttpStatus> deleteAllToDos() {
        try {
            toDoService.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     */
}
