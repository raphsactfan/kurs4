package Services;

import DAO.CategoryDAO;
import Entities.Category;
import Interfaces.DAO;
import Interfaces.Service;

import java.sql.Connection;
import java.util.List;

public class CategoryService implements Service<Category> {
    private final DAO<Category> daoService;

    public CategoryService(Connection connection) {
        this.daoService = new CategoryDAO(connection);
    }

    @Override
    public Category findEntity(int id) {
        return daoService.findById(id);
    }

    @Override
    public void saveEntity(Category entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(Category entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(Category entity) {
        daoService.update(entity);
    }

    @Override
    public List<Category> findAllEntities() {
        return daoService.findAll();
    }

    public List<Category> findCategoriesByName(String name) {
        return findAllEntities().stream()
                .filter(category -> category.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    public int countProductsInCategory(int categoryId) {
        return ((CategoryDAO) daoService).countProductsInCategory(categoryId);
    }

}
