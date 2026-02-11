package controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.PieChart;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import main.Utility.ClientSocket;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import com.google.gson.reflect.TypeToken;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

public class StatisticsSupplierController {

    @FXML
    private PieChart supplierPieChart;

    @FXML
    public void initialize() {
        loadSupplierStatistics();
    }

    private void loadSupplierStatistics() {
        try {
            ClientSocket clientSocket = ClientSocket.getInstance();
            Request request = new Request();
            request.setCommand("SUPPLIER_STATISTICS");

            clientSocket.sendRequest(request);
            Response response = clientSocket.receiveResponse();

            if (response != null && "SUCCESS".equals(response.getStatus())) {
                Map<String, Integer> statistics = clientSocket.getGson().fromJson(
                        response.getMessage(), new TypeToken<Map<String, Integer>>() {}.getType()
                );
                populatePieChart(statistics);
            } else {
                showError("Не удалось загрузить статистику по поставщикам");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Ошибка загрузки статистики: " + e.getMessage());
        }
    }

    private void populatePieChart(Map<String, Integer> statistics) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : statistics.entrySet()) {
            String formattedLabel = entry.getKey() + " (" + entry.getValue() + ")";
            pieChartData.add(new PieChart.Data(formattedLabel, entry.getValue()));
        }
        supplierPieChart.setData(pieChartData);
    }

    @FXML
    private void printChart() {
        try {
            // Сохранить диаграмму как изображение
            WritableImage image = supplierPieChart.snapshot(new SnapshotParameters(), null);
            File tempFile = File.createTempFile("chart", ".png");
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", tempFile);

            // Выбор файла для сохранения PDF
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File pdfFile = fileChooser.showSaveDialog(supplierPieChart.getScene().getWindow());

            if (pdfFile != null) {
                // Получение текущей даты и времени
                String currentDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

                // Путь к шрифту
                String fontPath = "c:/windows/fonts/arial.ttf";
                BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font titleFont = new Font(baseFont, 18, Font.BOLD);
                Font textFont = new Font(baseFont, 12);

                // Создание PDF
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                document.open();

                // Добавление заголовка
                Paragraph title = new Paragraph("Статистика по поставщикам", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                // Добавление даты и времени
                Paragraph dateTime = new Paragraph("Дата и время отчета: " + currentDateTime, textFont);
                dateTime.setAlignment(Element.ALIGN_CENTER);
                dateTime.setSpacingAfter(20); // Добавление отступа после даты
                document.add(dateTime);

                // Добавление изображения диаграммы
                Image chartImage = Image.getInstance(tempFile.getAbsolutePath());
                chartImage.scaleToFit(500, 400);
                chartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(chartImage);

                document.close();

                System.out.println("PDF успешно создан: " + pdfFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Не удалось создать PDF: " + e.getMessage());
        }
    }


    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) supplierPieChart.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/StatisticsSelection.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Выбор статистики");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Не удалось вернуться на страницу администратора: " + e.getMessage());
        }
    }

    private void showError(String message) {
        System.err.println(message);
    }
}
