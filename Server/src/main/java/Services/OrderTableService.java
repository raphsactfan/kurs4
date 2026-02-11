package Services;

import DAO.OrderTableDAO;
import Entities.OrderTable;
import Interfaces.DAO;
import Interfaces.Service;

import java.sql.Connection;
import java.util.List;

public class OrderTableService implements Service<OrderTable> {
    private DAO<OrderTable> daoService;

    public OrderTableService(Connection connection) {
        this.daoService = new OrderTableDAO(connection);
    }

    @Override
    public OrderTable findEntity(int id) {
        return daoService.findById(id);
    }

    @Override
    public void saveEntity(OrderTable entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(OrderTable entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(OrderTable entity) {
        daoService.update(entity);
    }

    @Override
    public List<OrderTable> findAllEntities() {
        return daoService.findAll();
    }


    public List<OrderTable> findOrdersByUserId(int userId) {
        return findAllEntities().stream()
                .filter(order -> order.getUser().getId() == userId)
                .toList();
    }

    public double getTotalOrderAmountByUserId(int userId) {
        return ((OrderTableDAO) daoService).getTotalOrderAmountByUserId(userId);
    }

}

