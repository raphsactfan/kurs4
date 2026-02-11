package main.Utility;

import com.google.gson.Gson;
import main.Models.Entities.User;
import main.Models.TCP.Request;
import main.Models.TCP.Response;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocket {
    private static volatile ClientSocket instance;
    private User user;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final Gson gson = new Gson();

    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    private ClientSocket() {
        connectToServer();
    }

    public static ClientSocket getInstance() {
        if (instance == null) {
            synchronized (ClientSocket.class) {
                if (instance == null) {
                    instance = new ClientSocket();
                }
            }
        }
        return instance;
    }

    private void connectToServer() {
        try {
            socket = new Socket(HOST, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to server");
        } catch (Exception e) {
            System.err.println("Failed to connect to the server: " + e.getMessage());
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectInputStream getInputStream() {
        return in;
    }

    public ObjectOutputStream getOutputStream() {
        return out;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Gson getGson() {
        return gson;
    }

    public void sendRequest(Request request) {
        try {
            if (socket == null || socket.isClosed()) {
                reconnect();
            }
            String jsonRequest = gson.toJson(request);
            out.writeObject(jsonRequest);
            out.flush();
        } catch (Exception e) {
            System.err.println("Failed to send request: " + e.getMessage());
        }
    }

    public Response receiveResponse() {
        try {
            if (socket == null || socket.isClosed()) {
                reconnect();
            }
            String jsonResponse = (String) in.readObject();
            return gson.fromJson(jsonResponse, Response.class);
        } catch (Exception e) {
            System.err.println("Failed to receive response: " + e.getMessage());
            return null;
        }
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            System.out.println("Connection closed");
        } catch (Exception e) {
            System.err.println("Failed to close connection: " + e.getMessage());
        }
    }

    private void reconnect() {
        System.out.println("Reconnecting to server...");
        close();
        connectToServer();
    }
}