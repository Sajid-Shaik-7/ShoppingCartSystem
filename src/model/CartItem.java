
package model;

/**
 * CartItem represents one product entry in the shopping cart.
 *
 * Concepts:
 * 1. Encapsulation
 * 2. Object Composition
 * 3. Data Validation
 * 4. Business Logic
 */
public class CartItem {

    private final Product product;
    private int quantity;

    /**
     * Creates a cart item with a product and quantity.
     */
    public CartItem(Product product, int quantity) {

        if (product == null) {
            throw new IllegalArgumentException(
                "Product cannot be null."
            );
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Quantity must be greater than zero."
            );
        }

        if (!product.isAvailable(quantity)) {
            throw new IllegalArgumentException(
                "Requested quantity is not available in stock."
            );
        }

        this.product = product;
        this.quantity = quantity;
    }

    // ---------------- BASIC GETTERS ----------------

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    // ---------------- QUANTITY MANAGEMENT ----------------

    /**
     * Updates the quantity of this cart item.
     */
    public void setQuantity(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Quantity must be greater than zero."
            );
        }

        if (!product.isAvailable(quantity)) {
            throw new IllegalArgumentException(
                "Requested quantity exceeds available stock."
            );
        }

        this.quantity = quantity;
    }

    /**
     * Adds one unit to the current quantity.
     */
    public void increaseQuantity() {
        setQuantity(quantity + 1);
    }

    /**
     * Removes one unit from the current quantity.
     * Quantity cannot become zero.
     */
    public void decreaseQuantity() {

        if (quantity <= 1) {
            throw new IllegalArgumentException(
                "Quantity cannot be less than one. Remove the item instead."
            );
        }

        quantity--;
    }

    /**
     * Adds multiple units.
     */
    public void increaseQuantity(int amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                "Increase amount must be greater than zero."
            );
        }

        if (amount > Integer.MAX_VALUE - quantity) {
            throw new IllegalArgumentException(
                "Quantity is too large."
            );
        }

        setQuantity(quantity + amount);
    }

    // ---------------- PRICE CALCULATIONS ----------------

    /**
     * Returns the original price multiplied by quantity.
     */
    public double getOriginalLineTotal() {
        return product.getPrice() * quantity;
    }

    /**
     * Returns the discounted price multiplied by quantity.
     */
    public double getLineTotal() {
        return product.getDiscountedPrice() * quantity;
    }

    /**
     * Returns the total savings for this cart item.
     */
    public double getDiscountAmount() {
        return getOriginalLineTotal() - getLineTotal();
    }

    /**
     * Checks whether the product has a discount.
     */
    public boolean hasDiscount() {
        return product.hasDiscount();
    }

    // ---------------- STOCK VALIDATION ----------------

    /**
     * Checks whether the current quantity is still available.
     */
    public boolean isStockAvailable() {
        return product.isAvailable(quantity);
    }

    // ---------------- UTILITY ----------------

    @Override
    public String toString() {
        return product.getName()
                + " x " + quantity
                + " = Rs. " + String.format("%.2f", getLineTotal());
    }
}
