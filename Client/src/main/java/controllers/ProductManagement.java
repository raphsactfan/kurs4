package controllers;

import main.Models.Entities.Product;
import main.Models.Entities.Supplier;
import main.Models.Entities.Category;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.util.StringConverter;
import main.Utility.ClientSocket;
import main.Models.TCP.Request;
import main.Models.TCP.Response;

public class ProductManagement {

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, Integer> quantityColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, String> supplierColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TextField searchField;

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

    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();
    private ObservableList<Category> categoryList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        supplierColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSupplier().getName()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory().getName()));

        loadProducts();
        loadSuppliers();
        loadCategories();

        // Настройка отображения ComboBox
        setupComboBoxConverters();
    }

    private void setupComboBoxConverters() {
        supplierComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Supplier supplier) {
                return supplier != null ? supplier.getName() : "";
            }

            @Override
            public Supplier fromString(String string) {
                return supplierList.stream()
                        .filter(supplier -> supplier.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        categoryComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getName() : "";
            }

            @Override
            public Category fromString(String string) {
                return categoryList.stream()
                        .filter(category -> category.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    private void loadProducts() {
        try {
            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("LOAD_PRODUCTS");

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                Product[] products = clientSocket.getGson().fromJson(response.getMessage(), Product[].class);
                productList.clear();
                productList.addAll(products);
                productTable.setItems(productList);
            } else {
                showError("Не удалось загрузить товары: " + response.getMessage());
            }
        } catch (Exception e) {
            showError("Ошибка загрузки товаров: " + e.getMessage());
        }
    }

    private void loadSuppliers() {
        try {
            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("LOAD_SUPPLIERS");

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                Supplier[] suppliers = clientSocket.getGson().fromJson(response.getMessage(), Supplier[].class);
                supplierList.clear();
                supplierList.addAll(suppliers);
                supplierComboBox.setItems(supplierList);
            } else {
                showError("Не удалось загрузить поставщиков: " + response.getMessage());
            }
        } catch (Exception e) {
            showError("Ошибка загрузки поставщиков: " + e.getMessage());
        }
    }

    private void loadCategories() {
        try {
            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("LOAD_CATEGORIES");

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                Category[] categories = clientSocket.getGson().fromJson(response.getMessage(), Category[].class);
                categoryList.clear();
                categoryList.addAll(categories);
                categoryComboBox.setItems(categoryList);
            } else {
                showError("Не удалось загрузить категории: " + response.getMessage());
            }
        } catch (Exception e) {
            showError("Ошибка загрузки категорий: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        ObservableList<Product> filteredProducts = FXCollections.observableArrayList();
        for (Product product : productList) {
            if (product.getName().toLowerCase().contains(query)) {
                filteredProducts.add(product);
            }
        }
        productTable.setItems(filteredProducts);
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        productTable.setItems(productList);
    }

    @FXML
    private void handleAddProduct() {
        String name = nameField.getText().trim();
        int quantity;
        double price;

        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            price = Double.parseDouble(priceField.getText().trim());

            // Проверка на отрицательные значения
            if (quantity < 0 || price < 0) {
                showError("Количество и цена не могут быть отрицательными");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Введите корректные значения для количества и цены");
            return;
        }

        Supplier supplier = supplierComboBox.getValue();
        Category category = categoryComboBox.getValue();

        if (name.isEmpty() || supplier == null || category == null) {
            showError("Заполните все поля");
            return;
        }

        try {
            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            Product product = new Product();
            product.setName(name);
            product.setQuantity(quantity);
            product.setPrice(price);
            product.setSupplier(supplier);
            product.setCategory(category);

            request.setCommand("ADD_PRODUCT");
            request.setData(clientSocket.getGson().toJson(product));
            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                showAlert("Успех", "Товар добавлен");
                loadProducts();
                clearFields();
            } else {
                showError("Ошибка добавления товара: " + response.getMessage());
            }
        } catch (Exception e) {
            showError("Ошибка добавления товара: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showError("Выберите товар для редактирования");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProduct.fxml"));
            Parent root = loader.load();

            EditProduct controller = loader.getController();
            controller.setProduct(selectedProduct, supplierList, categoryList);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Редактирование товара");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controller.isSaveClicked()) {
                Product updatedProduct = controller.getProduct();

                // Проверка данных после редактирования
                if (updatedProduct.getName().trim().isEmpty()) {
                    showError("Название товара не может быть пустым");
                    return;
                }
                if (updatedProduct.getQuantity() < 0) {
                    showError("Количество товара не может быть отрицательным");
                    return;
                }
                if (updatedProduct.getPrice() < 0) {
                    showError("Цена товара не может быть отрицательной");
                    return;
                }
                if (updatedProduct.getSupplier() == null) {
                    showError("Выберите поставщика для товара");
                    return;
                }
                if (updatedProduct.getCategory() == null) {
                    showError("Выберите категорию для товара");
                    return;
                }

                updateProduct(updatedProduct);
                loadProducts();
            }
        } catch (Exception e) {
            showError("Ошибка открытия окна редактирования: " + e.getMessage());
        }
    }

    private void updateProduct(Product product) {
        // Проверка на отрицательные значения
        if (product.getQuantity() < 0 || product.getPrice() < 0) {
            showError("Количество и цена не могут быть отрицательными");
            return;
        }

        try {
            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("EDIT_PRODUCT");
            request.setData(clientSocket.getGson().toJson(product));

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                showAlert("Успех", "Товар обновлен");
            } else {
                showError("Ошибка обновления товара: " + response.getMessage());
            }
        } catch (Exception e) {
            showError("Ошибка обновления товара: " + e.getMessage());
        }
    }


    @FXML
    private void handleDeleteProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showError("Выберите товар для удаления");
            return;
        }

        try {
            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("DELETE_PRODUCT");
            request.setData(clientSocket.getGson().toJson(selectedProduct));

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                showAlert("Успех", "Товар удален");
                loadProducts();
            } else {
                showError("Ошибка удаления товара: " + response.getMessage());
            }
        } catch (Exception e) {
            showError("Ошибка удаления товара: " + e.getMessage());
        }
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) productTable.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/adminPage.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Page");
            stage.show();
        } catch (Exception e) {
            showError("Ошибка возврата на страницу администратора");
        }
    }

    private void clearFields() {
        nameField.clear();
        quantityField.clear();
        priceField.clear();
        supplierComboBox.getSelectionModel().clearSelection(); //убрать
        categoryComboBox.getSelectionModel().clearSelection(); //убрать
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
