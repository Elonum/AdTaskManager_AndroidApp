package com.example.adtaskmanager.models;

public class Client {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String notes; // Примечания

    public Client(int id, String name, String phone, String email, String address, String notes) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.notes = notes;
    }

    // Конструктор для использования в ArrayAdapter (если требуется только имя)
    public Client(int id, String name, String email) { // Пример конструктора из AddEditProjectActivity
        this.id = id;
        this.name = name;
        this.email = email;
        // Остальные поля остаются null или по умолчанию
    }

    @Override
    public String toString() {
        return name; // Это важно для ArrayAdapter, чтобы он отображал имя клиента
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getNotes() { return notes; }

    // Setters (если нужно для редактирования объекта)
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setNotes(String notes) { this.notes = notes; }
}