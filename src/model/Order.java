
package model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Order represents a customer's purchase in ShopEase.
 *
 * Concepts:
 * 1. Encapsulation
 * 2. Constructor Overloading
 * 3. Data Validation
 * 4. Object Initialization
 * 5. Order Lifecycle Management
 */
public class Order {

    // Basic order information
    private final int orderId;
    private final String trackingId;
    private final LocalDateTime orderDate;

    // Customer information
    private String customerName;
    private String deliveryAddress;

    // Product information
    private String itemsSummary;

    // Financial information
    private double subtotal;
    private double discountAmount;
    private double gstAmount;
    private double grandTotal;

    // Order and payment information
    private String status;
    private String paymentMethod;
    private String paymentStatus;

    // Supported order statuses
    public static final String ONGOING = "Ongoing";
    public static final String COMPLETED = "Completed";
    public static final String PLACED = "Placed";
    public static final String CONFIRMED = "Confirmed";
    public static final String PROCESSING = "Processing";
    public static final String SHIPPED = "Shipped";
    public static final String OUT_FOR_DELIVERY = "Out for Delivery";
    public static final String DELIVERED = "Delivered";
    public static final String CANCELLED = "Cancelled";
    public static final String RETURNED = "Returned";

    // Supported payment statuses
    public static final String PAYMENT_PENDING = "Pending";
    public static final String PAYMENT_PAID = "Paid";
    public static final String PAYMENT_FAILED = "Failed";
    public static final String PAYMENT_REFUNDED = "Refunded";

    /**
     * Original constructor.
     * Maintains compatibility with the existing OrderManager.
     */
    public Order(int orderId, String customerName,
                 String itemsSummary, double grandTotal,
                 String status) {

        this(orderId, customerName, itemsSummary,
             grandTotal, status, "", "Not specified");
    }

    /**
     * Extended constructor.
     */
    public Order(int orderId, String customerName,
                 String itemsSummary, double grandTotal,
                 String status, String deliveryAddress,
                 String paymentMethod) {

        if (orderId <= 0) {
            throw new IllegalArgumentException(
                "Order ID must be positive."
            );
        }

        validateText(customerName, "Customer name");
        validateText(itemsSummary, "Order items");

        validateAmount(grandTotal, "Grand total");

        this.orderId = orderId;
        this.trackingId = "SHP-" + UUID.randomUUID()
                                      .toString()
                                      .substring(0, 8)
                                      .toUpperCase();

        this.orderDate = LocalDateTime.now();

        this.customerName = customerName.trim();
        this.itemsSummary = itemsSummary.trim();
        this.deliveryAddress = normalize(deliveryAddress);

        this.grandTotal = grandTotal;

        this.subtotal = grandTotal;
        this.discountAmount = 0;
        this.gstAmount = 0;

        this.status = validateOrderStatus(status);

        this.paymentMethod = normalize(paymentMethod);
        this.paymentStatus = PAYMENT_PENDING;
    }

    // ---------------- BASIC GETTERS ----------------

    public int getOrderId() {
        return orderId;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getItemsSummary() {
        return itemsSummary;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    // ---------------- FINANCIAL GETTERS ----------------

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getGstAmount() {
        return gstAmount;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    // ---------------- STATUS GETTERS ----------------

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    // ---------------- ORDER STATUS MANAGEMENT ----------------

    public void setStatus(String status) {
        this.status = validateOrderStatus(status);
    }

    public boolean isCompleted() {
        return COMPLETED.equalsIgnoreCase(status)
                || DELIVERED.equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return CANCELLED.equalsIgnoreCase(status);
    }

    public boolean isOngoing() {
        return !isCompleted() && !isCancelled();
    }

    // ---------------- PAYMENT MANAGEMENT ----------------

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = validatePaymentStatus(paymentStatus);
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = normalize(paymentMethod);
    }

    // ---------------- CUSTOMER INFORMATION ----------------

    public void setCustomerName(String customerName) {
        validateText(customerName, "Customer name");
        this.customerName = customerName.trim();
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = normalize(deliveryAddress);
    }

    // ---------------- FINANCIAL MANAGEMENT ----------------

    /**
     * Updates the financial breakdown of the order.
     * The total is subtotal - discount + GST.
     */
    public void setFinancialDetails(double subtotal,
                                    double discountAmount,
                                    double gstAmount) {

        validateAmount(subtotal, "Subtotal");
        validateAmount(discountAmount, "Discount");
        validateAmount(gstAmount, "GST");

        if (discountAmount > subtotal) {
            throw new IllegalArgumentException(
                "Discount cannot exceed subtotal."
            );
        }

        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.gstAmount = gstAmount;

        this.grandTotal = subtotal - discountAmount + gstAmount;
    }

    // ---------------- VALIDATION ----------------

    private static void validateText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                field + " cannot be empty."
            );
        }
    }

    private static void validateAmount(double amount, String field) {
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException(
                field + " cannot be negative or invalid."
            );
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private static String validateOrderStatus(String status) {

        validateText(status, "Order status");

        String[] validStatuses = {
            ONGOING,
            COMPLETED,
            PLACED,
            CONFIRMED,
            PROCESSING,
            SHIPPED,
            OUT_FOR_DELIVERY,
            DELIVERED,
            CANCELLED,
            RETURNED
        };

        for (String validStatus : validStatuses) {
            if (validStatus.equalsIgnoreCase(status.trim())) {
                return validStatus;
            }
        }

        throw new IllegalArgumentException(
            "Invalid order status: " + status
        );
    }

    private static String validatePaymentStatus(String status) {

        validateText(status, "Payment status");

        String[] validStatuses = {
            PAYMENT_PENDING,
            PAYMENT_PAID,
            PAYMENT_FAILED,
            PAYMENT_REFUNDED
        };

        for (String validStatus : validStatuses) {
            if (validStatus.equalsIgnoreCase(status.trim())) {
                return validStatus;
            }
        }

        throw new IllegalArgumentException(
            "Invalid payment status: " + status
        );
    }

    // ---------------- UTILITY ----------------

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", trackingId='" + trackingId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", grandTotal=" + grandTotal +
                ", status='" + status + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
