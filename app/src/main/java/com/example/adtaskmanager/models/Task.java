package com.example.adtaskmanager.models;

public class Task {
    private int id;
    private int projectId;
    private String assigneeId; // Пока String, в будущем это будет ID пользователя
    private String title;
    private String description;
    private String dueDate; // Дата и время выполнения, пока String, потом Date/LocalDateTime
    private String status;
    private int completionPercentage;
    private String assigneeName; // Добавим для удобства отображения в UI

    public Task(int id, int projectId, String assigneeId, String title, String description, String dueDate, String status, int completionPercentage, String assigneeName) {
        this.id = id;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.completionPercentage = completionPercentage;
        this.assigneeName = assigneeName;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    // Сеттеры (если нужны)
    public void setId(int id) {
        this.id = id;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }
}
