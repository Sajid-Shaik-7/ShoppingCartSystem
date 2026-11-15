
package service;

import java.time.LocalDate;

import model.BookRental;
import model.Product;

/**
 * Manages all book rental transactions in ShopEase.
 *
 * Responsibilities:
 * 1. Create book rentals.
 * 2. Maintain rental records.
 * 3. Manage rental stock.
 * 4. Process book returns.
 * 5. Track customer rental history.
 * 6. Identify overdue rentals.
 */
public class RentalManager {

    private BookRental[] rentals;
    private int rentalCount;

    /**
     * Creates an empty rental manager.
     */
    public RentalManager() {
        rentals = new BookRental[10];
        rentalCount = 0;
    }

    // =====================================================
    // INTERNAL ARRAY MANAGEMENT
    // =====================================================

    /**
     * Expands the rental array when it becomes full.
     */
    private void ensureCapacity() {

        if (rentalCount == rentals.length) {

            BookRental[] newRentals =
                    new BookRental[rentals.length * 2];

            System.arraycopy(
                rentals,
                0,
                newRentals,
                0,
                rentalCount
            );

            rentals = newRentals;
        }
    }

    // =====================================================
    // CREATE RENTAL
    // =====================================================

    /**
     * Creates a new book rental and deducts rental stock.
     *
     * @param username   customer username
     * @param product    selected book
     * @param quantity   number of copies
     * @param rentalDays rental duration
     * @return created rental
     */
    public synchronized BookRental createRental(
            String username,
            Product product,
            int quantity,
            int rentalDays) {

        if (product == null) {
            throw new IllegalArgumentException(
                "Please select a valid book."
            );
        }

        if (!product.isRentable()) {
            throw new IllegalArgumentException(
                "This book is not available for rental."
            );
        }

        if (!product.isRentalAvailable(quantity, rentalDays)) {
            throw new IllegalArgumentException(
                "Insufficient rental stock or invalid rental duration."
            );
        }

        // Create the rental record.
        BookRental rental = new BookRental(
            username,
            product,
            quantity,
            rentalDays,
            LocalDate.now()
        );

        // Deduct rental stock.
        product.reduceRentalStock(quantity);

        // Save the rental.
        ensureCapacity();
        rentals[rentalCount] = rental;
        rentalCount++;

        return rental;
    }

    // =====================================================
    // FIND RENTAL
    // =====================================================

    /**
     * Finds a rental using its unique rental ID.
     *
     * @param rentalId rental reference
     * @return matching rental or null
     */
    public synchronized BookRental findRentalById(String rentalId) {

        if (rentalId == null || rentalId.trim().isEmpty()) {
            return null;
        }

        for (int i = 0; i < rentalCount; i++) {

            if (rentals[i].getRentalId().equalsIgnoreCase(rentalId)) {
                return rentals[i];
            }
        }

        return null;
    }

    // =====================================================
    // CUSTOMER RENTAL HISTORY
    // =====================================================

    /**
     * Returns all rentals belonging to a customer.
     *
     * @param username customer username
     * @return customer's rental records
     */
    public synchronized BookRental[] getRentalsByCustomer(String username) {

        if (username == null || username.trim().isEmpty()) {
            return new BookRental[0];
        }

        int count = 0;

        for (int i = 0; i < rentalCount; i++) {

            if (rentals[i].getCustomerUsername().equalsIgnoreCase(username)) {
                count++;
            }
        }

        BookRental[] customerRentals = new BookRental[count];

        int index = 0;

        for (int i = 0; i < rentalCount; i++) {

            if (rentals[i].getCustomerUsername().equalsIgnoreCase(username)) {
                customerRentals[index++] = rentals[i];
            }
        }

        return customerRentals;
    }

    // =====================================================
    // RETURN BOOK
    // =====================================================

    /**
     * Processes the return of a rented book.
     *
     * Restores rental stock only after a successful return.
     *
     * @param rentalId rental reference
     */
    public synchronized void returnRental(String rentalId) {

        BookRental rental = findRentalById(rentalId);

        if (rental == null) {
            throw new IllegalArgumentException(
                "Rental record not found."
            );
        }

        if (rental.isReturned()) {
            throw new IllegalStateException(
                "This book has already been returned."
            );
        }

        // Mark the rental as returned.
        rental.markReturned();

        // Restore available rental stock.
        rental.getProduct().returnRentalStock(
            rental.getQuantity()
        );
    }

    // =====================================================
    // OVERDUE RENTALS
    // =====================================================

    /**
     * Returns all currently overdue rentals.
     *
     * @return overdue rental records
     */
    public synchronized BookRental[] getOverdueRentals() {

        int count = 0;

        for (int i = 0; i < rentalCount; i++) {

            rentals[i].refreshStatus();

            if (rentals[i].isOverdue()) {
                count++;
            }
        }

        BookRental[] overdueRentals = new BookRental[count];

        int index = 0;

        for (int i = 0; i < rentalCount; i++) {

            if (rentals[i].isOverdue()) {
                overdueRentals[index++] = rentals[i];
            }
        }

        return overdueRentals;
    }

    // =====================================================
    // ALL RENTALS
    // =====================================================

    /**
     * Returns a copy of the complete rental array.
     *
     * @return all rental records
     */
    public synchronized BookRental[] getAllRentals() {

        BookRental[] result = new BookRental[rentalCount];

        System.arraycopy(
            rentals,
            0,
            result,
            0,
            rentalCount
        );

        return result;
    }

    // =====================================================
    // STATISTICS
    // =====================================================

    public synchronized int getRentalCount() {
        return rentalCount;
    }

    public synchronized int getActiveRentalCount() {

        int count = 0;

        for (int i = 0; i < rentalCount; i++) {

            rentals[i].refreshStatus();

            if (rentals[i].isActive() || rentals[i].isOverdue()) {
                count++;
            }
        }

        return count;
    }

    public synchronized int getReturnedRentalCount() {

        int count = 0;

        for (int i = 0; i < rentalCount; i++) {

            if (rentals[i].isReturned()) {
                count++;
            }
        }

        return count;
    }
}
