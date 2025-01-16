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

public class TrainerDashboardController {

    @FXML
    private VBox root; // Nodul principal pentru scena curentă

    @FXML
    private Label welcomeLabel;

    public void setWelcomeMessage(String name) {
        welcomeLabel.setText("Welcome Trainer " + name + "!");
    }


    @FXML
    private void handleViewTrainingSessions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/ViewTrainingSessions.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("View Training Sessions");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load training sessions.");
        }
    }


    @FXML
    private void handleViewFeedback() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/ViewFeedback.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("View Feedback");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load feedback view.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // Încarcă interfața de Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/LoginView.fxml"));
            Scene scene = new Scene(loader.load());

            // Obține Stage-ul curent și setează scena Login
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            // Opțional: Curăță sesiunea utilizatorului curent
            SessionManager.setCurrentUser(null);

            System.out.println("Deconectare reușită. Redirecționat la pagina de login.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Eroare la încărcarea paginii de login!");
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
