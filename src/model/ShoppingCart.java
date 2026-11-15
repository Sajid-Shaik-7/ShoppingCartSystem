
package model;

import exception.InvalidQuantityException;
import exception.OutOfStockException;

/**
 * ShoppingCart manages products selected by a customer.
 *
 * Concepts:
 * 1. Arrays and Dynamic Array Expansion
 * 2. Method Overloading
 * 3. Searching and Removing Elements
 * 4. Exception Handling
 * 5. Encapsulation
 * 6. Business Logic
 */
public class ShoppingCart {

    private CartItem[] items;
    private int itemCount;

    // Zero means no budget limit.
    private double budgetLimit;

    private static final int INITIAL_CAPACITY = 10;

    /**
     * Creates an empty shopping cart.
     */
    public ShoppingCart() {
        items = new CartItem[INITIAL_CAPACITY];
        itemCount = 0;
        budgetLimit = 0;
    }

    // ---------------- ADD TO CART ----------------

    /**
     * Adds one unit of a product.
     */
    public void addToCart(Product product)
            throws InvalidQuantityException, OutOfStockException {

        addToCart(product, 1);
    }

    /**
     * Adds a product with the requested quantity.
     * If it already exists, increases its quantity.
     */
    public void addToCart(Product product, int quantity)
            throws InvalidQuantityException, OutOfStockException {

        if (product == null) {
            throw new InvalidQuantityException(
                "Please select a valid product."
            );
        }

        checkQuantity(quantity, product.getStock());

        if (!product.isActive()) {
            throw new OutOfStockException(
                "This product is currently unavailable."
            );
        }

        int found = findIndexByProductId(product.getId());

        if (found >= 0) {

            int currentQuantity = items[found].getQuantity();

            if (quantity > Integer.MAX_VALUE - currentQuantity) {
                throw new InvalidQuantityException(
                    "Requested quantity is too large."
                );
            }

            int newQuantity = currentQuantity + quantity;

            checkQuantity(newQuantity, product.getStock());

            items[found].setQuantity(newQuantity);

            return;
        }

        ensureCapacity();

        items[itemCount] = new CartItem(product, quantity);

        itemCount++;
    }

    // ---------------- VALIDATION ----------------

    /**
     * Validates quantity and available stock.
     */
    public void checkQuantity(int quantity, int stock)
            throws InvalidQuantityException, OutOfStockException {

        if (quantity <= 0) {
            throw new InvalidQuantityException(
                "Quantity must be greater than zero."
            );
        }

        if (quantity > stock) {
            throw new OutOfStockException(
                "Only " + stock + " item(s) left in stock."
            );
        }
    }

    // ---------------- ARRAY MANAGEMENT ----------------

    /**
     * Automatically expands the array when capacity is full.
     */
    private void ensureCapacity() {

        if (itemCount == items.length) {

            int newCapacity = items.length * 2;

            CartItem[] newItems = new CartItem[newCapacity];

            int i = 0;

            while (i < itemCount) {
                newItems[i] = items[i];
                i++;
            }

            items = newItems;
        }
    }

    // ---------------- SEARCH PRODUCT ----------------

    /**
     * Returns the index of a product.
     * Returns -1 when the product is not found.
     */
    public int findIndexByProductId(int productId) {

        int i = 0;

        while (i < itemCount) {

            if (items[i].getProduct().getId() == productId) {
                return i;
            }

            i++;
        }

        return -1;
    }

    // ---------------- REMOVE ITEM ----------------

    /**
     * Removes a product and shifts remaining elements left.
     */
    public void removeItem(int index)
            throws InvalidQuantityException {

        if (index < 0 || index >= itemCount) {
            throw new InvalidQuantityException(
                "Please select a valid cart item."
            );
        }

        int i = index;

        while (i < itemCount - 1) {

            items[i] = items[i + 1];

            i++;
        }

        items[itemCount - 1] = null;

        itemCount--;
    }

    /**
     * Removes a product using its product ID.
     */
    public void removeProduct(int productId)
            throws InvalidQuantityException {

        int index = findIndexByProductId(productId);

        if (index == -1) {
            throw new InvalidQuantityException(
                "Product not found in your cart."
            );
        }

        removeItem(index);
    }

    // ---------------- UPDATE QUANTITY ----------------

    /**
     * Updates the quantity of an existing cart item.
     */
    public void updateQty(int index, int quantity)
            throws InvalidQuantityException, OutOfStockException {

        if (index < 0 || index >= itemCount) {
            throw new InvalidQuantityException(
                "Please select a valid cart item."
            );
        }

        Product product = items[index].getProduct();

        checkQuantity(quantity, product.getStock());

        if (!product.isActive()) {
            throw new OutOfStockException(
                "This product is currently unavailable."
            );
        }

        items[index].setQuantity(quantity);
    }

