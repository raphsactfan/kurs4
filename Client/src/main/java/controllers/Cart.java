package controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Models.Entities.Product;

public class Cart {

    @FXML
    private TilePane cartPane;

    @FXML
    private Label totalLabel;

    @FXML
    private Label itemCountLabel; // Новый Label для отображения количества товаров

    private ObservableList<Product> cart;

    public void setCart(ObservableList<Product> cart) {
        this.cart = cart;
        renderCartItems();
        updateTotal();
    }

    @FXML
    public void initialize() {
        // Пустой метод, так как корзина обновляется в setCart
    }

    private void renderCartItems() {
        cartPane.getChildren().clear();

        for (Product product : cart) {
            VBox productCard = createProductCard(product);
            cartPane.getChildren().add(productCard);
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-color: white; -fx-background-radius: 8; -fx-pref-width: 200;");

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-wrap-text: true;");
        nameLabel.setMaxWidth(180); // Максимальная ширина текста для переноса

        Label priceLabel = new Label("Цена: " + product.getPrice() + " ₽");
        Label quantityLabel = new Label("Количество: " + product.getQuantity());
        Label totalLabel = new Label("Итого: " + (product.getPrice() * product.getQuantity()) + " ₽");

        Button deleteButton = new Button("Удалить");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(event -> removeProduct(product));

        card.getChildren().addAll(nameLabel, priceLabel, quantityLabel, totalLabel, deleteButton);
        return card;
    }

    @FXML
    private void clearCart() {
        cart.clear();
        renderCartItems();
        updateTotal();
    }

    private void removeProduct(Product product) {
        cart.remove(product);
        renderCartItems();
        updateTotal();
    }

    private void updateTotal() {
        double total = cart.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        int itemCount = cart.stream().mapToInt(Product::getQuantity).sum(); // Подсчет общего количества товаров

        totalLabel.setText("Итог: " + total + " руб.");
        itemCountLabel.setText("Количество: " + itemCount); // Обновление общего количества
    }

    @FXML
    private void proceedToCheckout() {
        if (cart == null || cart.isEmpty()) {
            showError("Ваша корзина пуста. Добавьте товары перед оформлением заказа.");
            return;
        }

        try {
            // Переход на страницу оформления заказа
            Stage stage = (Stage) cartPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Checkout.fxml"));
            Parent root = loader.load();

            // Передача корзины в контроллер оформления заказа
            Checkout checkoutController = loader.getController();
            checkoutController.setCart(cart);

            stage.setScene(new Scene(root));
            stage.setTitle("Оформление заказа");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Ошибка перехода на страницу оформления заказа.");
        }
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) cartPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Catalog.fxml"));
            Parent root = loader.load();

            Catalog controller = loader.getController();
            stage.setScene(new Scene(root));
            stage.setTitle("Каталог");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
