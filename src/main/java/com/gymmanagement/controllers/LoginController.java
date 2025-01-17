package com.gymmanagement.controllers;

import com.gymmanagement.dao.UserDAO;
import com.gymmanagement.models.User;
import com.gymmanagement.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    // Datele contului generic pentru admin
    private static final String ADMIN_EMAIL = "a";//admin@gym.com
    private static final String ADMIN_PASSWORD = "a";//admin123

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Verificare pentru contul generic de admin
        if (ADMIN_EMAIL.equals(email) && ADMIN_PASSWORD.equals(password)) {
            System.out.println("Autentificare reușită pentru admin generic.");
            User adminUser = new User(0, "Admin", ADMIN_EMAIL, ADMIN_PASSWORD, "admin");
            SessionManager.setCurrentUser(adminUser); // Setează utilizatorul curent
            redirectToAdminDashboard(adminUser);
            return;
        }

        // Verificare in baza de date pentru clienti si antrenori
        User user = userDAO.getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Autentificare reușită pentru: " + user.getName());
            errorLabel.setText("");

            // Seteaza utilizatorul curent în sesiune
            SessionManager.setCurrentUser(user);

            // Redirectionare pe baza rolului
            switch (user.getRole().toLowerCase()) {
                case "trainer":
                    redirectToTrainerDashboard(user);
                    break;
                case "client":
                    redirectToClientDashboard(user);
                    break;
                default:
                    errorLabel.setText("Rol necunoscut!");
            }
        } else {
            errorLabel.setText("Email sau parolă incorecte!");
        }
    }


    private void redirectToAdminDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/AdminDashboardView.fxml"));
            Scene scene = new Scene(loader.load());
            AdminDashboardController controller = loader.getController();
            controller.setWelcomeMessage(user.getName());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void redirectToTrainerDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/TrainerDashboardView.fxml"));
            Scene scene = new Scene(loader.load());
            TrainerDashboardController controller = loader.getController();
            controller.setWelcomeMessage(user.getName());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void redirectToClientDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/ClientDashboardView.fxml"));
            Scene scene = new Scene(loader.load());
            ClientDashboardController controller = loader.getController();
            controller.setWelcomeMessage(user.getName());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
