package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.Utility.ClientSocket;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        ClientSocket.getInstance().getSocket();
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        primaryStage.setTitle("Авторизация");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
