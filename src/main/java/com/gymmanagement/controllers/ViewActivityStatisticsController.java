package com.gymmanagement.controllers;

import com.gymmanagement.dao.ActivityLogDAO;
import com.gymmanagement.models.ActivityLog;
import com.gymmanagement.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ViewActivityStatisticsController {

  @FXML
  private TableView<ActivityLog> activityTable;

  @FXML
  private TableColumn<ActivityLog, LocalDate> logDateColumn;

  @FXML
  private TableColumn<ActivityLog, Integer> durationMinutesColumn;

  @FXML
  private TableColumn<ActivityLog, String> activityTypeColumn;

  @FXML
  private Label totalSessionsLabel;

  @FXML
  private Label totalDurationLabel;

  @FXML
  private Label averageDurationLabel;

  @FXML
  private BarChart<String, Number> activityChart;

  private ActivityLogDAO activityLogDAO;

  public void setActivityLogDAO(ActivityLogDAO activityLogDAO) {
    this.activityLogDAO = activityLogDAO;
  }

  @FXML
  public void initialize() {
    if (activityLogDAO == null) {
      activityLogDAO = new ActivityLogDAO();
    }

    // Configure columns for the table
    logDateColumn.setCellValueFactory(new PropertyValueFactory<>("logDate"));
    durationMinutesColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
    activityTypeColumn.setCellValueFactory(new PropertyValueFactory<>("activityType"));

    try {
      loadActivityLogs();
      loadActivityStatistics();
    } catch (SQLException e) {
      showAlert("Error", "Failed to load activity data. Please try again.");
      e.printStackTrace();
    }
  }


  private void loadActivityLogs() throws SQLException {
    int clientId = SessionManager.getCurrentUser().getId();
    List<ActivityLog> logs = activityLogDAO.getActivityLogsByClientId(clientId);
    ObservableList<ActivityLog> observableLogs = FXCollections.observableArrayList(logs);
    activityTable.setItems(observableLogs);
  }

  private void loadActivityStatistics() throws SQLException {
    int clientId = SessionManager.getCurrentUser().getId();
    int totalSessions = activityLogDAO.getTotalSessionsLastMonth(clientId);
    int totalDuration = activityLogDAO.getTotalDurationLastMonth(clientId);
    double averageDuration = totalSessions > 0 ? (double) totalDuration / totalSessions : 0;

    totalSessionsLabel.setText("Total Sessions: " + totalSessions);
    totalDurationLabel.setText("Total Duration: " + totalDuration + " minutes");
    averageDurationLabel.setText("Average Duration: " + String.format("%.2f", averageDuration) + " minutes");

    loadActivityChart(clientId);
  }

  private void loadActivityChart(int clientId) throws SQLException {
    List<ActivityLog> logs = activityLogDAO.getActivityLogsByClientId(clientId);
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Activity Last Month");

    for (ActivityLog log : logs) {
      if (log.getLogDate().isAfter(LocalDate.now().minusMonths(1))) {
        series.getData().add(new XYChart.Data<>(log.getLogDate().toString(), log.getDurationMinutes()));
      }
    }

    activityChart.getData().clear();
    activityChart.getData().add(series);
  }

  @FXML
  private void handleAddActivity() {
    Dialog<ActivityLog> dialog = new Dialog<>();
    dialog.setTitle("Add Activity Log");

    Label typeLabel = new Label("Activity Type:");
    TextField typeField = new TextField();
    Label durationLabel = new Label("Duration (minutes):");
    TextField durationField = new TextField();
    Label dateLabel = new Label("Date:");
    DatePicker datePicker = new DatePicker(LocalDate.now());

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.add(typeLabel, 0, 0);
    grid.add(typeField, 1, 0);
    grid.add(durationLabel, 0, 1);
    grid.add(durationField, 1, 1);
    grid.add(dateLabel, 0, 2);
    grid.add(datePicker, 1, 2);

    dialog.getDialogPane().setContent(grid);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == ButtonType.OK) {
        try {
          String activityType = typeField.getText();
          int duration = Integer.parseInt(durationField.getText());
          LocalDate date = datePicker.getValue();

          if (activityType.isEmpty() || duration <= 0 || date == null) {
            throw new IllegalArgumentException("All fields must be valid.");
          }

          return new ActivityLog(SessionManager.getCurrentUser().getId(), activityType, duration, date);
        } catch (Exception e) {
          showAlert("Validation Error", "Please enter valid data.");
        }
      }
      return null;
    });

    dialog.showAndWait().ifPresent(activityLog -> {
      try {
        if (activityLogDAO.insertActivityLog(activityLog)) {
          showAlert("Success", "Activity log added successfully.");
          loadActivityLogs();
          loadActivityStatistics();
        } else {
          showAlert("Error", "Failed to add activity log.");
        }
      } catch (SQLException e) {
        showAlert("Error", "An error occurred while adding the activity log.");
        e.printStackTrace();
      }
    });
  }

  private void showAlert(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }
}
