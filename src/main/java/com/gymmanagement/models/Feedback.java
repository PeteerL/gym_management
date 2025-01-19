package com.gymmanagement.models;

import java.sql.Timestamp;

public class Feedback {
    private int feedbackId;
    private int clientId;
    private int trainerId;
    private String feedbackText;
    private int rating;
    private Timestamp timestamp;

    // Constructor pentru inserare (fara feedbackId si timestamp)
    public Feedback(int clientId, int trainerId, String feedbackText, int rating) {
        this.clientId = clientId;
        this.trainerId = trainerId;
        this.feedbackText = feedbackText;
        this.rating = rating;
    }

    // Constructor complet (pentru citire din baza de date)
    public Feedback(int feedbackId, int clientId, int trainerId, String feedbackText, int rating, Timestamp timestamp) {
        this.feedbackId = feedbackId;
        this.clientId = clientId;
        this.trainerId = trainerId;
        this.feedbackText = feedbackText;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    // Getteri si setteri
    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
