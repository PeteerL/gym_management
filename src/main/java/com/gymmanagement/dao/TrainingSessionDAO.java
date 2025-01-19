package com.gymmanagement.dao;

import com.gymmanagement.config.DatabaseConfig;
import com.gymmanagement.models.TrainingSession;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrainingSessionDAO {

    public List<TrainingSession> getAllSessions() {
        List<TrainingSession> sessions = new ArrayList<>();
        String query = "SELECT * FROM training_sessions";

        try (Connection connection = DatabaseConfig.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                TrainingSession session = new TrainingSession(
                        resultSet.getInt("session_id"),
                        resultSet.getInt("trainer_id"),
                        resultSet.getString("session_type"),
                        resultSet.getTimestamp("session_date").toLocalDateTime(),
                        resultSet.getInt("capacity"),
                        resultSet.getInt("available_slots")
                );
                sessions.add(session);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sessions;
    }

    public boolean addSession(TrainingSession session) {
        String query = "INSERT INTO training_sessions (trainer_id, session_type, session_date, capacity, available_slots) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Verificați dacă `id` este valid
            int trainerId = session.getTrainerId();
            if (!isTrainerIdValid(trainerId)) {
                System.err.println("Invalid trainer ID: " + trainerId);
                return false;
            }

            statement.setInt(1, trainerId); // Aici setăm id-ul din tabela `users`
            statement.setString(2, session.getSessionType());
            statement.setTimestamp(3, Timestamp.valueOf(session.getSessionDate()));
            statement.setInt(4, session.getCapacity());
            statement.setInt(5, session.getAvailableSlots());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metoda pentru a verifica validitatea trainer_id
    private boolean isTrainerIdValid(int trainerId) {
        if (trainerId <= 0) {
            return false;
        }
        String query = "SELECT COUNT(*) FROM users WHERE id = ? AND role = 'trainer'";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, trainerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<Integer> getTrainerIds() {
        List<Integer> trainerIds = new ArrayList<>();
        String query = "SELECT id FROM users WHERE role = 'trainer'";

        try (Connection connection = DatabaseConfig.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                trainerIds.add(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trainerIds;
    }

    public boolean updateSession(TrainingSession session) {
        String query = "UPDATE training_sessions SET trainer_id = ?, session_type = ?, session_date = ?, capacity = ?, available_slots = ? WHERE session_id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, session.getTrainerId());
            preparedStatement.setString(2, session.getSessionType());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(session.getSessionDate()));
            preparedStatement.setInt(4, session.getCapacity());
            preparedStatement.setInt(5, session.getAvailableSlots());
            preparedStatement.setInt(6, session.getSessionId());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSession(int sessionId) {
        String query = "DELETE FROM training_sessions WHERE session_id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, sessionId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<TrainingSession> getSessionsByTrainer(int trainerId, int clientId) {
        List<TrainingSession> sessions = new ArrayList<>();
        String query =
                "SELECT ts.*, " +
                        "       CASE WHEN EXISTS (" +
                        "           SELECT 1 FROM enrollments e " +
                        "           WHERE e.session_id = ts.session_id AND e.client_id = ?" +
                        "       ) THEN TRUE ELSE FALSE END AS enrolled " +
                        "FROM training_sessions ts " +
                        "WHERE ts.trainer_id = ? AND ts.session_date > NOW()";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, clientId);
            statement.setInt(2, trainerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    TrainingSession session = new TrainingSession(
                            resultSet.getInt("session_id"),
                            resultSet.getInt("trainer_id"),
                            resultSet.getString("session_type"),
                            resultSet.getTimestamp("session_date").toLocalDateTime(),
                            resultSet.getInt("capacity"),
                            resultSet.getInt("available_slots"),
                            resultSet.getBoolean("enrolled")
                    );
                    sessions.add(session);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public List<TrainingSession> getTrainingSessionsByTrainerId(int trainerId) {
        List<TrainingSession> sessions = new ArrayList<>();
        String query = "SELECT session_id, session_type, session_date, capacity, available_slots " +
                "FROM training_sessions " +
                "WHERE trainer_id = ? AND session_date > NOW() " +
                "ORDER BY session_date ASC";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, trainerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    TrainingSession session = new TrainingSession(
                            resultSet.getInt("session_id"),
                            trainerId,
                            resultSet.getString("session_type"),
                            resultSet.getTimestamp("session_date").toLocalDateTime(),
                            resultSet.getInt("capacity"),
                            resultSet.getInt("available_slots")
                    );
                    sessions.add(session);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }
}
