package com.gymmanagement.dao;

import com.gymmanagement.config.DatabaseConfig;
import com.gymmanagement.models.Feedback;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    // Adauga un nou feedback in baza de date
    public boolean addFeedback(Feedback feedback) {
        String query = "INSERT INTO feedback (client_id, trainer_id, feedback_text, rating, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, feedback.getClientId());
            stmt.setInt(2, feedback.getTrainerId());
            stmt.setString(3, feedback.getFeedbackText());
            stmt.setInt(4, feedback.getRating());
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis())); // Setăm timestamp-ul curent
            return stmt.executeUpdate() > 0; // Returnează true dacă inserarea a avut succes
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Returneaza false daca apare o eroare
    }

    // Obtine toate feedback-urile pentru un antrenor specific
    public List<Feedback> getFeedbackForTrainer(int trainerId) {
        String query = "SELECT * FROM feedback WHERE trainer_id = ? ORDER BY timestamp DESC";
        List<Feedback> feedbackList = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Feedback feedback = new Feedback(
                        rs.getInt("feedback_id"),
                        rs.getInt("client_id"),
                        rs.getInt("trainer_id"),
                        rs.getString("feedback_text"),
                        rs.getInt("rating"),
                        rs.getTimestamp("timestamp")
                );
                feedbackList.add(feedback);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }

    // Sterge un feedback dupa ID
    public boolean deleteFeedback(int feedbackId) {
        String query = "DELETE FROM feedback WHERE feedback_id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, feedbackId);
            return stmt.executeUpdate() > 0; // Returnează true dacă ștergerea a avut succes
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Returneaza false daca apare o eroare
    }

    // Obtine toate feedback-urile (optional, pentru administratori)
    public List<Feedback> getAllFeedback() {
        String query = "SELECT * FROM feedback ORDER BY timestamp DESC";
        List<Feedback> feedbackList = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Feedback feedback = new Feedback(
                        rs.getInt("feedback_id"),
                        rs.getInt("client_id"),
                        rs.getInt("trainer_id"),
                        rs.getString("feedback_text"),
                        rs.getInt("rating"),
                        rs.getTimestamp("timestamp")
                );
                feedbackList.add(feedback);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }

    // Obtine lista cu numele antrenorilor
    public List<String> getTrainerNames() {
        List<String> trainerNames = new ArrayList<>();
        String query = "SELECT name FROM users WHERE role = 'trainer'";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                trainerNames.add(rs.getString("name")); // Adauga numele antrenorului in lista
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trainerNames; // Returneaza lista cu numele antrenorilor
    }
}
