package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Models.Entities.Address;
import main.Models.Entities.Supplier;

public class EditSupplier {

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

    private Supplier supplier; // Текущий поставщик
    private boolean saveClicked = false; // для проверки сохранены ли изменения

    // Метод для передачи данных поставщика в контроллер
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;

        // Заполнение полей текущими данными
        nameField.setText(supplier.getName());
        representativeField.setText(supplier.getRepresentative());
        phoneField.setText(supplier.getPhone());

        Address address = supplier.getAddress();
        if (address != null) {
            countryField.setText(address.getCountry());
            cityField.setText(address.getCity());
            streetField.setText(address.getStreet());
        }
    }

    // Метод для возврата изменённого поставщика
    public Supplier getSupplier() {
        return supplier;
    }

    // Метод для проверки, нажал ли пользователь "Сохранить"
    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (supplier != null) {
            supplier.setName(nameField.getText().trim());
            supplier.setRepresentative(representativeField.getText().trim());
            supplier.setPhone(phoneField.getText().trim());

            Address address = supplier.getAddress();
            if (address == null) {
                address = new Address();
            }

            address.setCountry(countryField.getText().trim());
            address.setCity(cityField.getText().trim());
            address.setStreet(streetField.getText().trim());

            supplier.setAddress(address);
            saveClicked = true;

            closeWindow();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        saveClicked = false;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
