package com.example.spring_todo_list.model;

import java.util.Date;
import java.util.UUID;

public class ToDo {
    private long id = 0;
    //private UUID id = UUID.randomUUID();

    private String name;
    //need to make it required and max length 120 chars

    private Date dueDate; //need to make it optional

    private boolean done;

    private Date doneDate;

    private int priority;

    private final Date createdOn;

    public ToDo (String name, Date dueDate, boolean done, int priority) {
        this.name = name;
        this.dueDate = dueDate;
        this.done = done;
        this.priority = priority;
        this.createdOn = new Date();
    }

    public long getId() {
    //public UUID getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        if(done) {
            this.done = true;
            this.doneDate = new Date();
        } else {
            this.done = false;
            this.doneDate = null;
        }
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public Date getCreatedOn() {
        return createdOn;
    }


    @Override
    public String toString() {
        return "To-Do [name = " + name + ", prio = " + priority + ", done = " + done + "]";
    }

}
