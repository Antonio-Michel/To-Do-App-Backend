package com.example.spring_todo_list.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.example.spring_todo_list.model.ToDo;

import static java.lang.Double.NaN;

@Service
public class ToDoService {

    static List<ToDo> todos = new ArrayList<ToDo>();
    //static UUID id;
    static long id = 0;

    public List<ToDo> findAll() {
        for (ToDo todo: todos
             ) {
            todo.setUrgency();
        }
        return todos;
    }

    public List<ToDo> findByNameContaining(String name) {
        return todos.stream().filter(todo -> todo.getName().contains(name)).toList();
    }

    public ToDo findById(long id) {
        return todos.stream().filter(todo -> id == todo.getId()).findAny().orElse(null);
    }

    public List<ToDo> findByPriority(List<ToDo> temp, int priority) {
        return temp.stream().filter(todo -> todo.getPriority() == priority).toList();
    }

    public double findAvgByPriority(List<ToDo> allTodos, int priority) {
        List<ToDo> allByPrio = allTodos.stream().filter(todo -> todo.getPriority() == priority && todo.isDone()).toList();
        long timeToComplete = 0;
        if(allByPrio.size() > 0 ) {
        for (int i = 0; i < allByPrio.size(); i++) {
            timeToComplete += ((allByPrio.get(i).getDoneDate().getTime() - allByPrio.get(i).getCreatedOn().getTime())/1000);
        }
        double result = (double) timeToComplete/ allByPrio.size();

        return result;}
        return 0.0;
    }

    public double findAvg(List<ToDo> allTodos) {
        allTodos = findByDone(allTodos, true);
        long timeToComplete = 0;
        if(!allTodos.isEmpty()) {
            for (int i = 0; i < allTodos.size(); i++) {
                timeToComplete += ((allTodos.get(i).getDoneDate().getTime() - allTodos.get(i).getCreatedOn().getTime())/1000);
            }
            return (double) timeToComplete/ allTodos.size();}
        return 0.0;
    }

    public ToDo save(ToDo todo) {
        //update ToDo
        if (todo.getId() != 0) {
            long _id = todo.getId();

            for (int idx = 0; idx < todos.size(); idx++) {
                if (_id == todos.get(idx).getId()) {
                    todos.set(idx, todo);
                    break;
                }
            }

            return todo;
        }
        //create new ToDo
        todo.setId(++id);
        todos.add(todo);
        return todo;
    }

    public void deleteById(long id) {
        todos.removeIf(todo -> id == todo.getId());
    }

    public void deleteAll() {
        todos.removeAll(todos);
    }

    public List<ToDo> findByDone(List<ToDo> temp, boolean isDone) {
        return temp.stream().filter(todo -> isDone == todo.isDone()).toList();
    }
    //create filter by not done

}
