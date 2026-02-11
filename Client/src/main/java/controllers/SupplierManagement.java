package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Models.Entities.Address;
import main.Models.Entities.Supplier;
import main.Utility.ClientSocket;
import main.Models.TCP.Request;
import main.Models.TCP.Response;

import java.util.ArrayList;
import java.util.List;

public class SupplierManagement {

    @FXML
    private TableView<Supplier> supplierTable;

    @FXML
    private TableColumn<Supplier, Integer> idColumn;

    @FXML
    private TableColumn<Supplier, String> nameColumn;

    @FXML
    private TableColumn<Supplier, String> representativeColumn;

    @FXML
    private TableColumn<Supplier, String> phoneColumn;

    @FXML
    private TableColumn<Supplier, String> addressColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField representativeField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField countryField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField streetField;

    @FXML
    private TextField searchField;

    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        representativeColumn.setCellValueFactory(new PropertyValueFactory<>("representative"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatAddress(cellData.getValue().getAddress()))
        );

        loadSuppliers();
    }

    private void loadSuppliers() {
        try {
            supplierTable.getItems().clear();

            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("LOAD_SUPPLIERS");

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                Supplier[] suppliers = clientSocket.getGson().fromJson(response.getMessage(), Supplier[].class);

                supplierList.clear();
                supplierList.addAll(suppliers);
                supplierTable.setItems(supplierList);
            } else {
                showError(response != null ? response.getMessage() : "Не удалось загрузить поставщиков");
            }
        } catch (Exception e) {
            showError("Ошибка загрузки поставщиков: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddSupplier() {

        if (nameField.getText().trim().isEmpty()) {
            showError("Поле 'Название' не должно быть пустым.");
            return;
        }
        if (representativeField.getText().trim().isEmpty()) {
            showError("Поле 'Представитель' не должно быть пустым.");
            return;
        }
        if (phoneField.getText().trim().isEmpty()) {
            showError("Поле 'Телефон' не должно быть пустым.");
            return;
        }
        if (countryField.getText().trim().isEmpty()) {
            showError("Поле 'Страна' не должно быть пустым.");
            return;
        }
        if (cityField.getText().trim().isEmpty()) {
            showError("Поле 'Город' не должно быть пустым.");
            return;
        }
        if (streetField.getText().trim().isEmpty()) {
            showError("Поле 'Улица' не должно быть пустым.");
            return;
        }

        try {
            // Создание нового поставщика
            Supplier newSupplier = new Supplier();
            Address address = new Address();

            address.setCountry(countryField.getText().trim());
            address.setCity(cityField.getText().trim());
            address.setStreet(streetField.getText().trim());

            newSupplier.setName(nameField.getText().trim());
            newSupplier.setRepresentative(representativeField.getText().trim());
            newSupplier.setPhone(phoneField.getText().trim());
            newSupplier.setAddress(address);

            // Отправка запроса на сервер
            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("ADD_SUPPLIER");
            request.setData(clientSocket.getGson().toJson(newSupplier));

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                showAlert("Успех", "Поставщик добавлен");
                clearFields();
                loadSuppliers();
            } else {
                showAlert("Ошибка", response != null ? response.getMessage() : "Ошибка добавления поставщика");
            }
        } catch (Exception e) {
            showError("Ошибка добавления поставщика: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleEditSupplier() {
        Supplier selectedSupplier = supplierTable.getSelectionModel().getSelectedItem();
        if (selectedSupplier == null) {
            showAlert("Ошибка", "Выберите поставщика для редактирования");
            return;
        }

        try {
            // Загрузка окна редактирования
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditSupplier.fxml"));
            Parent root = loader.load();

            // Настройка контроллера
            EditSupplier controller = loader.getController();
            controller.setSupplier(selectedSupplier);

            // Открытие модального окна
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Редактирование поставщика");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Проверка, были ли сохранены изменения
            if (controller.isSaveClicked()) {
                Supplier updatedSupplier = controller.getSupplier();


                if (updatedSupplier.getName().trim().isEmpty()) {
                    showAlert("Ошибка", "Поле 'Название' не должно быть пустым.");
                    return;
                }
                if (updatedSupplier.getRepresentative().trim().isEmpty()) {
                    showAlert("Ошибка", "Поле 'Представитель' не должно быть пустым.");
                    return;
                }
                if (updatedSupplier.getPhone().trim().isEmpty()) {
                    showAlert("Ошибка", "Поле 'Телефон' не должно быть пустым.");
                    return;
                }
                if (updatedSupplier.getAddress().getCountry().trim().isEmpty()) {
                    showAlert("Ошибка", "Поле 'Страна' не должно быть пустым.");
                    return;
                }
                if (updatedSupplier.getAddress().getCity().trim().isEmpty()) {
                    showAlert("Ошибка", "Поле 'Город' не должно быть пустым.");
                    return;
                }
                if (updatedSupplier.getAddress().getStreet().trim().isEmpty()) {
                    showAlert("Ошибка", "Поле 'Улица' не должно быть пустым.");
                    return;
                }


                updateSupplier(updatedSupplier);
                loadSuppliers(); // Обновление таблицы
            }
        } catch (Exception e) {
            showError("Ошибка открытия окна редактирования: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateSupplier(Supplier supplier) {
        try {
            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("EDIT_SUPPLIER");
            request.setData(clientSocket.getGson().toJson(supplier));

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                showAlert("Success", "Поставщик обновлён");
            } else {
                showError("Ошибка обновления поставщика: " + (response != null ? response.getMessage() : "Нет ответа от сервера"));
            }
        } catch (Exception e) {
            showError("Ошибка обновления поставщика: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteSupplier() {
        Supplier selectedSupplier = supplierTable.getSelectionModel().getSelectedItem();
        if (selectedSupplier == null) {
            showAlert("Error", "Выберите поставщика для удаления");
            return;
        }

        try {
            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("DELETE_SUPPLIER");
            request.setData(clientSocket.getGson().toJson(selectedSupplier));

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                showAlert("Success", "Поставщик удален");
                loadSuppliers();
            } else {
                showAlert("Error", response != null ? response.getMessage() : "Ошибка удаления поставщика");
            }
        } catch (Exception e) {
            showError("Ошибка удаления поставщика: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();

        ObservableList<Supplier> filteredSuppliers = FXCollections.observableArrayList();
        for (Supplier supplier : supplierList) {
            if (supplier.getName().toLowerCase().contains(query)) {
                filteredSuppliers.add(supplier);
            }
        }

        supplierTable.setItems(filteredSuppliers);

        if (filteredSuppliers.isEmpty()) {
            showAlert("Info", "Поставщики не найдены");
        }
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        supplierTable.setItems(supplierList);
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            Stage stage = (Stage) supplierTable.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/adminPage.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Page");
            stage.show();
        } catch (Exception e) {
            showError("Ошибка возврата на страницу администратора");
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.clear();
        representativeField.clear();
        phoneField.clear();
        countryField.clear();
        cityField.clear();
        streetField.clear();
    }

    private String formatAddress(Address address) {
        return String.format("%s, %s, %s", address.getCountry(), address.getCity(), address.getStreet());
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
