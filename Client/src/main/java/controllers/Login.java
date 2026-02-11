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

public class Login {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Заполните все поля");
            return;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);

        try {
            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();

            User user = new User();
            user.setLogin(username);
            user.setPassword(hashedPassword);

            request.setCommand("LOGIN");
            request.setData(clientSocket.getGson().toJson(user));

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                User loggedInUser = clientSocket.getGson().fromJson(response.getMessage(), User.class);
                clientSocket.setUser(loggedInUser); // Сохраняем данные пользователя

                if ("admin".equalsIgnoreCase(loggedInUser.getRole())) {
                    showAlert("Success", "Вход выполнен как администратор!");
                    goToAdminPage();
                } else {
                    showAlert("Success", "Вход выполнен как пользователь!");
                    goToMainPage();
                }
            } else {
                showAlert("Error", response != null ? response.getMessage() : "Ошибка авторизации");
            }
        } catch (Exception e) {
            showAlert("Error", "Ошибка соединения");
            e.printStackTrace();
        }
    }


    private void goToAdminPage() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/adminPage.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Page");
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Невозможно загрузить страницу администратора");
            e.printStackTrace();
        }
    }

    private void goToMainPage() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/mainPage.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Main Page");
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Невозможно загрузить главную страницу");
            e.printStackTrace();
        }
    }

    @FXML
    private void goToRegister(ActionEvent event) {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/register.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Регистрация");
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Невозможно загрузить страницу регистрации");
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
