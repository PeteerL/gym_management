package com.gymmanagement.models;

import javafx.beans.property.*;
import java.time.LocalDate;

public class ActivityLog {
    private int logId;
    private int clientId;
    private String activityType;
    private int durationMinutes;
    private LocalDate logDate;

    private final SimpleIntegerProperty logIdProperty = new SimpleIntegerProperty();
    private final SimpleIntegerProperty clientIdProperty = new SimpleIntegerProperty();
    private final SimpleStringProperty activityTypeProperty = new SimpleStringProperty();
    private final SimpleIntegerProperty durationProperty = new SimpleIntegerProperty();
    private final SimpleObjectProperty<LocalDate> logDateProperty = new SimpleObjectProperty<>();

    // Constructors
    public ActivityLog(int logId, int clientId, String activityType, int durationMinutes, LocalDate logDate) {
        this.logId = logId;
        this.clientId = clientId;
        this.activityType = activityType;
        this.durationMinutes = durationMinutes;
        this.logDate = logDate;

        this.logIdProperty.set(logId);
        this.clientIdProperty.set(clientId);
        this.activityTypeProperty.set(activityType);
        this.durationProperty.set(durationMinutes);
        this.logDateProperty.set(logDate);
    }

    public ActivityLog(int clientId, String activityType, int durationMinutes, LocalDate logDate) {
        this.clientId = clientId;
        this.activityType = activityType;
        this.durationMinutes = durationMinutes;
        this.logDate = logDate;

        this.clientIdProperty.set(clientId);
        this.activityTypeProperty.set(activityType);
        this.durationProperty.set(durationMinutes);
        this.logDateProperty.set(logDate);
    }

    // Getters and Setters
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
        this.logIdProperty.set(logId);
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
        this.clientIdProperty.set(clientId);
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
        this.activityTypeProperty.set(activityType);
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
        this.durationProperty.set(durationMinutes);
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
        this.logDateProperty.set(logDate);
    }

    // JavaFX Properties
    public IntegerProperty logIdProperty() {
        return logIdProperty;
    }

    public IntegerProperty clientIdProperty() {
        return clientIdProperty;
    }

    public StringProperty activityTypeProperty() {
        return activityTypeProperty;
    }

    public IntegerProperty durationProperty() {
        return durationProperty;
    }

    public ObjectProperty<LocalDate> logDateProperty() {
        return logDateProperty;
    }
}
