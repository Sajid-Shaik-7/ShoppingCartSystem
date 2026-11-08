// CONCEPT: Arrays - Order[] stores all orders in memory (size 100)
public class OrderManager {
    private Order[] orders;
    private int orderCount;
    private int nextId;

    // Creates an empty order list. First order id is 1001.
    public OrderManager() {
        orders = new Order[100];
        orderCount = 0;
        nextId = 1001;
    }

    // Saves a new order with status "Ongoing" and returns it.
    public Order addOrder(String customerName, String itemsSummary, double grandTotal) {
        Order order = new Order(nextId, customerName, itemsSummary, grandTotal, "Ongoing");
        orders[orderCount] = order;
        orderCount = orderCount + 1;
        nextId = nextId + 1;
        return order;
    }

    // Marks the order as Completed, or throws if missing / already completed.
    public void markCompleted(int orderId) throws Exception {
        Order order = findById(orderId);
        if (order == null) {
            throw new Exception("Order not found.");
        }
        if (order.getStatus().equalsIgnoreCase("Completed")) {
            throw new Exception("This order is already completed.");
        }
        order.setStatus("Completed");
    }

    // Finds one order by id, or returns null.
    public Order findById(int orderId) {
        int i = 0;
        while (i < orderCount) {
            if (orders[i].getOrderId() == orderId) {
                return orders[i];
            }
            i = i + 1;
        }
        return null;
    }

    // Counts how many orders have the given status (Ongoing or Completed).
    public int countByStatus(String status) {
        int count = 0;
        int i = 0;
        while (i < orderCount) {
            if (orders[i].getStatus().equalsIgnoreCase(status)) {
                count = count + 1;
            }
            i = i + 1;
        }
        return count;
    }

    // Sums grand totals of Completed orders only (admin revenue).
    public double getTotalRevenue() {
        double sum = 0;
        int i = 0;
        while (i < orderCount) {
            if (orders[i].getStatus().equalsIgnoreCase("Completed")) {
                sum = sum + orders[i].getGrandTotal();
            }
            i = i + 1;
        }
        return sum;
    }

    // Returns how many orders are stored.
    public int getOrderCount() {
        return orderCount;
    }

    // Returns the orders array for tables and "My Orders".
    public Order[] getOrders() {
        return orders;
    }
}
