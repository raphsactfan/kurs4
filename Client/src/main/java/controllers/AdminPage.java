package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import main.Utility.ClientSocket;
import main.Models.Entities.User;

public class AdminPage {

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        ClientSocket clientSocket = ClientSocket.getInstance();
        User currentUser = clientSocket.getUser(); // Получаем текущего пользователя
        if (currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole())) {
            welcomeLabel.setText("Здраствуйте, " + currentUser.getLogin() + "!");
        } else {
            // Если пользователь не админ, перенаправляем на страницу входа
            redirectToLogin();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Очищаем данные текущего пользователя
            ClientSocket.getInstance().setUser(null);
            navigateTo(event, "/login.fxml", "Авторизация");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openUserManagement(ActionEvent event) {
        navigateTo(event, "/UserManagement.fxml", "Управление пользователями");
    }

    @FXML
    private void openSupplierManagement(ActionEvent event) {
        navigateTo(event, "/SupplierManagement.fxml", "Управление поставщиками");
    }

    @FXML
    private void openAddressManagement(ActionEvent event) {
        navigateTo(event, "/AddressManagement.fxml", "Управление адресами");
    }

    @FXML
    private void openCategoryManagement(ActionEvent event) {
        navigateTo(event, "/CategoryManagement.fxml", "Управление категориями");
    }

    @FXML
    private void openProductManagement(ActionEvent event) {
        navigateTo(event, "/ProductManagement.fxml", "Управление товарами");
    }

    @FXML
    private void openOrderManagement(ActionEvent event) {
        navigateTo(event, "/OrderManagement.fxml", "Управление заказами");
    }

    @FXML
    private void openStatisticsSelection(ActionEvent event) {
        navigateTo(event, "/StatisticsSelection.fxml", "Просмотр статистики");
    }

    private void navigateTo(ActionEvent event, String fxmlPath, String title) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Не удалось открыть страницу: " + title + " (" + e.getMessage() + ")");
        }
    }

    private void redirectToLogin() {
        try {
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Авторизация");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
