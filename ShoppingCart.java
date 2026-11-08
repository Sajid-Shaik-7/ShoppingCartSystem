// CONCEPT: Arrays - CartItem[] with add, remove (shift left) and update
public class ShoppingCart {
    private CartItem[] items;
    private int itemCount;

    // Makes an empty cart that can hold 50 items.
    public ShoppingCart() {
        items = new CartItem[50];
        itemCount = 0;
    }

    // CONCEPT: Methods - overloaded addToCart with default quantity 1
    public void addToCart(Product p) throws InvalidQuantityException, OutOfStockException {
        addToCart(p, 1);
    }

    // Adds a product with a given quantity, or increases qty if already in cart.
    public void addToCart(Product p, int qty) throws InvalidQuantityException, OutOfStockException {
        checkQuantity(qty, p.getStock());
        int found = findIndexByProductId(p.getId());
        if (found >= 0) {
            int newQty = items[found].getQuantity() + qty;
            checkQuantity(newQty, p.getStock());
            items[found].setQuantity(newQty);
            return;
        }
        items[itemCount] = new CartItem(p, qty);
        itemCount = itemCount + 1;
    }

    // Throws if quantity is not greater than 0 or is more than stock.
    public void checkQuantity(int qty, int stock) throws InvalidQuantityException, OutOfStockException {
        if (qty <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than 0.");
        }
        if (qty > stock) {
            throw new OutOfStockException("Only " + stock + " item(s) left in stock.");
        }
    }

    // Finds the cart index of a product, or -1 if it is not in the cart.
    public int findIndexByProductId(int productId) {
        int i = 0;
        while (i < itemCount) {
            if (items[i].getProduct().getId() == productId) {
                return i;
            }
            i = i + 1;
        }
        return -1;
    }

    // Removes a cart item and shifts the later items one place to the left.
    public void removeItem(int index) throws InvalidQuantityException {
        if (index < 0 || index >= itemCount) {
            throw new InvalidQuantityException("Please select a cart item first.");
        }
        int i = index;
        while (i < itemCount - 1) {
            items[i] = items[i + 1];
            i = i + 1;
        }
        items[itemCount - 1] = null;
        itemCount = itemCount - 1;
    }

    // Sets a new quantity for the selected cart row.
    public void updateQty(int index, int qty) throws InvalidQuantityException, OutOfStockException {
        if (index < 0 || index >= itemCount) {
            throw new InvalidQuantityException("Please select a cart item first.");
        }
        Product p = items[index].getProduct();
        checkQuantity(qty, p.getStock());
        items[index].setQuantity(qty);
    }

    // Adds up all line totals to get the cart subtotal.
    public double getSubtotal() {
        double sum = 0;
        int i = 0;
        while (i < itemCount) {
            sum = sum + items[i].getLineTotal();
            i = i + 1;
        }
        return sum;
    }

    // Returns how many different products are in the cart.
    public int getItemCount() {
        return itemCount;
    }

    // Returns the cart array (used by the bill and the cart table).
    public CartItem[] getItems() {
        return items;
    }

    // Empties the cart after a successful checkout.
    public void clear() {
        int i = 0;
        while (i < itemCount) {
            items[i] = null;
            i = i + 1;
        }
        itemCount = 0;
    }

    // Builds a short text like "Laptop x1, Mouse x2" for the order record.
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
            i = i + 1;
        }
        return sb.toString();
    }
}
