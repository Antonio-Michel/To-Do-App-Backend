package com.example.spring_todo_list.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.example.spring_todo_list.model.ToDo;

@Service
public class ToDoService {

    static List<ToDo> todos = new ArrayList<ToDo>();
    //static UUID id;
    static long id = 0;

    public List<ToDo> findAll() {
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
