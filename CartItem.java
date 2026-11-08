// CONCEPT: Class - one line in the shopping cart (product + quantity)
public class CartItem {
    private Product product;
    private int quantity;

    // Stores a product and how many of it the user wants.
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Returns the product for this cart line.
    public Product getProduct() {
        return product;
    }

    // Returns the quantity of this product in the cart.
    public int getQuantity() {
        return quantity;
    }

    // Changes the quantity of this cart line.
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Returns price multiplied by quantity for this line.
    public double getLineTotal() {
        return product.getPrice() * quantity;
    }
}
