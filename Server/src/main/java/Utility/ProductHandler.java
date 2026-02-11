package Utility;

import Entities.Product;
import Services.ProductService;
import TCP.Request;

import java.io.ObjectOutputStream;
import java.util.List;

public class ProductHandler extends BaseHandler {
    private final ProductService productService;

    public ProductHandler(ProductService productService, ObjectOutputStream out) {
        super(out);
        this.productService = productService;
    }

    @Override
    protected void processRequest(Request request) throws Exception {
        String command = request.getCommand().toUpperCase();

        switch (command) {
            case "ADD_PRODUCT":
                addProduct(request);
                break;
            case "EDIT_PRODUCT":
                editProduct(request);
                break;
            case "DELETE_PRODUCT":
                deleteProduct(request);
                break;
            case "LOAD_PRODUCTS":
                loadProducts(request);
                break;
            case "SEARCH_PRODUCTS":
                searchProducts(request);
                break;
            default:
                throw new Exception("Unknown command: " + command);
        }
    }

    private void addProduct(Request request) throws Exception {
        Product newProduct = gson.fromJson(request.getData(), Product.class);
        productService.saveEntity(newProduct);
        sendResponse("SUCCESS", "Товар успешно добавлен");
    }

    private void editProduct(Request request) throws Exception {
        Product updatedProduct = gson.fromJson(request.getData(), Product.class);
        productService.updateEntity(updatedProduct);
        sendResponse("SUCCESS", "Товар успешно обновлен");
    }

    private void deleteProduct(Request request) throws Exception {
        Product productToDelete = gson.fromJson(request.getData(), Product.class);
        productService.deleteEntity(productToDelete);
        sendResponse("SUCCESS", "Товар успешно удален");
    }

    private void loadProducts(Request request) throws Exception {
        List<Product> products = productService.findAllEntities();
        String jsonResponse = gson.toJson(products);
        sendResponse("SUCCESS", jsonResponse);
    }

    private void searchProducts(Request request) throws Exception {
        String name = request.getData(); // Имя или часть имени для поиска
        List<Product> filteredProducts = productService.findProductsByName(name);
        String jsonResponse = gson.toJson(filteredProducts);
        sendResponse("SUCCESS", jsonResponse);
    }
}