    /**
     * Increases the quantity of an item by one.
     */
    public void increaseQty(int index)
            throws InvalidQuantityException, OutOfStockException {

        if (index < 0 || index >= itemCount) {
            throw new InvalidQuantityException(
                "Please select a valid cart item."
            );
        }

        updateQty(index, items[index].getQuantity() + 1);
    }

    /**
     * Decreases the quantity of an item by one.
     */
    public void decreaseQty(int index)
            throws InvalidQuantityException, OutOfStockException {

        if (index < 0 || index >= itemCount) {
            throw new InvalidQuantityException(
                "Please select a valid cart item."
            );
        }

        int newQuantity = items[index].getQuantity() - 1;

        if (newQuantity <= 0) {
            removeItem(index);
            return;
        }

        updateQty(index, newQuantity);
    }

    // ---------------- PRICE CALCULATIONS ----------------

    /**
     * Returns the discounted subtotal of all cart items.
     */
    public double getSubtotal() {

        double sum = 0;

        int i = 0;

        while (i < itemCount) {

            sum += items[i].getLineTotal();

            i++;
        }

        return sum;
    }

    /**
     * Returns the subtotal before product discounts.
     */
    public double getOriginalSubtotal() {

        double sum = 0;

        int i = 0;

        while (i < itemCount) {

            sum += items[i].getOriginalLineTotal();

            i++;
        }

        return sum;
    }

    /**
     * Returns total savings from product discounts.
     */
    public double getTotalSavings() {

        return getOriginalSubtotal() - getSubtotal();
    }

    // ---------------- CART INFORMATION ----------------

    /**
     * Returns the number of different products.
     */
    public int getItemCount() {
        return itemCount;
    }

    /**
     * Returns the total number of individual units.
     */
    public int getTotalQuantity() {

        int total = 0;

        int i = 0;

        while (i < itemCount) {

            total += items[i].getQuantity();

            i++;
        }

        return total;
    }

    /**
     * Returns a copy of the cart array.
     * Prevents external code from modifying the internal array.
     */
    public CartItem[] getItems() {

        CartItem[] copy = new CartItem[itemCount];

        int i = 0;

        while (i < itemCount) {

            copy[i] = items[i];

            i++;
        }

        return copy;
    }

    public boolean isEmpty() {
        return itemCount == 0;
    }

    // ---------------- BUDGET MANAGEMENT ----------------

    /**
     * Sets the customer's shopping budget.
     * Zero means unlimited budget.
     */
    public void setBudgetLimit(double budgetLimit) {

        if (!Double.isFinite(budgetLimit) || budgetLimit < 0) {
            throw new IllegalArgumentException(
                "Budget cannot be negative or invalid."
            );
        }

        this.budgetLimit = budgetLimit;
    }

    public double getBudgetLimit() {
        return budgetLimit;
    }

    /**
     * Checks whether the cart is within the budget.
     */
    public boolean isWithinBudget() {

        return budgetLimit == 0 || getSubtotal() <= budgetLimit;
    }

    /**
     * Returns the remaining budget.
     */
    public double getRemainingBudget() {

        if (budgetLimit == 0) {
            return Double.POSITIVE_INFINITY;
        }

        return budgetLimit - getSubtotal();
    }

    // ---------------- STOCK VALIDATION ----------------

    /**
     * Checks whether every cart item is still available.
     */
    public boolean validateStock() {

        int i = 0;

        while (i < itemCount) {

            CartItem item = items[i];

            if (!item.isStockAvailable()) {
                return false;
            }

            i++;
        }

        return true;
    }

    // ---------------- CART SUMMARY ----------------

    /**
     * Creates a summary such as:
     * Laptop x1, Mouse x2
     */
    public String getItemsSummary() {

        StringBuilder sb = new StringBuilder();

        int i = 0;

        while (i < itemCount) {

            if (i > 0) {
                sb.append(", ");
            }

            sb.append(items[i].getProduct().getName());

            sb.append(" x");

            sb.append(items[i].getQuantity());

            i++;
        }

        return sb.toString();
    }

    // ---------------- CLEAR CART ----------------

    /**
     * Removes all products from the cart.
     */
    public void clear() {

        int i = 0;

        while (i < itemCount) {

            items[i] = null;

            i++;
        }

        itemCount = 0;
    }
}
