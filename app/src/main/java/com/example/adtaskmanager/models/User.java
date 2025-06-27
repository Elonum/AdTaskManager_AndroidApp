package com.example.adtaskmanager.models;

public class User {
    private String id;
    private String name;
    private String surname;
    private String email;
    private long createdAt;

    public User(String id, String name, String surname, String email, long createdAt) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.createdAt = createdAt;
    }

    public User() {}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return name + " " + surname;
    }
}
