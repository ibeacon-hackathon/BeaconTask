package com.beacontask.android;

/**
 * Created by Refael Ozeri on 19/12/2014.
 */
public class Task {
    private String name;
    private String description;
    private int credit;
    private boolean is_assigned;
    private boolean completed;

    public Task(String name, String description, int credit, boolean is_assigned, boolean completed) {
        this.name = name;
        this.description = description;
        this.credit = credit;
        this.is_assigned = is_assigned;
        this.completed = completed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public void setIs_assigned(boolean is_assigned) {
        this.is_assigned = is_assigned;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getName() {

        return name;
    }
    public String getDescription() {

        return description;
    }

    public int getCredit() {
        return credit;
    }

    public boolean isAssigned() {
        return is_assigned;
    }

    public boolean isCompleted() {
        return completed;
    }
}
