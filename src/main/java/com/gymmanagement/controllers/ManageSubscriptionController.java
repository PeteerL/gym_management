package com.gymmanagement.controllers;

import com.gymmanagement.dao.SubscriptionDAO;
import com.gymmanagement.dao.UserDAO;
import com.gymmanagement.models.Subscription;
import com.gymmanagement.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ManageSubscriptionController {

    @FXML
    private TableView<Subscription> subscriptionTable;

    @FXML
    private TableColumn<Subscription, Integer> subscriptionIdColumn;

    @FXML
    private TableColumn<Subscription, String> clientNameColumn;

    @FXML
    private TableColumn<Subscription, String> typeColumn;

    @FXML
    private TableColumn<Subscription, LocalDate> startDateColumn;

    @FXML
    private TableColumn<Subscription, LocalDate> endDateColumn;

    @FXML
    private TableColumn<Subscription, Boolean> activeColumn;

    private final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();
    private final UserDAO userDAO = new UserDAO();
    private Map<String, Integer> clientNameToIdMap;

    @FXML
    public void initialize() {
        // Configurare coloane tabel
        subscriptionIdColumn.setCellValueFactory(new PropertyValueFactory<>("subscriptionId"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("subscriptionType"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));

        // Încărcăm datele în tabel
        loadSubscriptions();

        // Mapăm numele clienților
        clientNameToIdMap = userDAO.getAllUsers().stream()
                .filter(user -> "client".equalsIgnoreCase(user.getRole()))
                .collect(Collectors.toMap(User::getName, User::getId));
    }

    private void loadSubscriptions() {
        List<Subscription> subscriptions = subscriptionDAO.getAllSubscriptions();
        ObservableList<Subscription> observableSubscriptions = FXCollections.observableArrayList(subscriptions);
        subscriptionTable.setItems(observableSubscriptions);
    }

    @FXML
    private void handleAddSubscription() {
        Subscription newSubscription = createSubscriptionDialog(null);
        if (newSubscription != null) {
            boolean success = subscriptionDAO.addSubscription(newSubscription);
            if (success) {
                showAlert("Success", "Subscription added successfully.");
                loadSubscriptions();
            } else {
                showAlert("Error", "Failed to add subscription.");
            }
        }
    }

    @FXML
    private void handleEditSubscription() {
        Subscription selectedSubscription = subscriptionTable.getSelectionModel().getSelectedItem();
        if (selectedSubscription == null) {
            showAlert("Error", "Please select a subscription to edit.");
            return;
        }

        Subscription updatedSubscription = createSubscriptionDialog(selectedSubscription);
        if (updatedSubscription != null) {
            boolean success = subscriptionDAO.updateSubscription(updatedSubscription);
            if (success) {
                showAlert("Success", "Subscription updated successfully.");
                loadSubscriptions();
            } else {
                showAlert("Error", "Failed to update subscription.");
            }
        }
    }

    @FXML
    private void handleDeleteSubscription() {
        Subscription selectedSubscription = subscriptionTable.getSelectionModel().getSelectedItem();
        if (selectedSubscription == null) {
            showAlert("Error", "Please select a subscription to delete.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Subscription");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete this subscription?");
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = subscriptionDAO.deleteSubscription(selectedSubscription.getSubscriptionId());
                if (success) {
                    showAlert("Success", "Subscription deleted successfully.");
                    loadSubscriptions();
                } else {
                    showAlert("Error", "Failed to delete subscription.");
                }
            }
        });
    }

    private Subscription createSubscriptionDialog(Subscription subscription) {
        Dialog<Subscription> dialog = new Dialog<>();
        dialog.setTitle(subscription == null ? "Add Subscription" : "Edit Subscription");

        // Fields
        Label clientLabel = new Label("Client:");
        ComboBox<String> clientComboBox = new ComboBox<>(FXCollections.observableArrayList(clientNameToIdMap.keySet()));
        Label typeLabel = new Label("Type:");
        ComboBox<String> typeComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "1 Month - Standard", "1 Month - Premium",
                "6 Months - Standard", "6 Months - Premium",
                "1 Year - Standard", "1 Year - Premium"
        ));
        Label startDateLabel = new Label("Start Date:");
        DatePicker startDatePicker = new DatePicker();
        Label endDateLabel = new Label("End Date:");
        DatePicker endDatePicker = new DatePicker();
        Label activeLabel = new Label("Active:");
        CheckBox activeCheckBox = new CheckBox();

        // Pre-fill fields if editing
        if (subscription != null) {
            clientComboBox.setValue(subscription.getClientName());
            typeComboBox.setValue(subscription.getSubscriptionType());
            startDatePicker.setValue(subscription.getStartDate());
            endDatePicker.setValue(subscription.getEndDate());
            activeCheckBox.setSelected(subscription.isActive());
        }

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(clientLabel, 0, 0);
        grid.add(clientComboBox, 1, 0);
        grid.add(typeLabel, 0, 1);
        grid.add(typeComboBox, 1, 1);
        grid.add(startDateLabel, 0, 2);
        grid.add(startDatePicker, 1, 2);
        grid.add(endDateLabel, 0, 3);
        grid.add(endDatePicker, 1, 3);
        grid.add(activeLabel, 0, 4);
        grid.add(activeCheckBox, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Update end date based on type
        typeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                LocalDate startDate = startDatePicker.getValue() != null ? startDatePicker.getValue() : LocalDate.now();
                startDatePicker.setValue(startDate); // Completează automat Start Date dacă nu este setat
                switch (newValue) {
                    case "1 Month - Standard":
                    case "1 Month - Premium":
                        endDatePicker.setValue(startDate.plusMonths(1));
                        break;
                    case "6 Months - Standard":
                    case "6 Months - Premium":
                        endDatePicker.setValue(startDate.plusMonths(6));
                        break;
                    case "1 Year - Standard":
                    case "1 Year - Premium":
                        endDatePicker.setValue(startDate.plusYears(1));
                        break;
                }
            }
        });

        // Setăm automat data curentă la deschiderea dialogului, dacă este pentru adăugare
        if (subscription == null) {
            startDatePicker.setValue(LocalDate.now());
        }


        // Result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String clientName = clientComboBox.getValue();
                Integer clientId = clientNameToIdMap.get(clientName);
                String subscriptionType = typeComboBox.getValue();
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                boolean isActive = activeCheckBox.isSelected();

                if (clientId == null || clientName == null || subscriptionType == null || startDate == null || endDate == null) {
                    showAlert("Validation Error", "All fields are required.");
                    return null;
                }

                return new Subscription(
                        subscription == null ? 0 : subscription.getSubscriptionId(),
                        clientId,
                        clientName,
                        subscriptionType,
                        startDate,
                        endDate,
                        isActive
                );
            }
            return null;
        });

        dialog.showAndWait();
        return dialog.getResult();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
