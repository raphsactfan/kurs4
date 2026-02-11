package Utility;

import Entities.Supplier;
import Services.SupplierService;
import TCP.Request;

import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

public class SupplierHandler extends BaseHandler {
    private final SupplierService supplierService;

    public SupplierHandler(SupplierService supplierService, ObjectOutputStream out) {
        super(out);
        this.supplierService = supplierService;
    }

    @Override
    protected void processRequest(Request request) throws Exception {
        String command = request.getCommand().toUpperCase();

        switch (command) {
            case "ADD_SUPPLIER":
                addSupplier(request);
                break;
            case "EDIT_SUPPLIER":
                editSupplier(request);
                break;
            case "DELETE_SUPPLIER":
                deleteSupplier(request);
                break;
            case "LOAD_SUPPLIERS":
                loadSuppliers(request);
                break;
            case "SEARCH_SUPPLIERS":
                searchSuppliers(request);
                break;
            case "SUPPLIER_STATISTICS":
                supplierStatistics(request);
                break;
            default:
                throw new Exception("Unknown command: " + command);
        }
    }

    private void addSupplier(Request request) throws Exception {
        Supplier newSupplier = gson.fromJson(request.getData(), Supplier.class);
        supplierService.saveEntity(newSupplier);
        sendResponse("SUCCESS", "Поставщик успешно добавлен");
    }

    private void editSupplier(Request request) throws Exception {
        Supplier updatedSupplier = gson.fromJson(request.getData(), Supplier.class);
        supplierService.updateEntity(updatedSupplier);
        sendResponse("SUCCESS", "Поставщик успешно обновлен");
    }

    private void deleteSupplier(Request request) throws Exception {
        Supplier supplierToDelete = gson.fromJson(request.getData(), Supplier.class);
        supplierService.deleteEntity(supplierToDelete);
        sendResponse("SUCCESS", "Поставщик успешно удален");
    }

    private void loadSuppliers(Request request) throws Exception {
        List<Supplier> suppliers = supplierService.findAllEntities();
        String jsonResponse = gson.toJson(suppliers);
        sendResponse("SUCCESS", jsonResponse);
    }

    private void searchSuppliers(Request request) throws Exception {
        String name = request.getData(); // Имя или часть имени для поиска
        List<Supplier> filteredSuppliers = supplierService.findSuppliersByName(name);
        String jsonResponse = gson.toJson(filteredSuppliers);
        sendResponse("SUCCESS", jsonResponse);
    }

    private void supplierStatistics(Request request) throws Exception {
        Map<String, Integer> supplierStatistics = supplierService.getSupplierProductCounts();
        String jsonResponse = gson.toJson(supplierStatistics);
        sendResponse("SUCCESS", jsonResponse);
    }
}
