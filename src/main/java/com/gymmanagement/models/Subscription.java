package com.gymmanagement.models;

import java.time.LocalDate;

public class Subscription {
    private int subscriptionId;
    private int clientId; // ID-ul clientului
    private String clientName; // Numele clientului
    private String subscriptionType;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;

    // Constructor complet (pentru citire din baza de date)
    public Subscription(int subscriptionId, String clientName, String subscriptionType, LocalDate startDate, LocalDate endDate, boolean active) {
        this.subscriptionId = subscriptionId;
        this.clientName = clientName;
        this.subscriptionType = subscriptionType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    // Constructor pentru inserare (fără subscriptionId)
    public Subscription(int clientId, String subscriptionType, LocalDate startDate, LocalDate endDate, boolean active) {
        this.clientId = clientId;
        this.subscriptionType = subscriptionType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    // Constructor complet (pentru editare și alte cazuri)
    public Subscription(int subscriptionId, int clientId, String clientName, String subscriptionType, LocalDate startDate, LocalDate endDate, boolean active) {
        this.subscriptionId = subscriptionId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.subscriptionType = subscriptionType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    // Getteri și setteri
    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
