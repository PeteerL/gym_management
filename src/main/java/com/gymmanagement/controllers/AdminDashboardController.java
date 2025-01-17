package com.gymmanagement.controllers;

import com.gymmanagement.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;

public class AdminDashboardController {

    @FXML
    private VBox root; // Nod principal din FXML

    @FXML
    private Label welcomeLabel;

    public void setWelcomeMessage(String name) {
        welcomeLabel.setText("Welcome Admin!");
    }

    @FXML
    private void handleManageUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/ManageUsersView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Manage Users");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageSubscriptions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/ManageSubscriptionView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Manage Subscriptions");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Eroare la încărcarea gestionării abonamentelor!");
        }
    }

    @FXML
    private void handleManageTrainingSessions() {
        try {
            // Incarca fisierul FXML pentru gestionarea sesiunilor de antrenament
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/ManageTrainingSessionsView.fxml"));
            Scene scene = new Scene(loader.load());

            // Creeaza o fereastra noua pentru gestionarea sesiunilor
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Manage Training Sessions");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Eroare la încărcarea gestionării sesiunilor de antrenament!");
        }
    }

    @FXML
    private void handleManageActivityLog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/ManageActivityLogView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Manage Activity Logs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the Manage Activity Logs view.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // Incarca interfata de Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/LoginView.fxml"));
            Scene scene = new Scene(loader.load());

            // Obtine Stage-ul curent si seteaza scena Login
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            // Optional: Curata sesiunea utilizatorului curent (daca ai un sistem de sesiuni)
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
