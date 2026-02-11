package Services;

import DAO.UserDAO;
import Entities.User;
import Interfaces.DAO;
import Interfaces.Service;

import java.sql.Connection;
import java.util.List;

public class UserService implements Service<User> {
    private DAO<User> daoService;

    public UserService(Connection connection) {
        this.daoService = new UserDAO(connection);
    }

    @Override
    public User findEntity(int id) {
        return daoService.findById(id);
    }

    @Override
    public void saveEntity(User entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(User entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(User entity) {
        daoService.update(entity);
    }

    @Override
    public List<User> findAllEntities() {
        return daoService.findAll();
    }


    public List<User> findUsersByRole(String role) {
        return findAllEntities().stream()
                .filter(user -> user.getRole().equalsIgnoreCase(role))
                .toList();
    }
}
