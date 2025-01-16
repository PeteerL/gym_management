package com.gymmanagement.controllers;

import com.gymmanagement.dao.EnrollmentDAO;
import com.gymmanagement.dao.TrainingSessionDAO;
import com.gymmanagement.dao.UserDAO;
import com.gymmanagement.models.TrainingSession;
import com.gymmanagement.models.User;
import com.gymmanagement.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScheduleTrainingSessionController {

    @FXML
    private ComboBox<String> trainerComboBox;

    @FXML
    private TableView<TrainingSession> trainingSessionTable;

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

    @FXML
    private TableColumn<TrainingSession, Boolean> enrolledColumn;

    @FXML
    private Button enrollButton;

    @FXML
    private Button withdrawButton;

    private final TrainingSessionDAO sessionDAO = new TrainingSessionDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final UserDAO userDAO = new UserDAO();
    private Map<String, Integer> trainerNameToIdMap;

    @FXML
    public void initialize() {
        sessionIdColumn.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
        sessionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("sessionType"));
        sessionDateColumn.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        availableSlotsColumn.setCellValueFactory(new PropertyValueFactory<>("availableSlots"));
        enrolledColumn.setCellValueFactory(new PropertyValueFactory<>("enrolled"));

        loadTrainers();

        enrollButton.setOnAction(event -> handleEnrollSession());
        withdrawButton.setOnAction(event -> handleWithdrawSession());
    }

    private void loadTrainers() {
        List<User> trainers = userDAO.getAllUsers().stream()
                .filter(user -> "trainer".equalsIgnoreCase(user.getRole()))
                .collect(Collectors.toList());

        trainerNameToIdMap = trainers.stream()
                .collect(Collectors.toMap(User::getName, User::getId));

        trainerComboBox.setItems(FXCollections.observableArrayList(trainerNameToIdMap.keySet()));
        trainerComboBox.setOnAction(event -> handleTrainerSelection());
    }

    @FXML
    private void handleTrainerSelection() {
        String selectedTrainer = trainerComboBox.getValue();
        if (selectedTrainer != null) {
            int trainerId = trainerNameToIdMap.get(selectedTrainer);
            int clientId = SessionManager.getCurrentUser().getId();

            List<TrainingSession> sessions = sessionDAO.getSessionsByTrainer(trainerId, clientId);
            ObservableList<TrainingSession> observableSessions = FXCollections.observableArrayList(sessions);
            trainingSessionTable.setItems(observableSessions);
        }
    }

    @FXML
    private void handleEnrollSession() {
        TrainingSession selectedSession = trainingSessionTable.getSelectionModel().getSelectedItem();
        if (selectedSession == null) {
            showAlert("Error", "Please select a session to enroll.");
            return;
        }

        if (selectedSession.getAvailableSlots() <= 0) {
            showAlert("Error", "No available slots in the selected session.");
            return;
        }

        int clientId = SessionManager.getCurrentUser().getId();
        boolean success = enrollmentDAO.enrollClientInSession(clientId, selectedSession.getSessionId());
        if (success) {
            showAlert("Success", "You have been enrolled in the session.");
            handleTrainerSelection(); // Refresh session list
        } else {
            showAlert("Error", "Failed to enroll in the session.");
        }
    }

    @FXML
    private void handleWithdrawSession() {
        TrainingSession selectedSession = trainingSessionTable.getSelectionModel().getSelectedItem();
        if (selectedSession == null) {
            showAlert("Error", "Please select a session to withdraw.");
            return;
        }

        if (!selectedSession.isEnrolled()) {
            showAlert("Error", "You are not enrolled in this session.");
            return;
        }

        int clientId = SessionManager.getCurrentUser().getId();
        boolean success = enrollmentDAO.withdrawClientFromSession(clientId, selectedSession.getSessionId());
        if (success) {
            showAlert("Success", "You have been withdrawn from the session.");
            handleTrainerSelection(); // Refresh session list
        } else {
            showAlert("Error", "Failed to withdraw from the session.");
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
