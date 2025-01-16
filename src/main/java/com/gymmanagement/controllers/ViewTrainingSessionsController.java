package com.gymmanagement.controllers;

import com.gymmanagement.dao.TrainingSessionDAO;
import com.gymmanagement.models.TrainingSession;
import com.gymmanagement.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class ViewTrainingSessionsController {

    @FXML
    private TableView<TrainingSession> trainingTable;

    @FXML
    private TableColumn<TrainingSession, String> sessionTypeColumn;

    @FXML
    private TableColumn<TrainingSession, String> sessionDateColumn;

    @FXML
    private TableColumn<TrainingSession, Integer> capacityColumn;

    @FXML
    private TableColumn<TrainingSession, Integer> availableSlotsColumn;

    private TrainingSessionDAO trainingSessionDAO;

    public void initialize() {
        trainingSessionDAO = new TrainingSessionDAO();

        // Configure columns
        sessionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("sessionType"));
        sessionDateColumn.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        availableSlotsColumn.setCellValueFactory(new PropertyValueFactory<>("availableSlots"));

        loadTrainingSessions();
    }

    private void loadTrainingSessions() {
        try {
            int trainerId = SessionManager.getCurrentUser().getId();
            List<TrainingSession> sessions = trainingSessionDAO.getTrainingSessionsByTrainerId(trainerId);
            ObservableList<TrainingSession> observableSessions = FXCollections.observableArrayList(sessions);
            trainingTable.setItems(observableSessions);
        } finally {
            System.out.println("Finalizează procesul.");
        }
    }
}
