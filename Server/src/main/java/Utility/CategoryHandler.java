package Utility;

import Entities.Category;
import Services.CategoryService;
import TCP.Request;

import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryHandler extends BaseHandler {
    private final CategoryService categoryService;

    public CategoryHandler(CategoryService categoryService, ObjectOutputStream out) {
        super(out);
        this.categoryService = categoryService;
    }

    @Override
    protected void processRequest(Request request) throws Exception {
        String command = request.getCommand().toUpperCase();

        switch (command) {
            case "ADD_CATEGORY":
                addCategory(request);
                break;
            case "EDIT_CATEGORY":
                editCategory(request);
                break;
            case "DELETE_CATEGORY":
                deleteCategory(request);
                break;
            case "LOAD_CATEGORIES":
                loadCategories(request);
                break;
            case "SEARCH_CATEGORIES":
                searchCategories(request);
                break;
            case "CATEGORY_STATISTICS":
                categoryStatistics(request);
                break;
            default:
                throw new Exception("Unknown command: " + command);
        }
    }

    private void addCategory(Request request) throws Exception {
        Category newCategory = gson.fromJson(request.getData(), Category.class);
        categoryService.saveEntity(newCategory);
        sendResponse("SUCCESS", "Категория успешно добавлена");
    }

    private void editCategory(Request request) throws Exception {
        Category updatedCategory = gson.fromJson(request.getData(), Category.class);
        categoryService.updateEntity(updatedCategory);
        sendResponse("SUCCESS", "Категория успешно обновлена");
    }

    private void deleteCategory(Request request) throws Exception {
        Category categoryToDelete = gson.fromJson(request.getData(), Category.class);
        categoryService.deleteEntity(categoryToDelete);
        sendResponse("SUCCESS", "Категория успешно удалена");
    }

    private void loadCategories(Request request) throws Exception {
        List<Category> categories = categoryService.findAllEntities();
        String jsonResponse = gson.toJson(categories);
        sendResponse("SUCCESS", jsonResponse);
    }

    private void searchCategories(Request request) throws Exception {
        String name = request.getData();
        List<Category> filteredCategories = categoryService.findCategoriesByName(name);
        String jsonResponse = gson.toJson(filteredCategories);
        sendResponse("SUCCESS", jsonResponse);
    }

    private void categoryStatistics(Request request) throws Exception {
        List<Category> categories = categoryService.findAllEntities();
        Map<String, Integer> statistics = new HashMap<>();

        for (Category category : categories) {
            int productCount = categoryService.countProductsInCategory(category.getId());
            statistics.put(category.getName(), productCount);
        }

        sendResponse("SUCCESS", gson.toJson(statistics));
    }
}
