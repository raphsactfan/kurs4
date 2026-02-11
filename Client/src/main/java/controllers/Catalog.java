package controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.Models.Entities.Category;
import main.Models.Entities.Product;
import main.Models.Entities.User;
import main.Utility.ClientSocket;
import main.Models.TCP.Request;
import main.Models.TCP.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Catalog {

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, String> availabilityColumn;

    @FXML
    private TableColumn<Product, HBox> quantityColumn;

    @FXML
    private TextField searchField;

    @FXML
    private TextField minPriceField;

    @FXML
    private TextField maxPriceField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private ObservableList<Category> categoryList = FXCollections.observableArrayList();

    private ObservableList<Product> filteredList = FXCollections.observableArrayList();

    private static final ObservableList<Product> cart = FXCollections.observableArrayList();

    private static final Map<Integer, ObservableList<Product>> userCarts = new HashMap<>();


    @FXML
    public void initialize() {
        configureTableColumns();
        setupCategoryComboBox();
        loadCategories();
        loadProducts();

        // Убедитесь, что у пользователя есть корзина
        ensureUserCart();
    }

    private void ensureUserCart() {
        User currentUser = ClientSocket.getInstance().getUser();
        if (currentUser != null) {
            userCarts.putIfAbsent(currentUser.getId(), FXCollections.observableArrayList());
        }
    }

    private ObservableList<Product> getCurrentUserCart() {
        User currentUser = ClientSocket.getInstance().getUser();
        if (currentUser != null) {
            return userCarts.get(currentUser.getId());
        }
        return FXCollections.observableArrayList();
    }

    private void configureTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        categoryColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory().getName()));
        availabilityColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getQuantity() > 0 ? "В наличии" : "Нет в наличии"
                ));
        quantityColumn.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();

            Spinner<Integer> quantitySpinner = new Spinner<>();
            quantitySpinner.setPrefWidth(80);

            // Если количество товара равно 0, отключаем Spinner и устанавливаем значение 0
            if (product.getQuantity() <= 0) {
                quantitySpinner.setDisable(true);
                quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));
            } else {
                // Иначе создаём Spinner с диапазоном от 1 до количества товара
                SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1, product.getQuantity(), 1
                );
                quantitySpinner.setValueFactory(valueFactory);
            }

            Button buyButton = new Button("Купить");
            buyButton.setDisable(product.getQuantity() <= 0);
            buyButton.setOnAction(event -> handleBuy(product, quantitySpinner.getValue()));

            HBox box = new HBox(10, quantitySpinner, buyButton);
            return new ReadOnlyObjectWrapper<>(box);
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

                // По умолчанию показываем все товары
                filteredList.setAll(productList);
                productTable.setItems(filteredList);
            } else {
                showError("Не удалось загрузить каталог: " + response.getMessage());
            }
        } catch (Exception e) {
            showError("Ошибка загрузки каталога: " + e.getMessage());
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
                categoryComboBox.getItems().add(0, new Category(-1, "Все категории")); // "Все категории"
                categoryComboBox.getSelectionModel().selectFirst();
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

        filteredList.setAll(productList.stream()
                .filter(product -> product.getName().toLowerCase().contains(query))
                .collect(Collectors.toList()));

        filterByCategory(); // Применяем текущий фильтр категории
    }

    @FXML
    private void filterByCategory() {
        Category selectedCategory = categoryComboBox.getValue();
        double minPrice = minPriceField.getText().isEmpty() ? 0.0 : Double.parseDouble(minPriceField.getText());
        double maxPrice = maxPriceField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceField.getText());

        // Применяем фильтр по категории и диапазону цен
        ObservableList<Product> filteredByCategory = filteredList.stream()
                .filter(product -> (selectedCategory == null || selectedCategory.getId() == -1 ||
                        product.getCategory().getId() == selectedCategory.getId()) &&
                        product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        productTable.setItems(filteredByCategory);
    }

    @FXML
    private void filterByPriceRange() {
        try {
            double minPrice = minPriceField.getText().isEmpty() ? 0.0 : Double.parseDouble(minPriceField.getText());
            double maxPrice = maxPriceField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceField.getText());

            // Применяем фильтр по ценам и категориям
            filterByCategory(); // Фильтруем, учитывая выбранную категорию
        } catch (NumberFormatException e) {
            showError("Введите корректные значения для минимальной и максимальной цены");
        }
    }

    @FXML
    private void resetFilters() {
        searchField.clear();
        categoryComboBox.getSelectionModel().selectFirst();
        minPriceField.clear();
        maxPriceField.clear();
        filteredList.setAll(productList);
        productTable.setItems(filteredList);
    }

    @FXML
    private void handleBuy(Product product, int quantity) {
        ObservableList<Product> cart = getCurrentUserCart();

        for (Product item : cart) {
            if (item.getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        Product cartItem = new Product();
        cartItem.setId(product.getId());
        cartItem.setName(product.getName());
        cartItem.setQuantity(quantity);
        cartItem.setPrice(product.getPrice());
        cart.add(cartItem);
    }

    @FXML
    private void goToCart() {
        try {
            Stage stage = (Stage) productTable.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cart.fxml"));
            Parent root = loader.load();

            Cart controller = loader.getController();
            controller.setCart(getCurrentUserCart());

            stage.setScene(new Scene(root));
            stage.setTitle("Корзина");
            stage.show();
        } catch (Exception e) {
            showError("Ошибка перехода в корзину: " + e.getMessage());
        }
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) productTable.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/MainPage.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Главная страница");
            stage.show();
        } catch (Exception e) {
            showError("Ошибка возврата на главную страницу: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupCategoryComboBox() {
        // Устанавливаем пользовательский StringConverter для ComboBox
        categoryComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category category) {
                if (category == null) {
                    return "";
                }
                return category.getName(); // Отображать только имя категории
            }

            @Override
            public Category fromString(String string) {
                return categoryList.stream()
                        .filter(category -> category.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        // для отображение значений в выпадающем списке
        categoryComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                } else {
                    setText(category.getName()); // Отображать только имя категории
                }
            }
        });
    }

}
