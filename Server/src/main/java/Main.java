import Utility.ClientThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int PORT_NUMBER = 12345;
    private static ServerSocket serverSocket;
    private static List<Socket> currentSockets = new ArrayList<>();

    //Настройка подключения к базе данных
    private static final String DB_URL = "jdbc:mysql://localhost:3306/storekurs";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "12345678";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Database connected.");

            serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println("Server started on port " + PORT_NUMBER);

            while (true) {
                Socket socket = serverSocket.accept(); //ожидается подключение клиента
                currentSockets.add(socket); //добавление сокета клиента
                System.out.println("Client connected: " + socket.getInetAddress() + ":" + socket.getPort());

                ClientThread clientThread = new ClientThread(socket, connection);
                Thread thread = new Thread(clientThread);
                thread.start();
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Failed to close server socket: " + e.getMessage());
            }
        }
    }
}
