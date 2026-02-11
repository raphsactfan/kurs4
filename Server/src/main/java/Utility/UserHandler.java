package Utility;

import Entities.User;
import Services.UserService;
import TCP.Request;

import java.io.ObjectOutputStream;
import java.util.List;

public class UserHandler extends BaseHandler {
    private final UserService userService;

    public UserHandler(UserService userService, ObjectOutputStream out) {
        super(out);
        this.userService = userService;
    }

    @Override
    protected void processRequest(Request request) throws Exception {
        String command = request.getCommand().toUpperCase();

        switch (command) {
            case "ADD_USER":
                addUser(request);
                break;
            case "EDIT_USER":
                editUser(request);
                break;
            case "DELETE_USER":
                deleteUser(request);
                break;
            case "LOAD_USERS":
                loadUsers(request);
                break;
            case "LOAD_USERS_BY_ROLE":
                loadUsersByRole(request);
                break;
            default:
                throw new Exception("Unknown command: " + command);
        }
    }

    private void addUser(Request request) throws Exception {
        User newUser = gson.fromJson(request.getData(), User.class);

        List<User> users = userService.findAllEntities();
        for (User user : users) {
            if (user.getLogin().equalsIgnoreCase(newUser.getLogin())) {
                throw new Exception("Данный логин занят!");
            }
        }

        if (newUser.getRole() == null || newUser.getRole().isEmpty()) {
            newUser.setRole("user"); // Роль по умолчанию
        }

        userService.saveEntity(newUser);
        sendResponse("SUCCESS", "Пользователь успешно добавлен!");
    }

    private void editUser(Request request) throws Exception {
        User updatedUser = gson.fromJson(request.getData(), User.class);

        User userToUpdate = userService.findEntity(updatedUser.getId());
        if (userToUpdate == null) {
            throw new Exception("Пользователь не найден...");
        }

        userToUpdate.setLogin(updatedUser.getLogin());
        userToUpdate.setPassword(updatedUser.getPassword());
        userToUpdate.setRole(updatedUser.getRole());

        userService.updateEntity(userToUpdate);
        sendResponse("SUCCESS", "Пользователь успешно обновлён!");
    }

    private void deleteUser(Request request) throws Exception {
        User userToDelete = gson.fromJson(request.getData(), User.class);

        User existingUser = userService.findEntity(userToDelete.getId());
        if (existingUser == null) {
            throw new Exception("Пользователь не найден");
        }

        userService.deleteEntity(existingUser);
        sendResponse("SUCCESS", "Пользователь успешно удалён!");
    }

    private void loadUsers(Request request) throws Exception {
        List<User> users = userService.findAllEntities();
        String jsonResponse = gson.toJson(users);
        sendResponse("SUCCESS", jsonResponse);
    }

    private void loadUsersByRole(Request request) throws Exception {
        String role = request.getData();
        List<User> usersByRole = userService.findUsersByRole(role);
        String jsonResponse = gson.toJson(usersByRole);
        sendResponse("SUCCESS", jsonResponse);
    }
}
