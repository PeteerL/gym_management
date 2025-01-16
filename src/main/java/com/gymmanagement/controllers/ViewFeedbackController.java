package com.gymmanagement.controllers;

import com.gymmanagement.dao.FeedbackDAO;
import com.gymmanagement.models.Feedback;
import com.gymmanagement.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ViewFeedbackController {

    @FXML
    private TableView<Feedback> feedbackTable;

    @FXML
    private TableColumn<Feedback, String> feedbackTextColumn;

    @FXML
    private TableColumn<Feedback, Integer> ratingColumn;

    @FXML
    private TableColumn<Feedback, String> timestampColumn;

    private FeedbackDAO feedbackDAO;

    @FXML
    public void initialize() {
        feedbackDAO = new FeedbackDAO();

        // Configure columns
        feedbackTextColumn.setCellValueFactory(new PropertyValueFactory<>("feedbackText"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        loadFeedback();
    }

    private void loadFeedback() {
        int trainerId = SessionManager.getCurrentUser().getId();
        List<Feedback> feedbackList = feedbackDAO.getFeedbackForTrainer(trainerId);

        ObservableList<Feedback> observableFeedback = FXCollections.observableArrayList(feedbackList);
        feedbackTable.setItems(observableFeedback);
    }
}
