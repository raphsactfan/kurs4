package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import main.Utility.ClientSocket;
import main.Models.Entities.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.PasswordUtils;

public class UserProfile {

    @FXML
    private Label usernameLabel;
    @FXML
    private Label totalAmountLabel;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    public void initialize() {
        ClientSocket clientSocket = ClientSocket.getInstance();
        User currentUser = clientSocket.getUser();

        if (currentUser != null) {
            usernameLabel.setText("Логин: " + currentUser.getLogin());
            loadTotalOrderAmount(currentUser.getId());
        } else {
            redirectToLogin();
        }
    }

    private void loadTotalOrderAmount(int userId) {
        try {
            ClientSocket clientSocket = ClientSocket.getInstance();

            // Создаем запрос на получение суммы заказов
            Request request = new Request();
            request.setCommand("GET_TOTAL_ORDER_AMOUNT");
            request.setData(String.valueOf(userId));

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                // Обновляем текст с суммой заказов
                totalAmountLabel.setText("Сумма заказов: " + response.getMessage() + " руб.");
            } else {
                totalAmountLabel.setText("Сумма заказов: Ошибка загрузки");
            }
        } catch (Exception e) {
            totalAmountLabel.setText("Сумма заказов: Ошибка подключения");
            e.printStackTrace();
        }
    }

    @FXML
    private void changePassword(ActionEvent event) {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Поля пароля не должны быть пустыми");
            return;
        }

        if (newPassword.length() < 5) {
            showError("Пароль должен содержать не менее 5 символов");
            return;
        }

        if (newPassword.contains(" ")) {
            showError("Пароль не должен содержать пробелов");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Пароли не совпадают");
            return;
        }

        try {
            ClientSocket clientSocket = ClientSocket.getInstance();
            User currentUser = clientSocket.getUser();

            if (currentUser.getRole() == null || currentUser.getRole().isEmpty()) {
                currentUser.setRole("user");
            }

            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            currentUser.setPassword(hashedPassword);

            Request request = new Request();
            request.setCommand("EDIT_USER");
            request.setData(clientSocket.getGson().toJson(currentUser));

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if ("SUCCESS".equals(response.getStatus())) {
                showInfo("Пароль успешно изменен!");
                goBack(event);
            } else {
                showError("Ошибка изменения пароля: " + response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Не удалось изменить пароль: " + e.getMessage());
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/MainPage.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Главная страница");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Не удалось вернуться на главную страницу: " + e.getMessage());
        }
    }

    private void redirectToLogin() {
        try {
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
