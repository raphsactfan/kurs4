package Services;

import DAO.OrderProductDAO;
import Entities.OrderProduct;
import Interfaces.DAO;
import Interfaces.Service;

import java.sql.Connection;
import java.util.List;

public class OrderProductService implements Service<OrderProduct> {
    private DAO<OrderProduct> daoService;

    public OrderProductService(Connection connection) {
        this.daoService = new OrderProductDAO(connection);
    }

    @Override
    public OrderProduct findEntity(int id) {
        throw new UnsupportedOperationException("Исключение");
    }

    @Override
    public void saveEntity(OrderProduct entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(OrderProduct entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(OrderProduct entity) {
        throw new UnsupportedOperationException("Исключение");
    }

    @Override
    public List<OrderProduct> findAllEntities() {
        return daoService.findAll();
    }


    public List<OrderProduct> findProductsByOrderId(int orderId) {
        return findAllEntities().stream()
                .filter(orderProduct -> orderProduct.getOrderId() == orderId)
                .toList();
    }
}
