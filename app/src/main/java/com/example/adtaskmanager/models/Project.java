package com.example.adtaskmanager.models;

public class Project {
    private int id;
    private String name;
    private String clientName;
    private String description; // Добавлено поле для описания
    private String status;
    private String startDate;
    private String endDate;
    private int completionPercentage; // Добавлено поле для процента выполнения

    // Обновленный конструктор, соответствующий данным из ProjectsFragment
    public Project(int id, String name, String clientName, String description, String status, String startDate, String endDate, int completionPercentage) {
        this.id = id;
        this.name = name;
        this.clientName = clientName;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.completionPercentage = completionPercentage;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getClientName() {
        return clientName;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getCompletionPercentage() { // Новый геттер
        return completionPercentage;
    }

    // Сеттеры (если нужны)
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setCompletionPercentage(int completionPercentage) { this.completionPercentage = completionPercentage; }
}