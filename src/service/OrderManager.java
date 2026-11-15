
package service;

import model.Order;

/**
 * Manages customer orders in ShopEase.
 *
 * Responsibilities:
 * 1. Create and store orders.
 * 2. Generate unique order IDs.
 * 3. Search orders by ID.
 * 4. Update order status.
 * 5. Calculate order statistics and revenue.
 *
 * Concepts:
 * 1. Arrays
 * 2. Encapsulation
 * 3. Searching
 * 4. Dynamic Array Expansion
 */
public class OrderManager {

    private Order[] orders;
    private int orderCount;
    private int nextId;

    /**
     * Creates an empty order manager.
     * First order ID starts from 1001.
     */
    public OrderManager() {
        orders = new Order[10];
        orderCount = 0;
        nextId = 1001;
    }

    /**
     * Expands the array when its capacity is full.
     */
    private void ensureCapacity() {

        if (orderCount >= orders.length) {

            Order[] newOrders = new Order[orders.length * 2];

            System.arraycopy(
                orders, 0,
                newOrders, 0,
                orderCount
            );

            orders = newOrders;
        }
    }

    /**
     * Creates and stores a new order.
     *
     * @param customerName customer name
     * @param itemsSummary purchased products summary
     * @param grandTotal final order amount
     * @return newly created order
     */
    public Order addOrder(
            String customerName,
            String itemsSummary,
            double grandTotal) {

        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Customer name cannot be empty."
            );
        }

        if (itemsSummary == null || itemsSummary.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Order items cannot be empty."
            );
        }

        if (!Double.isFinite(grandTotal) || grandTotal < 0) {
            throw new IllegalArgumentException(
                "Order total must be a valid non-negative amount."
            );
        }

        if (nextId == Integer.MAX_VALUE) {
            throw new IllegalStateException(
                "No more order IDs are available."
            );
        }

        ensureCapacity();

        Order order = new Order(
            nextId,
            customerName.trim(),
            itemsSummary,
            grandTotal,
            Order.ONGOING
        );

        orders[orderCount] = order;

        orderCount++;
        nextId++;

        return order;
    }

    /**
     * Marks an order as completed.
     *
     * @param orderId order ID
     * @throws Exception if order is missing or already completed
     */
    public void markCompleted(int orderId) throws Exception {

        Order order = findById(orderId);

        if (order == null) {
            throw new Exception("Order not found.");
        }

        if (order.isCompleted()) {
            throw new Exception(
                "This order is already completed."
            );
        }

        if (order.isCancelled()) {
            throw new Exception(
                "Cancelled orders cannot be completed."
            );
        }

        order.setStatus(Order.COMPLETED);
    }

    /**
     * Updates the status of an existing order.
     *
     * @param orderId order ID
     * @param newStatus new order status
     */
    public void updateOrderStatus(int orderId, String newStatus) {

        Order order = findById(orderId);

        if (order == null) {
            throw new IllegalArgumentException("Order not found.");
        }

        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Order status cannot be empty."
            );
        }

        order.setStatus(newStatus.trim());
    }

    /**
     * Finds an order using its ID.
     *
     * @param orderId order ID
     * @return matching order, or null if not found
     */
    public Order findById(int orderId) {

        for (int i = 0; i < orderCount; i++) {

            if (orders[i].getOrderId() == orderId) {
                return orders[i];
            }
        }

        return null;
    }

    /**
     * Returns the number of orders with a particular status.
     *
     * @param status status to search
     * @return matching order count
     */
    public int countByStatus(String status) {

        if (status == null) {
            return 0;
        }

        int count = 0;

        for (int i = 0; i < orderCount; i++) {

            if (orders[i].getStatus().equalsIgnoreCase(status.trim())) {
                count++;
            }
        }

        return count;
    }

    /**
     * Calculates revenue from completed orders only.
     *
     * @return total completed-order revenue
     */
    public double getTotalRevenue() {

        double totalRevenue = 0;

        for (int i = 0; i < orderCount; i++) {

            if (orders[i].getStatus().equalsIgnoreCase(Order.COMPLETED)) {
                totalRevenue += orders[i].getGrandTotal();
            }
        }

        return totalRevenue;
    }

    /**
     * Returns the total number of stored orders.
     */
    public int getOrderCount() {
        return orderCount;
    }

    /**
     * Returns a copy of the stored orders array.
     * The returned array contains only actual orders.
     */
    public Order[] getOrders() {

        Order[] result = new Order[orderCount];

        System.arraycopy(
            orders, 0,
            result, 0,
            orderCount
        );

        return result;
    }

    /**
     * Returns orders belonging to a particular customer.
     *
     * @param customerName customer name
     * @return matching customer orders
     */
    public Order[] getOrdersByCustomer(String customerName) {

        if (customerName == null || customerName.trim().isEmpty()) {
            return new Order[0];
        }

        int matchingCount = 0;

        for (int i = 0; i < orderCount; i++) {

            if (orders[i].getCustomerName()
                    .equalsIgnoreCase(customerName.trim())) {
                matchingCount++;
            }
        }

        Order[] customerOrders = new Order[matchingCount];

        int index = 0;

        for (int i = 0; i < orderCount; i++) {

            if (orders[i].getCustomerName()
                    .equalsIgnoreCase(customerName.trim())) {

                customerOrders[index] = orders[i];
                index++;
            }
        }

        return customerOrders;
    }
}
