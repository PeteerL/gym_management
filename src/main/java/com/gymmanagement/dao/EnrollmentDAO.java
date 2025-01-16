package com.gymmanagement.dao;

import com.gymmanagement.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;

public class EnrollmentDAO {

    public boolean enrollClientInSession(int clientId, int sessionId) {
        String checkQuery = "SELECT COUNT(*) FROM enrollments WHERE client_id = ? AND session_id = ?";
        String enrollQuery = "INSERT INTO enrollments (client_id, session_id) VALUES (?, ?)";
        String updateSlotsQuery = "UPDATE training_sessions SET available_slots = available_slots - 1 WHERE session_id = ? AND available_slots > 0";

        try (Connection connection = DatabaseConfig.getConnection()) {
            // Verificăm dacă clientul este deja înscris
            if (isClientEnrolledInSession(clientId, sessionId)) {
                System.out.println("Client already enrolled in this session.");
                return false;
            }

            // Înscrierea clientului și actualizarea locurilor disponibile
            connection.setAutoCommit(false);

            try (PreparedStatement enrollStmt = connection.prepareStatement(enrollQuery);
                 PreparedStatement updateSlotsStmt = connection.prepareStatement(updateSlotsQuery)) {

                enrollStmt.setInt(1, clientId);
                enrollStmt.setInt(2, sessionId);
                enrollStmt.executeUpdate();

                updateSlotsStmt.setInt(1, sessionId);
                int rowsUpdated = updateSlotsStmt.executeUpdate();

                if (rowsUpdated > 0) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    System.out.println("No available slots for the session.");
                }
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean withdrawClientFromSession(int clientId, int sessionId) {
        String checkDateQuery = "SELECT session_date, capacity FROM training_sessions WHERE session_id = ?";
        String withdrawQuery = "DELETE FROM enrollments WHERE client_id = ? AND session_id = ?";
        String updateSlotsQuery = "UPDATE training_sessions SET available_slots = GREATEST(available_slots + 1, capacity) WHERE session_id = ?";

        try (Connection connection = DatabaseConfig.getConnection()) {
            // Verificăm dacă sesiunea este în viitor și dacă clientul este înscris
            if (!isClientEnrolledInSession(clientId, sessionId)) {
                System.out.println("Client is not enrolled in this session.");
                return false;
            }

            try (PreparedStatement checkStmt = connection.prepareStatement(checkDateQuery)) {
                checkStmt.setInt(1, sessionId);
                ResultSet resultSet = checkStmt.executeQuery();
                if (resultSet.next()) {
                    LocalDateTime sessionDate = resultSet.getTimestamp("session_date").toLocalDateTime();
                    if (sessionDate.isBefore(LocalDateTime.now().plusDays(2))) {
                        System.out.println("Cannot withdraw within 2 days of the session.");
                        return false;
                    }
                }
            }

            // Retragerea clientului și actualizarea locurilor disponibile
            connection.setAutoCommit(false);

            try (PreparedStatement withdrawStmt = connection.prepareStatement(withdrawQuery);
                 PreparedStatement updateSlotsStmt = connection.prepareStatement(updateSlotsQuery)) {

                withdrawStmt.setInt(1, clientId);
                withdrawStmt.setInt(2, sessionId);
                withdrawStmt.executeUpdate();

                updateSlotsStmt.setInt(1, sessionId);
                updateSlotsStmt.executeUpdate();

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isClientEnrolledInSession(int clientId, int sessionId) {
        String query = "SELECT COUNT(*) FROM enrollments WHERE client_id = ? AND session_id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            stmt.setInt(2, sessionId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
