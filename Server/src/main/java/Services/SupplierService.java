package Services;

import DAO.SupplierDAO;
import Entities.Supplier;
import Interfaces.DAO;
import Interfaces.Service;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class SupplierService implements Service<Supplier> {
    private final DAO<Supplier> daoService;

    public SupplierService(Connection connection) {
        this.daoService = new SupplierDAO(connection);
    }

    @Override
    public Supplier findEntity(int id) {
        return daoService.findById(id);
    }

    @Override
    public void saveEntity(Supplier entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(Supplier entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(Supplier entity) {
        daoService.update(entity);
    }

    @Override
    public List<Supplier> findAllEntities() {
        return daoService.findAll();
    }


    public List<Supplier> findSuppliersByName(String name) {
        return findAllEntities().stream()
                .filter(supplier -> supplier.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    public Map<String, Integer> getSupplierProductCounts() {
        return ((SupplierDAO) daoService).fetchSupplierProductCounts();
    }

}
