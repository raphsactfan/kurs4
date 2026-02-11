package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Models.Entities.User;
import main.Utility.ClientSocket;
import main.Utility.PasswordUtils;
import main.Models.TCP.Request;
import main.Models.TCP.Response;

import java.io.IOException;

public class UserManagement {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> loginColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private VBox editSection;

    @FXML
    private PasswordField editPasswordField;

    @FXML
    private TextField roleField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private TextField searchField;

    private User selectedUserForEdit;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        roleComboBox.setItems(FXCollections.observableArrayList("user", "admin"));

        loadUsers();
    }

    private void loadUsers() {
        try {
            userTable.getItems().clear();

            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("LOAD_USERS");
            request.setData("");

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                String jsonData = response.getMessage();
                User[] users = clientSocket.getGson().fromJson(jsonData, User[].class);

                userList.clear();
                userList.addAll(users);
                userTable.setItems(userList); // Заполняем таблицу данными
            } else {
                showError(response != null ? response.getMessage() : "Не удалось загрузить пользователей");
            }
        } catch (Exception e) {
            showError("Ошибка загрузки пользователей: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddUser() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue(); // Получаем выбранную роль

        if (login.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Error", "Заполните все поля.");
            return;
        }

        if (login.length() < 5 || password.length() < 5) {
            showAlert("Error", "Логин и пароль должны быть длиннее 5 символов");
            return;
        }

        if (login.contains(" ") || password.contains(" ")) {
            showAlert("Error", "Логин и пароль не должны содержать пробелы");
            return;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);

        try {
            User newUser = new User();
            newUser.setLogin(login);
            newUser.setPassword(hashedPassword);
            newUser.setRole(role); // Установка роли из ComboBox

            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("ADD_USER");
            request.setData(clientSocket.getGson().toJson(newUser));

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                showAlert("Success", "Пользователь добавлен");
                loginField.clear();
                passwordField.clear();
                roleComboBox.getSelectionModel().clearSelection(); // Очистка выбора роли
                loadUsers();
            } else {
                showAlert("Error", response != null ? response.getMessage() : "Не удалось добавить пользователя");
            }
        } catch (Exception e) {
            showAlert("Error", "Ошибка добавления пользователя");
            e.printStackTrace();
        }
    }



    @FXML
    private void handleEditUser() {
        selectedUserForEdit = userTable.getSelectionModel().getSelectedItem();
        if (selectedUserForEdit == null) {
            showAlert("Error", "Выберите пользователя для редактирования");
            return;
        }

        editPasswordField.setText("");
        editSection.setVisible(true);
        editSection.setManaged(true);
    }

    @FXML
    private void handleSaveEdit() {
        if (selectedUserForEdit == null) {
            showAlert("Error", "Нет выбранного пользователя для редактирования");
            return;
        }

        String newPassword = editPasswordField.getText();

        if (newPassword.isEmpty()) {
            showAlert("Error", "Пароль не может быть пустым");
            return;
        }

        if (newPassword.length() < 5) {
            showAlert("Error", "Пароль должен быть длиннее 5 символов");
            return;
        }

        if (newPassword.contains(" ")) {
            showAlert("Error", "Пароль не должен содержать пробелы");
            return;
        }

        String hashedPassword = PasswordUtils.hashPassword(newPassword);

        try {
            // Обновляем пароль
            selectedUserForEdit.setPassword(hashedPassword);

            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("EDIT_USER");
            request.setData(clientSocket.getGson().toJson(selectedUserForEdit));

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                showAlert("Success", "Пароль успешно обновлён");
                loadUsers();
                handleCancelEdit();
            } else {
                showAlert("Error", response != null ? response.getMessage() : "Ошибка обновления пользователя");
            }
        } catch (Exception e) {
            showAlert("Error", "Не удалось обновить пользователя");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelEdit() {
        editSection.setVisible(false);
        editSection.setManaged(false);
        selectedUserForEdit = null;
    }

    @FXML
    private void handleDeleteUser() {
        // Получаем выбранного пользователя
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Выберите пользователя для удаления");
            return;
        }

        // Проверяем, пытается ли администратор удалить самого себя
        ClientSocket clientSocket = ClientSocket.getInstance();
        User currentUser = clientSocket.getUser(); // Текущий пользователь

        if (currentUser != null && selectedUser.getId() == currentUser.getId()) {
            showAlert("Error", "Вы не можете удалить свой собственный аккаунт!");
            return;
        }

        try {
            // Отправляем запрос на удаление
            Request request = new Request();
            request.setCommand("DELETE_USER");
            request.setData(clientSocket.getGson().toJson(selectedUser));

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                showAlert("Success", "Пользователь успешно удалён");
                loadUsers(); // Обновляем таблицу пользователей
            } else {
                showAlert("Error", response != null ? response.getMessage() : "Ошибка удаления пользователя");
            }
        } catch (Exception e) {
            showError("Ошибка удаления пользователя: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            showAlert("Error", "Введите запрос для поиска");
            return;
        }

        ObservableList<User> filteredUsers = FXCollections.observableArrayList();

        for (User user : userList) {
            if (user.getLogin().toLowerCase().contains(query)) {
                filteredUsers.add(user);
            }
        }

        userTable.setItems(filteredUsers);

        if (filteredUsers.isEmpty()) {
            showAlert("Info", "Пользователи не найдены");
        }
    }

    @FXML
    private void clearSearch() {
        searchField.clear(); // Очищаем поле поиска
        userTable.setItems(userList); // Возвращаем исходный список пользователей
    }


    @FXML
    private void goBack(ActionEvent event) {
        try {
            Stage stage = (Stage) userTable.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/adminPage.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Page");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Невозможно загрузить страницу администратора");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
