package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Models.Entities.OrderProduct;
import main.Models.Entities.OrderTable;
import main.Models.Entities.User;
import main.Utility.ClientSocket;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import com.google.gson.Gson;
import main.Utility.GsonConfig;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class OrderManagement {

    @FXML
    private TableView<OrderTable> ordersTable;

    @FXML
    private TableColumn<OrderTable, String> orderIdColumn;

    @FXML
    private TableColumn<OrderTable, String> userColumn;

    @FXML
    private TableColumn<OrderTable, String> dateColumn;

    @FXML
    private TableColumn<OrderTable, String> totalAmountColumn;

    @FXML
    private TableColumn<OrderTable, String> quantityColumn;

    @FXML
    private TableColumn<OrderTable, VBox> productsColumn;

    @FXML
    private TextField searchUserField;

    private final ObservableList<OrderTable> ordersList = FXCollections.observableArrayList();
    private final ObservableList<OrderTable> filteredOrders = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTableColumns();
        loadAllOrders();
    }

    private void configureTableColumns() {
        orderIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getOrderId())));

        userColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue().getUser();
            return new SimpleStringProperty(user != null ? user.getLogin() : "Неизвестен");
        });

        dateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getOrderDetails() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                return new SimpleStringProperty(cellData.getValue().getOrderDetails().getDateTime().format(formatter));
            } else {
                return new SimpleStringProperty("Дата не указана");
            }
        });

        totalAmountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f руб.", cellData.getValue().getTotalAmount())));

        quantityColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));

        productsColumn.setCellValueFactory(cellData -> {
            VBox vBox = new VBox();
            List<OrderProduct> orderProducts = cellData.getValue().getOrderProducts();
            if (orderProducts != null && !orderProducts.isEmpty()) {
                orderProducts.forEach(orderProduct -> {
                    Label productLabel = new Label(orderProduct.getProduct().getName());
                    vBox.getChildren().add(productLabel);
                });
            } else {
                Label noProductsLabel = new Label("Нет товаров");
                vBox.getChildren().add(noProductsLabel);
            }
            return new SimpleObjectProperty<>(vBox);
        });
    }

    private void loadAllOrders() {
        try {
            ClientSocket clientSocket = ClientSocket.getInstance();
            Gson gson = GsonConfig.createGson();
            Request request = new Request();
            request.setCommand("LOAD_ALL_ORDERS");
            request.setData("");

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                OrderTable[] orders = gson.fromJson(response.getMessage(), OrderTable[].class);
                ordersList.clear();
                ordersList.addAll(orders);
                filteredOrders.setAll(ordersList); // Устанавливаем начальный фильтр
                ordersTable.setItems(filteredOrders);
            } else {
                showError("Не удалось загрузить заказы: " + (response != null ? response.getMessage() : "Нет ответа от сервера"));
            }
        } catch (Exception e) {
            showError("Ошибка загрузки заказов: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void filterOrdersByUser() {
        String query = searchUserField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            filteredOrders.setAll(ordersList);
        } else {
            filteredOrders.setAll(ordersList.stream()
                    .filter(order -> order.getUser() != null && order.getUser().getLogin().toLowerCase().contains(query))
                    .collect(Collectors.toList()));
        }
        ordersTable.setItems(filteredOrders);

        if (filteredOrders.isEmpty()) {
            showAlert("Info", "Заказы не найдены");
        }
    }

    @FXML
    private void resetSearch() {
        searchUserField.clear(); // Очищаем поле поиска
        filteredOrders.setAll(ordersList); // Восстанавливаем полный список заказов
        ordersTable.setItems(filteredOrders);
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) ordersTable.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/adminPage.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Главная страница");
            stage.show();
        } catch (Exception e) {
            showError("Ошибка возврата на главную страницу: " + e.getMessage());
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
