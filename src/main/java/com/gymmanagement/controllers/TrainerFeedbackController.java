package com.gymmanagement.controllers;

import com.gymmanagement.dao.FeedbackDAO;
import com.gymmanagement.dao.UserDAO;
import com.gymmanagement.models.Feedback;
import com.gymmanagement.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class TrainerFeedbackController {

    @FXML
    private ChoiceBox<String> trainerChoiceBox;

    @FXML
    private TextArea feedbackTextArea;

    @FXML
    private Slider ratingSlider;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        loadTrainers();
    }

    private void loadTrainers() {
        // Obtine numele antrenorilor din baza de date si populeaza ChoiceBox-ul
        ObservableList<String> trainerNames = FXCollections.observableArrayList(feedbackDAO.getTrainerNames());
        trainerChoiceBox.setItems(trainerNames);
    }

    @FXML
    private void handleSubmitFeedback() {
        String selectedTrainerName = trainerChoiceBox.getValue();
        String feedbackText = feedbackTextArea.getText();
        int rating = (int) ratingSlider.getValue();

        if (selectedTrainerName == null || feedbackText.isEmpty()) {
            showAlert("Error", "Please fill all fields before submitting the feedback.");
            return;
        }

        int trainerId = userDAO.getUserIdByName(selectedTrainerName);
        int clientId = SessionManager.getCurrentUser().getId();

        Feedback feedback = new Feedback(clientId, trainerId, feedbackText, rating);

        if (feedbackDAO.addFeedback(feedback)) {
            showAlert("Success", "Feedback submitted successfully.");
            clearFields();
            closeWindow();
        } else {
            showAlert("Error", "Failed to submit feedback. Please try again.");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void clearFields() {
        trainerChoiceBox.setValue(null);
        feedbackTextArea.clear();
        ratingSlider.setValue(3); // Seteaza valoarea implicita a slider-ului la 3
    }

    private void closeWindow() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
