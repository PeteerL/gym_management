package com.gymmanagement.controllers;

import com.gymmanagement.dao.UserDAO;
import com.gymmanagement.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.List;

public class ManageUsersController {

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        // Set up the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Load users into the table
        loadUsers();
    }

    private void loadUsers() {
        List<User> users = userDAO.getAllUsers(); // Obține utilizatorii din baza de date
        ObservableList<User> observableUsers = FXCollections.observableArrayList(users);
        usersTable.setItems(observableUsers);
    }


    @FXML
    private void handleAddUser() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add User");
        dialog.setHeaderText("Enter the details of the new user");

        // Configurare câmpuri pentru introducerea datelor
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Label roleLabel = new Label("Role:");
        ChoiceBox<String> roleChoiceBox = new ChoiceBox<>();
        roleChoiceBox.getItems().addAll("client", "trainer");

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(roleLabel, 0, 3);
        grid.add(roleChoiceBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Butoane OK și Cancel
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Convertim rezultatul dialogului
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                // Validare
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText().trim();
                String role = roleChoiceBox.getValue();

                // Validări
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
                    showAlert("Validation Error", "All fields are required.");
                    return null;
                }
                if (!isValidNameForAdd(name)) {
                    showAlert("Validation Error", "Name must start with a capital letter.");
                    return null;
                }
                if (!isValidEmailForAdd(email)) {
                    showAlert("Validation Error", "Invalid email format.");
                    return null;
                }


                // Dacă trece validarea, returnăm utilizatorul
                return new User(0, name, email, password, role);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            boolean success = userDAO.addUser(user); // Apelăm metoda addUser
            if (success) {
                showAlert("Success", "User added successfully.");
                loadUsers(); // Reîncărcăm lista de utilizatori în tabel
            } else {
                showAlert("Error", "Failed to add user. Please try again.");
            }
        });
    }

    // Metodă pentru validarea numelui în Add User
    private boolean isValidNameForAdd(String name) {
        return name.matches("^[A-Z][a-zA-Z ]*$");
    }

    // Metodă pentru validarea emailului în Add User
    private boolean isValidEmailForAdd(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }



    @FXML
    private void handleEditUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlert("Error", "Please select a user to edit.");
            return;
        }

        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit the details of the selected user");

        // Configurare câmpuri pentru editarea datelor
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField(selectedUser.getName());
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField(selectedUser.getEmail());
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Leave blank to keep current password");
        Label roleLabel = new Label("Role:");
        ChoiceBox<String> roleChoiceBox = new ChoiceBox<>();
        roleChoiceBox.getItems().addAll("client", "trainer");
        roleChoiceBox.setValue(selectedUser.getRole());

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(roleLabel, 0, 3);
        grid.add(roleChoiceBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Butoane OK și Cancel
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Convertim rezultatul dialogului
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText().trim();
                String role = roleChoiceBox.getValue();

                // Validări
                if (name.isEmpty() || email.isEmpty() || role == null) {
                    showAlert("Validation Error", "All fields except password are required.");
                    return null;
                }
                if (!isValidNameForEdit(name)) {
                    showAlert("Validation Error", "Name must start with a capital letter.");
                    return null;
                }
                if (!isValidEmailForEdit(email)) {
                    showAlert("Validation Error", "Invalid email format.");
                    return null;
                }


                // Actualizăm obiectul utilizator
                selectedUser.setName(name);
                selectedUser.setEmail(email);
                selectedUser.setRole(role);
                if (!password.isEmpty()) {
                    selectedUser.setPassword(password); // Schimbăm parola doar dacă este completată
                }
                return selectedUser;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            boolean success = userDAO.updateUser(user); // Apelăm metoda updateUser
            if (success) {
                showAlert("Success", "User updated successfully.");
                loadUsers(); // Reîncărcăm lista de utilizatori în tabel
            } else {
                showAlert("Error", "Failed to update user. Please try again.");
            }
        });
    }

    // Metodă pentru validarea numelui în Edit User
    private boolean isValidNameForEdit(String name) {
        return name.matches("^[A-Z][a-zA-Z ]*$");
    }

    // Metodă pentru validarea emailului în Edit User
    private boolean isValidEmailForEdit(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }




    @FXML
    private void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Delete User");
            confirmationAlert.setHeaderText(null);
            confirmationAlert.setContentText("Are you sure you want to delete user: " + selectedUser.getName() + "?");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean success = userDAO.deleteUser(selectedUser.getId()); // Delete user from database
                    if (success) {
                        showAlert("Delete User", "User deleted successfully.");
                        loadUsers();
                    } else {
                        showAlert("Error", "Failed to delete user.");
                    }
                }
            });
        } else {
            showAlert("Error", "Please select a user to delete.");
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
