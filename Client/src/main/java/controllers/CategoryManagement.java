package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Models.Entities.Category;
import main.Utility.ClientSocket;
import main.Models.TCP.Request;
import main.Models.TCP.Response;

public class CategoryManagement {

    @FXML
    private TableView<Category> categoryTable;

    @FXML
    private TableColumn<Category, Integer> idColumn;

    @FXML
    private TableColumn<Category, String> nameColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField searchField;

    private ObservableList<Category> categoryList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Настройка столбцов таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Загрузка данных
        loadCategories();
    }

    private void loadCategories() {
        try {
            categoryTable.getItems().clear();

            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("LOAD_CATEGORIES");

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                Category[] categories = clientSocket.getGson().fromJson(response.getMessage(), Category[].class);

                categoryList.clear();
                categoryList.addAll(categories);
                categoryTable.setItems(categoryList);
            } else {
                showError(response != null ? response.getMessage() : "Не удалось загрузить категории");
            }
        } catch (Exception e) {
            showError("Ошибка загрузки категорий: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddCategory() {
        String categoryName = nameField.getText().trim();

        if (categoryName.isEmpty()) {
            showError("Название категории не может быть пустым");
            return;
        }

        try {
            Category newCategory = new Category();
            newCategory.setName(categoryName);

            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("ADD_CATEGORY");
            request.setData(clientSocket.getGson().toJson(newCategory));

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if ("SUCCESS".equals(response.getStatus())) {
                showAlert("Успех", "Категория успешно добавлена");
                nameField.clear(); // Очищаем поле ввода
                loadCategories(); // Обновляем таблицу категорий
            } else {
                showAlert("Ошибка", response.getMessage());
            }
        } catch (Exception e) {
            showError("Ошибка добавления категории: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditCategory() {
        Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showAlert("Ошибка", "Выберите категорию для редактирования");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditCategory.fxml"));
            Parent root = loader.load();

            EditCategory controller = loader.getController();
            controller.setCategory(selectedCategory);

            Stage stage = new Stage();
            stage.setTitle("Редактирование категории");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controller.isSaved()) {
                // Проверяем, что имя категории не пустое
                if (selectedCategory.getName().trim().isEmpty()) {
                    showAlert("Ошибка", "Название категории не может быть пустым");
                    return;
                }

                // Отправляем запрос на сервер для обновления категории
                ClientSocket clientSocket = ClientSocket.getInstance();
                Request request = new Request();
                request.setCommand("EDIT_CATEGORY");
                request.setData(clientSocket.getGson().toJson(selectedCategory));

                clientSocket.sendRequest(request);
                Response response = clientSocket.receiveResponse();

                if ("SUCCESS".equals(response.getStatus())) {
                    showAlert("Успех", "Категория успешно обновлена");
                    loadCategories();
                } else {
                    showAlert("Ошибка", response.getMessage());
                }
            }
        } catch (Exception e) {
            showError("Ошибка открытия окна редактирования: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteCategory() {
        Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            showAlert("Error", "Выберите категорию для удаления");
            return;
        }

        try {
            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("DELETE_CATEGORY");
            request.setData(clientSocket.getGson().toJson(selectedCategory));

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                showAlert("Success", "Категория удалена");
                loadCategories();
            } else {
                showError(response != null ? response.getMessage() : "Ошибка удаления категории");
            }
        } catch (Exception e) {
            showError("Ошибка удаления категории: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();

        ObservableList<Category> filteredCategories = FXCollections.observableArrayList();
        for (Category category : categoryList) {
            if (category.getName().toLowerCase().contains(query)) {
                filteredCategories.add(category);
            }
        }

        categoryTable.setItems(filteredCategories);

        if (filteredCategories.isEmpty()) {
            showAlert("Info", "Категории не найдены");
        }
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        categoryTable.setItems(categoryList);
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) categoryTable.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/adminPage.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Page");
            stage.show();
        } catch (Exception e) {
            showError("Ошибка возврата на страницу администратора");
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
