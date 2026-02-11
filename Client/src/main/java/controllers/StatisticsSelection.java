package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StatisticsSelection {

    @FXML
    private void openCategoryStatistics(ActionEvent event) {
        navigateTo(event, "/StatisticsCategory.fxml", "Статистика по категориям");
    }

    @FXML
    private void openSupplierStatistics(ActionEvent event) {
        navigateTo(event, "/StatisticsSupplier.fxml", "Статистика по поставщикам");
    }

    @FXML
    private void goBack(ActionEvent event) {
        navigateTo(event, "/adminPage.fxml", "Admin Page");
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
}
