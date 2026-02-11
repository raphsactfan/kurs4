package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import main.Utility.ClientSocket;
import main.Models.Entities.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;

public class MainPage {

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        // Получение текущего пользователя из ClientSocket
        ClientSocket clientSocket = ClientSocket.getInstance();
        User currentUser = clientSocket.getUser();

        if (currentUser != null) {
            // Установка приветственного сообщения
            welcomeLabel.setText("Здраствуйте, " + currentUser.getLogin() + "!");
        } else {
            // Если пользователь не авторизован, перенаправление на страницу входа
            redirectToLogin();
        }
    }

    @FXML
    private void openUserProfile(ActionEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/UserProfile.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Личный кабинет");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Не удалось открыть личный кабинет: " + e.getMessage());
        }
    }

    @FXML
    private void openCatalog(ActionEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/Catalog.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Каталог");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Не удалось открыть каталог: " + e.getMessage());
        }
    }

    @FXML
    private void openOrders(ActionEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/UserOrders.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Мои заказы");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Не удалось открыть заказы: " + e.getMessage());
        }
    }


    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Очистка данных текущего пользователя
            ClientSocket.getInstance().setUser(null);

            // Перенаправление на страницу входа
            redirectToLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void redirectToLogin() {
        try {
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            System.err.println("Failed to redirect to login page: " + e.getMessage());
        }
    }
}
