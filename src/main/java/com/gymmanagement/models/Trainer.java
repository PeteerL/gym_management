package com.gymmanagement.models;

public class Trainer {
    private int id;
    private int userId;
    private String specialization;
    private String phoneNumber;

    public Trainer(int id, int userId, String specialization, String phoneNumber) {
        this.id = id;
        this.userId = userId;
        this.specialization = specialization;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}