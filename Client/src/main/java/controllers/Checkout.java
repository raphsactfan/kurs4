package controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Models.Entities.Address;
import main.Models.Entities.OrderProduct;
import main.Models.Entities.OrderTable;
import main.Models.Entities.Product;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Checkout {

    @FXML
    private TextField countryField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField streetField;

    @FXML
    private VBox productsPane;
    @FXML
    private Label totalLabel;
    @FXML
    private CheckBox agreementCheckBox;
    @FXML
    private Button submitOrderButton;

    private ObservableList<Product> cart;

    public void setCart(ObservableList<Product> cart) {
        this.cart = cart;
        renderCartItems();
        updateTotal();
    }

    @FXML
    public void initialize() {
        // Инициализация корзины и итоговой суммы
    }

    private void renderCartItems() {
        productsPane.getChildren().clear();

        for (Product product : cart) {
            Label productLabel = new Label(String.format("Товар: %s | Количество: %d | Цена: %.2f руб.",
                    product.getName(), product.getQuantity(), product.getPrice()));
            productLabel.setStyle("-fx-font-size: 14px;");
            productsPane.getChildren().add(productLabel);
        }
    }

    private void updateTotal() {
        double total = cart.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        totalLabel.setText(String.format("%.2f руб.", total));
    }

    @FXML
    private void submitOrder() {
        // Проверяем поля
        if (countryField.getText().trim().isEmpty()) {
            showAlert("Ошибка", "Поле 'Страна' обязательно для заполнения.");
            return;
        }

        if (cityField.getText().trim().isEmpty()) {
            showAlert("Ошибка", "Поле 'Город' обязательно для заполнения.");
            return;
        }

        if (streetField.getText().trim().isEmpty()) {
            showAlert("Ошибка", "Поле 'Улица' обязательно для заполнения.");
            return;
        }

        if (!agreementCheckBox.isSelected()) {
            showAlert("Ошибка", "Вы должны согласиться с условиями соглашения.");
            return;
        }

        try {
            // Создаем объект заказа
            OrderTable order = new OrderTable();
            Address address = new Address();
            address.setCountry(countryField.getText());
            address.setCity(cityField.getText());
            address.setStreet(streetField.getText());
            order.setAddress(address);

            order.setUser(ClientSocket.getInstance().getUser());
            order.setQuantity(cart.stream().mapToInt(Product::getQuantity).sum());
            order.setTotalAmount(cart.stream().mapToDouble(product -> product.getPrice() * product.getQuantity()).sum());
            order.setOrderProducts(cart.stream().map(product -> {
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setProduct(product);
                return orderProduct;
            }).collect(Collectors.toList()));

            // Отправляем запрос на сервер
            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("CREATE_ORDER");
            request.setData(clientSocket.getGson().toJson(order));

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if ("SUCCESS".equals(response.getStatus())) {
                showAlert("Успех", "Заказ успешно оформлен!");
                cart.clear();
                goBack();
            } else {
                showAlert("Ошибка", response.getMessage());
            }
        } catch (Exception e) {
            showAlert("Ошибка", "Произошла ошибка при оформлении заказа: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showAgreement() {
        Stage agreementStage = new Stage();
        agreementStage.initModality(Modality.APPLICATION_MODAL);
        agreementStage.initStyle(StageStyle.UTILITY);
        agreementStage.setTitle("Соглашение");

        TextArea agreementText = new TextArea();
        agreementText.setWrapText(true);
        agreementText.setEditable(false);

        try {
            InputStream inputStream = getClass().getResourceAsStream("/agreement.txt");
            if (inputStream != null) {
                String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                agreementText.setText(content);
            } else {
                agreementText.setText("Ошибка: файл соглашения не найден.");
            }
        } catch (IOException e) {
            agreementText.setText("Ошибка при загрузке текста соглашения: " + e.getMessage());
        }

        Button closeButton = new Button("Закрыть");
        closeButton.setOnAction(e -> agreementStage.close());

        VBox layout = new VBox(10, agreementText, closeButton);
        layout.setStyle("-fx-padding: 10;");

        Scene scene = new Scene(layout, 400, 300);
        agreementStage.setScene(scene);
        agreementStage.show();
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) countryField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cart.fxml"));
            Parent root = loader.load();

            Cart cartController = loader.getController();
            cartController.setCart(cart);

            stage.setScene(new Scene(root));
            stage.setTitle("Корзина");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Ошибка при возврате в корзину: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
