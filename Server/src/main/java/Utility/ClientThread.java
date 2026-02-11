package Utility;

import DAO.AddressDAO;
import Entities.Address;
import Entities.User;
import Services.*;
import TCP.Request;
import TCP.Response;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientThread implements Runnable {
    private final Socket clientSocket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final Gson gson;
    private final Connection connection;

    // Обработчики
    private final Map<String, BaseHandler> handlers = new HashMap<>();

    public ClientThread(Socket socket, Connection connection) throws IOException {
        this.clientSocket = socket;
        this.connection = connection;
        this.in = new ObjectInputStream(clientSocket.getInputStream());
        this.out = new ObjectOutputStream(clientSocket.getOutputStream());
        this.gson = new Gson();

        // Сервисы
        UserService userService = new UserService(connection);
        SupplierService supplierService = new SupplierService(connection);
        CategoryService categoryService = new CategoryService(connection);
        ProductService productService = new ProductService(connection);
        OrderTableService orderTableService = new OrderTableService(connection);
        OrderProductService orderProductService = new OrderProductService(connection);
        OrderDetailsService orderDetailsService = new OrderDetailsService(connection);

        // Регистрация обработчиков
        handlers.put("ADD_USER", new UserHandler(userService, out));
        handlers.put("EDIT_USER", new UserHandler(userService, out));
        handlers.put("DELETE_USER", new UserHandler(userService, out));
        handlers.put("LOAD_USERS", new UserHandler(userService, out));
        handlers.put("LOAD_USERS_BY_ROLE", new UserHandler(userService, out));

        handlers.put("ADD_SUPPLIER", new SupplierHandler(supplierService, out));
        handlers.put("EDIT_SUPPLIER", new SupplierHandler(supplierService, out));
        handlers.put("DELETE_SUPPLIER", new SupplierHandler(supplierService, out));
        handlers.put("LOAD_SUPPLIERS", new SupplierHandler(supplierService, out));
        handlers.put("SEARCH_SUPPLIERS", new SupplierHandler(supplierService, out));
        handlers.put("SUPPLIER_STATISTICS", new SupplierHandler(supplierService, out));

        handlers.put("ADD_CATEGORY", new CategoryHandler(categoryService, out));
        handlers.put("EDIT_CATEGORY", new CategoryHandler(categoryService, out));
        handlers.put("DELETE_CATEGORY", new CategoryHandler(categoryService, out));
        handlers.put("LOAD_CATEGORIES", new CategoryHandler(categoryService, out));
        handlers.put("SEARCH_CATEGORIES", new CategoryHandler(categoryService, out));
        handlers.put("CATEGORY_STATISTICS", new CategoryHandler(categoryService, out));

        handlers.put("ADD_PRODUCT", new ProductHandler(productService, out));
        handlers.put("EDIT_PRODUCT", new ProductHandler(productService, out));
        handlers.put("DELETE_PRODUCT", new ProductHandler(productService, out));
        handlers.put("LOAD_PRODUCTS", new ProductHandler(productService, out));
        handlers.put("SEARCH_PRODUCTS", new ProductHandler(productService, out));

        handlers.put("CREATE_ORDER", new OrderHandler(orderTableService, orderProductService, orderDetailsService, productService, connection, out));
        handlers.put("LOAD_USER_ORDERS", new OrderHandler(orderTableService, orderProductService, orderDetailsService, productService, connection, out));
        handlers.put("LOAD_ALL_ORDERS", new OrderHandler(orderTableService, orderProductService, orderDetailsService, productService, connection, out));
        handlers.put("GET_TOTAL_ORDER_AMOUNT", new OrderHandler(orderTableService, orderProductService, orderDetailsService, productService, connection, out));
    }

    @Override
    public void run() {
        try {
            while (true) {
                String json = (String) in.readObject();
                Request request = gson.fromJson(json, Request.class);

                // Обработка авторизации и регистрации + адреса
                switch (request.getCommand().toUpperCase()) {
                    case "REGISTER":
                        handleRegister(request);
                        break;
                    case "LOGIN":
                        handleLogin(request);
                        break;
                    case "LOAD_ADDRESSES":
                        try {
                            AddressDAO addressDAO = new AddressDAO(connection);
                            List<Address> addresses = addressDAO.getAllAddresses();
                            sendResponse("SUCCESS", gson.toJson(addresses));
                        } catch (Exception e) {
                            sendResponse("ERROR", "Не удалось загрузить адреса: " + e.getMessage());
                        }
                        break;
                    default:
                        handleCommand(request);
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Client connection error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Failed to close client socket: " + e.getMessage());
            }
        }
    }

    private void handleCommand(Request request) {
        BaseHandler handler = handlers.get(request.getCommand().toUpperCase());
        if (handler != null) {
            handler.handle(request);
        } else {
            sendResponse("ERROR", "Unknown command");
        }
    }

    private void handleRegister(Request request) {
        User newUser = gson.fromJson(request.getData(), User.class);

        try {
            PreparedStatement checkUser = connection.prepareStatement("SELECT * FROM User WHERE Login = ?");
            checkUser.setString(1, newUser.getLogin());
            ResultSet resultSet = checkUser.executeQuery();

            if (resultSet.next()) {
                sendResponse("ERROR", "Данный логин занят");
                return;
            }

            String role = newUser.getRole();
            if (role == null || role.isEmpty()) {
                role = "user"; // Роль по умолчанию
            }

            PreparedStatement insertUser = connection.prepareStatement("INSERT INTO User (Login, Password, Role) VALUES (?, ?, ?)");
            insertUser.setString(1, newUser.getLogin());
            insertUser.setString(2, newUser.getPassword());
            insertUser.setString(3, role);
            insertUser.executeUpdate();

            sendResponse("SUCCESS", "Пользователь успешно зарегистрирован");
        } catch (SQLException e) {
            sendResponse("ERROR", "Registration failed: " + e.getMessage());
        }
    }

    private void handleLogin(Request request) {
        User user = gson.fromJson(request.getData(), User.class);

        try {
            PreparedStatement checkUser = connection.prepareStatement(
                    "SELECT * FROM User WHERE Login = ? AND Password = ?"
            );
            checkUser.setString(1, user.getLogin());
            checkUser.setString(2, user.getPassword());
            ResultSet resultSet = checkUser.executeQuery();

            if (resultSet.next()) {
                User loggedInUser = new User();
                loggedInUser.setId(resultSet.getInt("UserID"));
                loggedInUser.setLogin(resultSet.getString("Login"));
                loggedInUser.setRole(resultSet.getString("Role"));

                sendResponse("SUCCESS", gson.toJson(loggedInUser));
            } else {
                sendResponse("ERROR", "Неверный логин или пароль");
            }
        } catch (SQLException e) {
            sendResponse("ERROR", "Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendResponse(String status, String message) {
        try {
            Response response = new Response(status, message);
            out.writeObject(gson.toJson(response));
            out.flush();
        } catch (IOException e) {
            System.err.println("Failed to send response: " + e.getMessage());
        }
    }
}
