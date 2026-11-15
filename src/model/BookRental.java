
package model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a book rental transaction in ShopEase.
 *
 * Stores:
 * 1. Rental and customer details
 * 2. Book information
 * 3. Rental period
 * 4. Rental cost and security deposit
 * 5. Return status
 */
public class BookRental {

    // Rental status constants
    public static final String ACTIVE = "Active";
    public static final String RETURNED = "Returned";
    public static final String OVERDUE = "Overdue";

    // Rental information
    private final String rentalId;
    private final String customerUsername;
    private final Product product;

    // Rental quantity and duration
    private final int quantity;
    private final int rentalDays;

    // Rental dates
    private final LocalDate rentalDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    // Financial information
    private final double rentalPricePerDay;
    private final double rentalAmount;
    private final double securityDeposit;
    private final double totalPayable;

    // Rental status
    private String status;

    /**
     * Creates a new book rental.
     *
     * @param customerUsername customer username
     * @param product          book being rented
     * @param quantity         number of copies
     * @param rentalDays       rental duration in days
     * @param rentalDate       rental starting date
     */
    public BookRental(
            String customerUsername,
            Product product,
            int quantity,
            int rentalDays,
            LocalDate rentalDate) {

        if (customerUsername == null || customerUsername.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Customer username cannot be empty."
            );
        }

        if (product == null) {
            throw new IllegalArgumentException(
                "Book cannot be null."
            );
        }

        if (!product.isRentable()) {
            throw new IllegalArgumentException(
                "This book is not available for rental."
            );
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Rental quantity must be greater than zero."
            );
        }

        if (rentalDays <= 0 ||
                rentalDays > product.getMaxRentalDays()) {
            throw new IllegalArgumentException(
                "Invalid rental duration. Maximum allowed: "
                + product.getMaxRentalDays() + " days."
            );
        }

        if (rentalDate == null) {
            throw new IllegalArgumentException(
                "Rental date cannot be null."
            );
        }

        if (!product.isRentalAvailable(quantity, rentalDays)) {
            throw new IllegalArgumentException(
                "Insufficient rental stock or invalid rental duration."
            );
        }

        this.rentalId = "RNT-" + UUID.randomUUID()
                .toString().substring(0, 8).toUpperCase();

        this.customerUsername = customerUsername;
        this.product = product;
        this.quantity = quantity;
        this.rentalDays = rentalDays;

        this.rentalDate = rentalDate;
        this.dueDate = rentalDate.plusDays(rentalDays);
        this.returnDate = null;

        // Store price snapshots so future product price changes
        // do not modify an existing rental.
        this.rentalPricePerDay = product.getRentalPricePerDay();

        this.rentalAmount =
                product.calculateRentalCost(rentalDays, quantity);

        this.securityDeposit =
                product.getSecurityDeposit() * quantity;

        this.totalPayable =
                rentalAmount + securityDeposit;

        this.status = ACTIVE;
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public String getRentalId() {
        return rentalId;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getRentalDays() {
        return rentalDays;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }

    public double getRentalAmount() {
        return rentalAmount;
    }

    public double getSecurityDeposit() {
        return securityDeposit;
    }

    public double getTotalPayable() {
        return totalPayable;
    }

    public String getStatus() {
        return status;
    }

    // =====================================================
    // RENTAL STATUS
    // =====================================================

    /**
     * Checks whether the rental has been returned.
     */
    public boolean isReturned() {
        return RETURNED.equals(status);
    }

    /**
     * Checks whether the rental is currently active.
     */
    public boolean isActive() {
        return ACTIVE.equals(status);
    }

    /**
     * Checks whether the book is overdue.
     */
    public boolean isOverdue() {

        return !isReturned()
                && LocalDate.now().isAfter(dueDate);
    }

    /**
     * Updates the rental status based on the current date.
     */
    public void refreshStatus() {

        if (isReturned()) {
            return;
        }

        status = isOverdue() ? OVERDUE : ACTIVE;
    }

    /**
     * Marks the rental as returned.
     */
    public void markReturned() {

        if (isReturned()) {
            throw new IllegalStateException(
                "This rental has already been returned."
            );
        }

        this.returnDate = LocalDate.now();
        this.status = RETURNED;
    }

    // =====================================================
    // RENTAL DETAILS
    // =====================================================

    /**
     * Returns the number of days remaining.
     */
    public long getRemainingDays() {

        if (isReturned()) {
            return 0;
        }

        long remaining =
                java.time.temporal.ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    dueDate
                );

        return Math.max(0, remaining);
    }

    /**
     * Returns a readable rental summary.
     */
    public String getRentalSummary() {

        return "Rental ID: " + rentalId
                + "\nBook: " + product.getName()
                + "\nCustomer: " + customerUsername
                + "\nQuantity: " + quantity
                + "\nRental Period: " + rentalDays + " days"
                + "\nRental Date: " + rentalDate
                + "\nDue Date: " + dueDate
                + "\nRental Amount: ₹" + rentalAmount
                + "\nRefundable Deposit: ₹" + securityDeposit
                + "\nTotal Paid: ₹" + totalPayable
                + "\nStatus: " + status;
    }

    @Override
    public String toString() {
        return rentalId + " - "
                + product.getName()
                + " (" + status + ")";
    }
}
