package Utility;

import TCP.Request;
import TCP.Response;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectOutputStream;

public abstract class BaseHandler {
    protected final ObjectOutputStream out;
    protected final Gson gson;

    public BaseHandler(ObjectOutputStream out) {
        this.out = out;
        this.gson = new Gson();
    }

    // Шаблонный метод
    public final void handle(Request request) {
        try {
            validateRequest(request);
            processRequest(request);
        } catch (Exception e) {
            sendResponse("ERROR", "Error processing request: " + e.getMessage());
        }
    }

    // Метод для валидации запроса (по умолчанию валидный)
    protected void validateRequest(Request request) throws Exception {
        if (request.getCommand() == null || request.getCommand().isEmpty()) {
            throw new Exception("Invalid command");
        }
    }

    // Абстрактный метод для выполнения запроса
    protected abstract void processRequest(Request request) throws Exception;

    // Метод для отправки ответа
    protected void sendResponse(String status, String message) {
        try {
            Response response = new Response(status, message);
            out.writeObject(gson.toJson(response));
            out.flush();
        } catch (IOException e) {
            System.err.println("Error sending response: " + e.getMessage());
        }
    }
}

