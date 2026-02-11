package controllers;

import main.Models.Entities.Category;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditCategory {

    @FXML
    private TextField nameField;

    private Category category;
    private boolean saved;

    @FXML
    public void initialize() {
        saved = false; // По умолчанию редактирование не завершено
    }

    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            nameField.setText(category.getName());
        }
    }

    public boolean isSaved() {
        return saved;
    }

    @FXML
    private void handleSave() {
        if (category != null) {
            category.setName(nameField.getText().trim());
        }
        saved = true;
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        saved = false;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
