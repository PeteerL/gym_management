package com.gymmanagement.dao;

import com.gymmanagement.config.DatabaseConfig;
import com.gymmanagement.models.Subscription;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionDAO {

    public List<Subscription> getAllSubscriptions() {
        String query = "SELECT s.subscription_id, u.name AS client_name, s.subscription_type, s.start_date, s.end_date, s.is_active " +
                "FROM subscriptions s INNER JOIN users u ON s.client_id = u.id";
        List<Subscription> subscriptions = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                subscriptions.add(new Subscription(
                        rs.getInt("subscription_id"),
                        rs.getString("client_name"), // Numele clientului din baza de date
                        rs.getString("subscription_type"),
                        rs.getDate("start_date").toLocalDate(), // Conversie java.sql.Date -> LocalDate
                        rs.getDate("end_date").toLocalDate(),
                        rs.getBoolean("is_active")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subscriptions;
    }

    public boolean addSubscription(Subscription subscription) {
        String query = "INSERT INTO subscriptions (client_id, subscription_type, start_date, end_date, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Obține ID-ul clientului pe baza numelui clientului
            int clientId = getClientIdByName(subscription.getClientName(), connection);
            if (clientId == -1) {
                System.err.println("Client not found: " + subscription.getClientName());
                return false;
            }

            stmt.setInt(1, clientId);
            stmt.setString(2, subscription.getSubscriptionType());
            stmt.setDate(3, Date.valueOf(subscription.getStartDate()));
            stmt.setDate(4, Date.valueOf(subscription.getEndDate()));
            stmt.setBoolean(5, subscription.isActive());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSubscription(Subscription subscription) {
        String query = "UPDATE subscriptions SET subscription_type = ?, start_date = ?, end_date = ?, is_active = ? WHERE subscription_id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, subscription.getSubscriptionType());
            stmt.setDate(2, Date.valueOf(subscription.getStartDate()));
            stmt.setDate(3, Date.valueOf(subscription.getEndDate()));
            stmt.setBoolean(4, subscription.isActive());
            stmt.setInt(5, subscription.getSubscriptionId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int getClientIdByName(String clientName, Connection connection) {
        String query = "SELECT id FROM users WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, clientName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Dacă clientul nu este găsit
    }

    public boolean deleteSubscription(int subscriptionId) {
        String query = "DELETE FROM subscriptions WHERE subscription_id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, subscriptionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Subscription getSubscriptionByClientId(int clientId) {
        String query = "SELECT s.subscription_id, u.name AS client_name, s.subscription_type, s.start_date, s.end_date, s.is_active " +
                "FROM subscriptions s INNER JOIN users u ON s.client_id = u.id WHERE s.client_id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Subscription(
                            rs.getInt("subscription_id"),
                            rs.getString("client_name"),
                            rs.getString("subscription_type"),
                            rs.getDate("start_date").toLocalDate(),
                            rs.getDate("end_date").toLocalDate(),
                            rs.getBoolean("is_active")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Dacă nu există abonament
    }

}
