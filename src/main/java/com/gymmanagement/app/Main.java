package com.gymmanagement.app;

import com.gymmanagement.config.DatabaseConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Incarca fisierul LoginView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gymmanagement/LoginView.fxml"));
            Scene scene = new Scene(loader.load());

            primaryStage.setTitle("Login - Gym Management");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Verifica conexiunea la baza de date
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                System.out.println("Conexiune reușită la baza de date!");
            } else {
                System.out.println("Conexiunea la baza de date a eșuat!");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la conectarea la baza de date: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
