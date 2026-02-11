package controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Models.Entities.OrderProduct;
import main.Models.Entities.OrderTable;
import main.Utility.ClientSocket;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.GsonConfig;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class UserOrders {

    @FXML
    private TableView<OrderTable> ordersTable;

    @FXML
    private TableColumn<OrderTable, String> dateColumn;

    @FXML
    private TableColumn<OrderTable, String> totalAmountColumn;

    @FXML
    private TableColumn<OrderTable, String> quantityColumn;

    @FXML
    private TableColumn<OrderTable, VBox> productsColumn;

    @FXML
    private TableColumn<OrderTable, Button> checkColumn;

    @FXML
    private Label totalOrdersLabel;
    @FXML
    private Label totalPriceLabel;

    private final ObservableList<OrderTable> ordersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTableColumns();
        loadUserOrders();
    }

    private void configureTableColumns() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        dateColumn.setCellValueFactory(cellData -> {
            String formattedDate = cellData.getValue().getOrderDetails().getDateTime().format(formatter);
            return new SimpleStringProperty(formattedDate);
        });

        totalAmountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f руб.", cellData.getValue().getTotalAmount())));

        quantityColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));

        productsColumn.setCellValueFactory(cellData -> {
            VBox vBox = new VBox();
            ObservableList<String> products = FXCollections.observableArrayList(
                    cellData.getValue().getOrderProducts().stream()
                            .map(orderProduct -> orderProduct.getProduct().getName())
                            .collect(Collectors.toList())
            );

            products.forEach(product -> {
                Label label = new Label(product);
                vBox.getChildren().add(label);
            });

            return new SimpleObjectProperty<>(vBox);
        });

        checkColumn.setCellValueFactory(cellData -> {
            Button checkButton = new Button("Чек");
            checkButton.setOnAction(event -> generatePdf(cellData.getValue()));
            return new SimpleObjectProperty<>(checkButton);
        });
    }

    private void generatePdf(OrderTable order) {
        try {
            Document document = new Document();
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                String fontPath = "c:/windows/fonts/arial.ttf";
                BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font titleFont = new Font(baseFont, 18, Font.BOLD);
                Font textFont = new Font(baseFont, 12);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                String formattedOrderDate = order.getOrderDetails().getDateTime().format(formatter);

                Paragraph title = new Paragraph("Чек для заказа #" + order.getOrderId(), titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                Paragraph dateTime = new Paragraph("Дата заказа: " + formattedOrderDate, textFont);
                dateTime.setAlignment(Element.ALIGN_CENTER);
                dateTime.setSpacingAfter(20);
                document.add(dateTime);

                document.add(new Paragraph("Общая сумма: " + String.format("%.2f руб.", order.getTotalAmount()), textFont));
                document.add(new Paragraph("Общее количество товаров: " + order.getQuantity() + " шт.", textFont));
                document.add(new Paragraph("Товары в заказе:", textFont));
                for (OrderProduct orderProduct : order.getOrderProducts()) {
                    document.add(new Paragraph(orderProduct.getProduct().getName() + " - " +
                            String.format("%.2f руб.", orderProduct.getProduct().getPrice()), textFont));
                }

                document.close();
                showInfo("Чек успешно сохранен в PDF.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Ошибка при генерации чека: " + e.getMessage());
        }
    }

    private void loadUserOrders() {
        try {
            ClientSocket clientSocket = ClientSocket.getInstance();
            Gson gson = GsonConfig.createGson();

            Request request = new Request();
            request.setCommand("LOAD_USER_ORDERS");
            request.setData(String.valueOf(clientSocket.getUser().getId()));

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                OrderTable[] orders = gson.fromJson(response.getMessage(), OrderTable[].class);
                ordersList.clear();
                ordersList.addAll(orders);
                ordersTable.setItems(ordersList);

                updateSummary();
            } else {
                showError("Не удалось загрузить заказы: " + (response != null ? response.getMessage() : "Нет ответа от сервера"));
            }
        } catch (Exception e) {
            showError("Ошибка загрузки заказов: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateSummary() {
        int totalOrders = ordersList.size();
        double totalPrice = ordersList.stream().mapToDouble(OrderTable::getTotalAmount).sum();

        totalOrdersLabel.setText("Всего заказов: " + totalOrders);
        totalPriceLabel.setText("Общая сумма: " + String.format("%.2f руб.", totalPrice));
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) ordersTable.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/MainPage.fxml"));
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

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
