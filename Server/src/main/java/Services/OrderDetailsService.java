package Services;

import DAO.OrderDetailsDAO;
import Entities.OrderDetails;
import Interfaces.DAO;
import Interfaces.Service;

import java.sql.Connection;
import java.util.List;

public class OrderDetailsService implements Service<OrderDetails> {
    private DAO<OrderDetails> daoService;

    public OrderDetailsService(Connection connection) {
        this.daoService = new OrderDetailsDAO(connection);
    }

    @Override
    public OrderDetails findEntity(int id) {
        return daoService.findById(id);
    }

    @Override
    public void saveEntity(OrderDetails entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(OrderDetails entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(OrderDetails entity) {
        daoService.update(entity);
    }

    @Override
    public List<OrderDetails> findAllEntities() {
        return daoService.findAll();
    }

    public OrderDetails findDetailsByOrderId(int orderId) {
        return findEntity(orderId);
    }
}

