package Utility;

import DAO.AddressDAO;
import Entities.Address;
import Entities.OrderDetails;
import Entities.OrderTable;
import Entities.OrderProduct;
import Services.OrderDetailsService;
import Services.OrderTableService;
import Services.OrderProductService;
import Services.ProductService;
import TCP.Request;
import com.google.gson.Gson;

import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderHandler extends BaseHandler {
    private final OrderTableService orderTableService;
    private final OrderProductService orderProductService;
    private final OrderDetailsService orderDetailsService;
    private final ProductService productService;
    private final Connection connection;
    private final Gson gson;

    public OrderHandler(OrderTableService orderTableService, OrderProductService orderProductService,
                        OrderDetailsService orderDetailsService, ProductService productService,
                        Connection connection, ObjectOutputStream out) {
        super(out);
        this.orderTableService = orderTableService;
        this.orderProductService = orderProductService;
        this.orderDetailsService = orderDetailsService;
        this.productService = productService;
        this.connection = connection;
        this.gson = GsonConfig.createGson();
    }

    @Override
    protected void processRequest(Request request) throws Exception {
        String command = request.getCommand().toUpperCase();

        switch (command) {
            case "CREATE_ORDER":
                createOrder(request);
                break;
            case "LOAD_ORDERS":
                loadOrders(request);
                break;
            case "ORDER_DETAILS":
                loadOrderDetails(request);
                break;
            case "LOAD_USER_ORDERS":
                loadUserOrders(request);
                break;
            case "LOAD_ALL_ORDERS":
                loadAllOrders(request);
                break;
            case "GET_TOTAL_ORDER_AMOUNT":
                getTotalOrderAmount(request);
                break;
            default:
                throw new Exception("Unknown command: " + command);
        }
    }

    private void createOrder(Request request) throws Exception {
        OrderTable order = gson.fromJson(request.getData(), OrderTable.class);

        // Сохранение адреса
        Address address = order.getAddress();
        if (address != null) {
            AddressDAO addressDAO = new AddressDAO(connection);
            addressDAO.save(address);
            order.setAddress(address);
        }

        // Сохранение заказа
        orderTableService.saveEntity(order);

        // Добавление продуктов к заказу
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            orderProduct.setOrderId(order.getOrderId());
            orderProductService.saveEntity(orderProduct);

            // Уменьшение количества товара
            productService.updateProductQuantity(orderProduct.getProduct().getId(), orderProduct.getProduct().getQuantity());
        }

        // Сохранение деталей заказа
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderId(order.getOrderId());
        orderDetails.setDateTime(LocalDateTime.now());
        orderDetailsService.saveEntity(orderDetails);

        sendResponse("SUCCESS", "Заказ успешно добавлен по ID: " + order.getOrderId());
    }

    private void loadOrders(Request request) throws Exception {
        List<OrderTable> orders = orderTableService.findAllEntities();
        sendResponse("SUCCESS", gson.toJson(orders));
    }

    private void loadOrderDetails(Request request) throws Exception {
        int orderId = Integer.parseInt(request.getData());
        List<OrderProduct> orderProducts = orderProductService.findProductsByOrderId(orderId);
        sendResponse("SUCCESS", gson.toJson(orderProducts));
    }

    private void loadUserOrders(Request request) throws Exception {
        int userId = Integer.parseInt(request.getData());
        List<OrderTable> userOrders = orderTableService.findOrdersByUserId(userId);

        for (OrderTable order : userOrders) {
            order.setOrderDetails(orderDetailsService.findDetailsByOrderId(order.getOrderId()));
            List<OrderProduct> products = orderProductService.findProductsByOrderId(order.getOrderId());
            order.setOrderProducts(products != null ? products : new ArrayList<>());
        }

        sendResponse("SUCCESS", gson.toJson(userOrders));
    }

    private void loadAllOrders(Request request) throws Exception {
        List<OrderTable> allOrders = orderTableService.findAllEntities();

        for (OrderTable order : allOrders) {
            OrderDetails details = orderDetailsService.findDetailsByOrderId(order.getOrderId());
            order.setOrderDetails(details != null ? details : new OrderDetails());

            List<OrderProduct> products = orderProductService.findProductsByOrderId(order.getOrderId());
            order.setOrderProducts(products != null ? products : new ArrayList<>());
        }

        sendResponse("SUCCESS", gson.toJson(allOrders));
    }

    private void getTotalOrderAmount(Request request) throws Exception {
        int userId = Integer.parseInt(request.getData());
        double totalAmount = orderTableService.getTotalOrderAmountByUserId(userId);
        sendResponse("SUCCESS", String.format("%.2f", totalAmount));
    }
}
