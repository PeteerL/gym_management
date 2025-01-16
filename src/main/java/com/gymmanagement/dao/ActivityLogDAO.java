package com.gymmanagement.dao;

import com.gymmanagement.models.ActivityLog;
import com.gymmanagement.config.DatabaseConfig;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO {

    public List<ActivityLog> getActivityLogsByClientId(int clientId) throws SQLException {
        List<ActivityLog> activityLogs = new ArrayList<>();
        String query = "SELECT * FROM activity_logs WHERE client_id = ? ORDER BY activity_date DESC";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, clientId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int logId = resultSet.getInt("log_id");
                String activityType = resultSet.getString("activity_description");
                int durationMinutes = resultSet.getInt("activity_duration");
                LocalDate logDate = resultSet.getDate("activity_date").toLocalDate();

                ActivityLog activityLog = new ActivityLog(logId, clientId, activityType, durationMinutes, logDate);
                activityLogs.add(activityLog);
            }
        }
        return activityLogs;
    }

    public int getTotalSessionsLastMonth(int clientId) throws SQLException {
        String query = "SELECT COUNT(*) AS total_sessions FROM activity_logs WHERE client_id = ? AND activity_date >= ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            stmt.setDate(2, Date.valueOf(LocalDate.now().minusMonths(1)));

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("total_sessions");
            }
        }
        return 0;
    }

    public int getTotalDurationLastMonth(int clientId) throws SQLException {
        String query = "SELECT SUM(activity_duration) AS total_duration FROM activity_logs WHERE client_id = ? AND activity_date >= ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            stmt.setDate(2, Date.valueOf(LocalDate.now().minusMonths(1)));

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("total_duration");
            }
        }
        return 0;
    }

    public List<ActivityLog> getAllActivityLogs() throws SQLException {
        List<ActivityLog> activityLogs = new ArrayList<>();
        String query = "SELECT * FROM activity_logs ORDER BY activity_date DESC";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int logId = resultSet.getInt("log_id");
                int clientId = resultSet.getInt("client_id");
                String activityType = resultSet.getString("activity_description");
                int durationMinutes = resultSet.getInt("activity_duration");
                LocalDate logDate = resultSet.getDate("activity_date").toLocalDate();

                activityLogs.add(new ActivityLog(logId, clientId, activityType, durationMinutes, logDate));
            }
        }
        return activityLogs;
    }


    public boolean insertActivityLog(ActivityLog activityLog) throws SQLException {
        String query = "INSERT INTO activity_logs (client_id, activity_description, activity_duration, activity_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, activityLog.getClientId());
            statement.setString(2, activityLog.getActivityType());
            statement.setInt(3, activityLog.getDurationMinutes());
            statement.setDate(4, Date.valueOf(activityLog.getLogDate()));
            return statement.executeUpdate() > 0;
        }
    }

    public void updateActivityLog(ActivityLog activityLog) throws SQLException {
        String query = "UPDATE activity_logs SET activity_description = ?, activity_duration = ?, activity_date = ? WHERE log_id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, activityLog.getActivityType());
            preparedStatement.setInt(2, activityLog.getDurationMinutes());
            preparedStatement.setDate(3, Date.valueOf(activityLog.getLogDate()));
            preparedStatement.setInt(4, activityLog.getLogId());
            preparedStatement.executeUpdate();
        }
    }

    public void deleteActivityLog(int logId) throws SQLException {
        String query = "DELETE FROM activity_logs WHERE log_id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, logId);
            preparedStatement.executeUpdate();
        }
    }
}
