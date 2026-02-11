package Services;

import DAO.ProductDAO;
import Entities.Product;
import Interfaces.DAO;
import Interfaces.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductService implements Service<Product> {
    private final DAO<Product> daoService;

    public ProductService(Connection connection) {
        this.daoService = new ProductDAO(connection);
    }

    @Override
    public Product findEntity(int id) {
        return daoService.findById(id);
    }

    @Override
    public void saveEntity(Product entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(Product entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(Product entity) {
        daoService.update(entity);
    }

    @Override
    public List<Product> findAllEntities() {
        return daoService.findAll();
    }


    public List<Product> findProductsByName(String name) {
        return findAllEntities().stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    public void updateProductQuantity(int productId, int quantity) {
        try {
            ((ProductDAO) daoService).updateQuantity(productId, quantity);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления количества: " + e.getMessage(), e);
        }
    }
}
