package controllers;

import main.Models.Entities.Product;
import main.Models.Entities.Supplier;
import main.Models.Entities.Category;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class EditProduct {

    @FXML
    private TextField nameField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField priceField;

    @FXML
    private ComboBox<Supplier> supplierComboBox;

    @FXML
    private ComboBox<Category> categoryComboBox;

    private Product product;
    private boolean saveClicked = false;

    public void setProduct(Product product, ObservableList<Supplier> suppliers, ObservableList<Category> categories) {
        this.product = product;

        // Установить списки в ComboBox
        supplierComboBox.setItems(suppliers);
        categoryComboBox.setItems(categories);

        // Настроить отображение только названия
        supplierComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Supplier supplier) {
                return supplier != null ? supplier.getName() : "";
            }

            @Override
            public Supplier fromString(String string) {
                return suppliers.stream().filter(supplier -> supplier.getName().equals(string)).findFirst().orElse(null);
            }
        });

        categoryComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getName() : "";
            }

            @Override
            public Category fromString(String string) {
                return categories.stream().filter(category -> category.getName().equals(string)).findFirst().orElse(null);
            }
        });

        // Заполнить поля текущими данными
        nameField.setText(product.getName());
        quantityField.setText(String.valueOf(product.getQuantity()));
        priceField.setText(String.valueOf(product.getPrice()));
        supplierComboBox.setValue(product.getSupplier());
        categoryComboBox.setValue(product.getCategory());
    }

    public Product getProduct() {
        return product;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        product.setName(nameField.getText().trim());
        product.setQuantity(Integer.parseInt(quantityField.getText().trim()));
        product.setPrice(Double.parseDouble(priceField.getText().trim()));
        product.setSupplier(supplierComboBox.getValue());
        product.setCategory(categoryComboBox.getValue());

        saveClicked = true;
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        saveClicked = false;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
