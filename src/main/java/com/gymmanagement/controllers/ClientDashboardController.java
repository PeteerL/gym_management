package com.gymmanagement.controllers;

import com.gymmanagement.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientDashboardController {

    @FXML
    private VBox root; // Nodul principal pentru scena curentă

    @FXML
    private Label welcomeLabel;

    public void setWelcomeMessage(String name) {
        welcomeLabel.setText("Welcome " + name + "!");
    }

    @FXML
    private void handleViewSubscription() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/ViewSubscription.fxml"));
            Parent root = loader.load();

            ViewSubscriptionController controller = loader.getController();
            int clientId = SessionManager.getCurrentUser().getId(); // Preia clientId din sesiunea curentă
            controller.initialize(clientId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("View Subscription");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load subscription details.");
        }
    }

    @FXML
    private void handleScheduleTrainingSession() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/ScheduleTrainingSessionView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Schedule Training Session");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load Schedule Training Session view.");
        }
    }

    @FXML
    private void handleSendFeedbackToStaff() {
        try {
            String mailto = "mailto:admin@gym.com?subject=Feedback%20for%20Staff";
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            desktop.mail(new java.net.URI(mailto));
            System.out.println("Redirected to email client.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open email client.");
        }
    }

    @FXML
    private void handleReviewTrainer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/TrainerFeedbackView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Trainer Feedback");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewActivityStatistics() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/ViewActivityStatistics.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("View Activity Statistics");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load activity statistics.");
        }
    }


    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/LoginView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            SessionManager.setCurrentUser(null);
            System.out.println("Logout reușit. Redirecționat la pagina de login.");
        } catch (IOException e) {
            e.printStackTrace();
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
