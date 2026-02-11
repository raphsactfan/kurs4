package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.Models.Entities.Address;
import main.Utility.ClientSocket;
import main.Models.TCP.Request;
import main.Models.TCP.Response;

public class AddressManagement {

    @FXML
    private TableView<Address> addressTable;

    @FXML
    private TableColumn<Address, Integer> idColumn;

    @FXML
    private TableColumn<Address, String> countryColumn;

    @FXML
    private TableColumn<Address, String> cityColumn;

    @FXML
    private TableColumn<Address, String> streetColumn;

    @FXML
    private TextField searchField;

    private ObservableList<Address> addressList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Настройка столбцов таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        streetColumn.setCellValueFactory(new PropertyValueFactory<>("street"));

        // Загрузка данных
        loadAddresses();
    }

    private void loadAddresses() {
        try {
            addressTable.getItems().clear();

            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("LOAD_ADDRESSES");

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                Address[] addresses = clientSocket.getGson().fromJson(response.getMessage(), Address[].class);

                addressList.clear();
                addressList.addAll(addresses);
                addressTable.setItems(addressList);
            } else {
                showError(response != null ? response.getMessage() : "Не удалось загрузить адреса");
            }
        } catch (Exception e) {
            showError("Ошибка загрузки адресов: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();

        ObservableList<Address> filteredAddresses = FXCollections.observableArrayList();
        for (Address address : addressList) {
            if (address.getCountry().toLowerCase().contains(query) || address.getCity().toLowerCase().contains(query) || address.getStreet().toLowerCase().contains(query)) {
                filteredAddresses.add(address);
            }
        }

        addressTable.setItems(filteredAddresses);

        if (filteredAddresses.isEmpty()) {
            showAlert("Info", "Адреса не найдены");
        }
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        addressTable.setItems(addressList);
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) addressTable.getScene().getWindow();
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
