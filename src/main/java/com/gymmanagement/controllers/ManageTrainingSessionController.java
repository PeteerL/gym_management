package com.gymmanagement.controllers;

import com.gymmanagement.dao.TrainingSessionDAO;
import com.gymmanagement.dao.UserDAO;
import com.gymmanagement.models.TrainingSession;
import com.gymmanagement.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ManageTrainingSessionController {

    @FXML
    private TableView<TrainingSession> sessionTable;

    @FXML
    private TableColumn<TrainingSession, Integer> sessionIdColumn;

    @FXML
    private TableColumn<TrainingSession, String> sessionTypeColumn;

    @FXML
    private TableColumn<TrainingSession, LocalDateTime> sessionDateColumn;

    @FXML
    private TableColumn<TrainingSession, Integer> capacityColumn;

    @FXML
    private TableColumn<TrainingSession, Integer> availableSlotsColumn;

    private final TrainingSessionDAO sessionDAO = new TrainingSessionDAO();
    private final UserDAO userDAO = new UserDAO();
    private Map<String, Integer> trainerNameToIdMap;

    @FXML
    public void initialize() {
        sessionIdColumn.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
        sessionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("sessionType"));
        sessionDateColumn.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        availableSlotsColumn.setCellValueFactory(new PropertyValueFactory<>("availableSlots"));

        loadSessions();
        loadTrainerNames();
    }

    private void loadSessions() {
        List<TrainingSession> sessions = sessionDAO.getAllSessions();
        ObservableList<TrainingSession> observableSessions = FXCollections.observableArrayList(sessions);
        sessionTable.setItems(observableSessions);
    }

    private void loadTrainerNames() {
        trainerNameToIdMap = userDAO.getAllUsers().stream()
                .filter(user -> "trainer".equalsIgnoreCase(user.getRole()))
                .collect(Collectors.toMap(User::getName, User::getId));
    }

    @FXML
    private void handleAddSession() {
        TrainingSession newSession = createSessionDialog(null);
        if (newSession != null) {
            boolean success = sessionDAO.addSession(newSession);
            if (success) {
                showAlert("Success", "Training session added successfully.");
                loadSessions();
            } else {
                showAlert("Error", "Failed to add training session.");
            }
        }
    }

    @FXML
    private void handleEditSession() {
        TrainingSession selectedSession = sessionTable.getSelectionModel().getSelectedItem();
        if (selectedSession == null) {
            showAlert("Error", "Please select a session to edit.");
            return;
        }

        TrainingSession updatedSession = createSessionDialog(selectedSession);
        if (updatedSession != null) {
            boolean success = sessionDAO.updateSession(updatedSession);
            if (success) {
                showAlert("Success", "Training session updated successfully.");
                loadSessions();
            } else {
                showAlert("Error", "Failed to update training session.");
            }
        }
    }

    @FXML
    private void handleDeleteSession() {
        TrainingSession selectedSession = sessionTable.getSelectionModel().getSelectedItem();
        if (selectedSession == null) {
            showAlert("Error", "Please select a session to delete.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Training Session");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete this session?");
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = sessionDAO.deleteSession(selectedSession.getSessionId());
                if (success) {
                    showAlert("Success", "Training session deleted successfully.");
                    loadSessions();
                } else {
                    showAlert("Error", "Failed to delete training session.");
                }
            }
        });
    }

    private TrainingSession createSessionDialog(TrainingSession session) {
        Dialog<TrainingSession> dialog = new Dialog<>();
        dialog.setTitle(session == null ? "Add Training Session" : "Edit Training Session");

        Label typeLabel = new Label("Session Type:");
        TextField typeField = new TextField();
        Label dateLabel = new Label("Session Date:");
        DatePicker datePicker = new DatePicker();
        Label timeLabel = new Label("Session Time (HH:mm):");
        TextField timeField = new TextField();
        Label capacityLabel = new Label("Capacity:");
        TextField capacityField = new TextField();
        Label slotsLabel = new Label("Available Slots:");
        TextField slotsField = new TextField();
        Label trainerLabel = new Label("Trainer:");
        ChoiceBox<String> trainerChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(trainerNameToIdMap.keySet()));

        if (session != null) {
            typeField.setText(session.getSessionType());
            datePicker.setValue(session.getSessionDate().toLocalDate());
            timeField.setText(session.getSessionDate().toLocalTime().toString());
            capacityField.setText(String.valueOf(session.getCapacity()));
            slotsField.setText(String.valueOf(session.getAvailableSlots()));
            trainerChoiceBox.setValue(getTrainerNameById(session.getTrainerId()));
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(typeLabel, 0, 0);
        grid.add(typeField, 1, 0);
        grid.add(dateLabel, 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(timeLabel, 0, 2);
        grid.add(timeField, 1, 2);
        grid.add(capacityLabel, 0, 3);
        grid.add(capacityField, 1, 3);
        grid.add(slotsLabel, 0, 4);
        grid.add(slotsField, 1, 4);
        grid.add(trainerLabel, 0, 5);
        grid.add(trainerChoiceBox, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String type = typeField.getText();
                if (type.isEmpty()) {
                    showAlert("Validation Error", "Session type cannot be empty.");
                    return null;
                }

                if (datePicker.getValue() == null) {
                    showAlert("Validation Error", "Please select a session date.");
                    return null;
                }

                String time = timeField.getText();
                if (time.isEmpty() || !time.matches("\\d{2}:\\d{2}")) {
                    showAlert("Validation Error", "Please enter a valid time in HH:mm format.");
                    return null;
                }

                LocalDateTime dateTime;
                try {
                    dateTime = LocalDateTime.of(
                            datePicker.getValue(),
                            java.time.LocalTime.parse(time)
                    );
                } catch (Exception e) {
                    showAlert("Validation Error", "Invalid date and time.");
                    return null;
                }

                int capacity;
                try {
                    capacity = Integer.parseInt(capacityField.getText());
                    if (capacity <= 0) {
                        showAlert("Validation Error", "Capacity must be a positive number.");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    showAlert("Validation Error", "Capacity must be a numeric value.");
                    return null;
                }

                int slots;
                try {
                    slots = Integer.parseInt(slotsField.getText());
                    if (slots < 0 || slots > capacity) {
                        showAlert("Validation Error", "Available slots must be between 0 and the capacity.");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    showAlert("Validation Error", "Available slots must be a numeric value.");
                    return null;
                }

                String trainerName = trainerChoiceBox.getValue();
                if (trainerName == null || !trainerNameToIdMap.containsKey(trainerName)) {
                    showAlert("Validation Error", "Please select a valid trainer.");
                    return null;
                }
                int trainerId = trainerNameToIdMap.get(trainerName);

                if (session == null) {
                    return new TrainingSession(trainerId, type, dateTime, capacity, slots);
                } else {
                    session.setTrainerId(trainerId);
                    session.setSessionType(type);
                    session.setSessionDate(dateTime);
                    session.setCapacity(capacity);
                    session.setAvailableSlots(slots);
                    return session;
                }
            }
            return null;
        });

        dialog.showAndWait();
        return dialog.getResult();
    }


    private String getTrainerNameById(int trainerId) {
        return trainerNameToIdMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == trainerId)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
