package com.gymmanagement.controllers;

import com.gymmanagement.dao.ActivityLogDAO;
import com.gymmanagement.models.ActivityLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.List;

public class ManageActivityLogController {

    @FXML
    private TableView<ActivityLog> activityLogTable;

    @FXML
    private TableColumn<ActivityLog, Integer> logIdColumn;

    @FXML
    private TableColumn<ActivityLog, Integer> clientIdColumn;

    @FXML
    private TableColumn<ActivityLog, String> activityTypeColumn;

    @FXML
    private TableColumn<ActivityLog, Integer> durationColumn;

    @FXML
    private TableColumn<ActivityLog, LocalDate> logDateColumn;

    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    @FXML
    public void initialize() {
        // Configure table columns
        logIdColumn.setCellValueFactory(new PropertyValueFactory<>("logId"));
        clientIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        activityTypeColumn.setCellValueFactory(new PropertyValueFactory<>("activityType"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        logDateColumn.setCellValueFactory(new PropertyValueFactory<>("logDate"));

        loadActivityLogs();
    }

    @FXML
    private void handleAddActivityLog() {
        ActivityLog newLog = showActivityLogDialog(null);
        if (newLog != null) {
            try {
                if (activityLogDAO.insertActivityLog(newLog)) {
                    showAlert("Success", "Activity log added successfully.");
                    loadActivityLogs();
                } else {
                    showAlert("Error", "Failed to add activity log.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleEditActivityLog() {
        ActivityLog selectedLog = activityLogTable.getSelectionModel().getSelectedItem();
        if (selectedLog == null) {
            showAlert("Error", "Please select an activity log to edit.");
            return;
        }

        ActivityLog updatedLog = showActivityLogDialog(selectedLog);
        if (updatedLog != null) {
            try {
                activityLogDAO.updateActivityLog(updatedLog);
                showAlert("Success", "Activity log updated successfully.");
                loadActivityLogs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDeleteActivityLog() {
        ActivityLog selectedLog = activityLogTable.getSelectionModel().getSelectedItem();
        if (selectedLog == null) {
            showAlert("Error", "Please select an activity log to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this activity log?", ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    activityLogDAO.deleteActivityLog(selectedLog.getLogId());
                    showAlert("Success", "Activity log deleted successfully.");
                    loadActivityLogs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private ActivityLog showActivityLogDialog(ActivityLog log) {
        Dialog<ActivityLog> dialog = new Dialog<>();
        dialog.setTitle(log == null ? "Add Activity Log" : "Edit Activity Log");

        Label clientIdLabel = new Label("Client ID:");
        TextField clientIdField = new TextField(log != null ? String.valueOf(log.getClientId()) : "");
        Label activityTypeLabel = new Label("Activity Type:");
        TextField activityTypeField = new TextField(log != null ? log.getActivityType() : "");
        Label durationLabel = new Label("Duration (minutes):");
        TextField durationField = new TextField(log != null ? String.valueOf(log.getDurationMinutes()) : "");
        Label logDateLabel = new Label("Date:");
        DatePicker logDatePicker = new DatePicker(log != null ? log.getLogDate() : LocalDate.now());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(clientIdLabel, 0, 0);
        grid.add(clientIdField, 1, 0);
        grid.add(activityTypeLabel, 0, 1);
        grid.add(activityTypeField, 1, 1);
        grid.add(durationLabel, 0, 2);
        grid.add(durationField, 1, 2);
        grid.add(logDateLabel, 0, 3);
        grid.add(logDatePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    int clientId = Integer.parseInt(clientIdField.getText());
                    String activityType = activityTypeField.getText();
                    int duration = Integer.parseInt(durationField.getText());
                    LocalDate logDate = logDatePicker.getValue();
                    return new ActivityLog(log == null ? 0 : log.getLogId(), clientId, activityType, duration, logDate);
                } catch (Exception e) {
                    showAlert("Validation Error", "Please provide valid data.");
                }
            }
            return null;
        });

        dialog.showAndWait();
        return dialog.getResult();
    }

    private void loadActivityLogs() {
        try {
            List<ActivityLog> activityLogs = activityLogDAO.getAllActivityLogs(); // Nouă metodă
            ObservableList<ActivityLog> observableLogs = FXCollections.observableArrayList(activityLogs);
            activityLogTable.setItems(observableLogs);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load activity logs.");
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
