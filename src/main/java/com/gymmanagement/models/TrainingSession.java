package com.gymmanagement.models;

import java.time.LocalDateTime;

public class TrainingSession {
    private int sessionId;
    private int trainerId;
    private String sessionType;
    private LocalDateTime sessionDate;
    private int capacity;
    private int availableSlots;
    private boolean enrolled; // Noua proprietate

    // Constructor complet (pentru citire din baza de date)
    public TrainingSession(int sessionId, int trainerId, String sessionType, LocalDateTime sessionDate, int capacity, int availableSlots, boolean enrolled) {
        this.sessionId = sessionId;
        this.trainerId = trainerId;
        this.sessionType = sessionType;
        this.sessionDate = sessionDate;
        this.capacity = capacity;
        this.availableSlots = availableSlots;
        this.enrolled = enrolled;
    }

    // Constructor pentru compatibilitate (fara `enrolled`)
    public TrainingSession(int sessionId, int trainerId, String sessionType, LocalDateTime sessionDate, int capacity, int availableSlots) {
        this.sessionId = sessionId;
        this.trainerId = trainerId;
        this.sessionType = sessionType;
        this.sessionDate = sessionDate;
        this.capacity = capacity;
        this.availableSlots = availableSlots;
        this.enrolled = false; // Valoare implicită
    }

    // Constructor fara `sessionId` (pentru inserare)
    public TrainingSession(int trainerId, String sessionType, LocalDateTime sessionDate, int capacity, int availableSlots) {
        this.trainerId = trainerId;
        this.sessionType = sessionType;
        this.sessionDate = sessionDate;
        this.capacity = capacity;
        this.availableSlots = availableSlots;
        this.enrolled = false; // Valoare implicita
    }

    // Getteri si setteri
    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public LocalDateTime getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDateTime sessionDate) {
        this.sessionDate = sessionDate;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(int availableSlots) {
        this.availableSlots = availableSlots;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }
}
