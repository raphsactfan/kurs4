package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.Models.Entities.User;
import main.Utility.ClientSocket;
import main.Utility.PasswordUtils;
import main.Models.TCP.Request;
import main.Models.TCP.Response;

public class Register {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Проверка на заполнение всех полей
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Заполните все поля");
            return;
        }

        // Проверка длины логина и пароля
        if (username.length() < 5 || password.length() < 5) {
            showAlert("Error", "Логин и пароль должны содержать минимум 5 символов");
            return;
        }

        // Проверка на наличие пробелов в логине и пароле
        if (username.contains(" ") || password.contains(" ")) {
            showAlert("Error", "Логин и пароль не должны содержать пробелы");
            return;
        }

        // Проверка совпадения пароля и подтверждения пароля
        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Пароли не совпадают");
            return;
        }

        // Хэширование пароля
        String hashedPassword = PasswordUtils.hashPassword(password);

        // Создание нового пользователя
        User newUser = new User();
        newUser.setLogin(username);
        newUser.setPassword(hashedPassword);

        // Отправка данных пользователя на сервер
        try {
            ClientSocket clientSocket = ClientSocket.getInstance();

            Request request = new Request();
            request.setCommand("REGISTER");
            request.setData(clientSocket.getGson().toJson(newUser));

            clientSocket.sendRequest(request);

            Response response = clientSocket.receiveResponse();
            if (response != null && "SUCCESS".equals(response.getStatus())) {
                showAlert("Success", "Регистрация прошла успешно!");
                goToLogin(event);
            } else {
                showAlert("Error", response != null ? response.getMessage() : "Логин уже занят");
            }
        } catch (Exception e) {
            showAlert("Error", "Ошибка соединения");
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Авторизация");
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Невозможно загрузить страницу");
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
}
