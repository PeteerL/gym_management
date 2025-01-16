package com.gymmanagement.controllers;

import com.gymmanagement.dao.SubscriptionDAO;
import com.gymmanagement.models.Subscription;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ViewSubscriptionController {

    @FXML
    private Label subscriptionTypeLabel;

    @FXML
    private Label startDateLabel;

    @FXML
    private Label endDateLabel;

    @FXML
    private Label activeLabel;

    private final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();

    public void initialize(int clientId) {
        Subscription subscription = subscriptionDAO.getSubscriptionByClientId(clientId);

        if (subscription != null) {
            subscriptionTypeLabel.setText(subscription.getSubscriptionType());
            startDateLabel.setText(subscription.getStartDate().toString());
            endDateLabel.setText(subscription.getEndDate().toString());
            activeLabel.setText(subscription.isActive() ? "Active" : "Inactive");
        } else {
            subscriptionTypeLabel.setText("No subscription found.");
            startDateLabel.setText("-");
            endDateLabel.setText("-");
            activeLabel.setText("-");
        }
    }
}
