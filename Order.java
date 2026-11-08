// CONCEPT: Class - one customer order stored for tracking
public class Order {
    private int orderId;
    private String customerName;
    private String itemsSummary;
    private double grandTotal;
    private String status;

    // Creates an order. Status is "Ongoing" or "Completed".
    public Order(int orderId, String customerName, String itemsSummary,
            double grandTotal, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.itemsSummary = itemsSummary;
        this.grandTotal = grandTotal;
        this.status = status;
    }

    // Returns the auto-generated order id.
    public int getOrderId() {
        return orderId;
    }

    // Returns the customer who placed the order.
    public String getCustomerName() {
        return customerName;
    }

    // Returns a short text of items in the order.
    public String getItemsSummary() {
        return itemsSummary;
    }

    // Returns the bill grand total.
    public double getGrandTotal() {
        return grandTotal;
    }

    // Returns "Ongoing" or "Completed".
    public String getStatus() {
        return status;
    }

    // Updates the order status.
    public void setStatus(String status) {
        this.status = status;
    }
}
