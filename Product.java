// CONCEPT: Class - Product with encapsulated fields, getters and setters
public class Product {
    private int id;
    private String name;
    private double price;
    private int stock;
    private String category;   // e.g. "Electronics", "Fashion"
    private String emoji;      // emoji used as product image placeholder

    // Creates one product with full details including category and emoji icon.
    public Product(int id, String name, double price, int stock,
                   String category, String emoji) {
        this.id       = id;
        this.name     = name;
        this.price    = price;
        this.stock    = stock;
        this.category = category;
        this.emoji    = emoji;
    }

    // Returns the product id.
    public int getId() { return id; }

    // Returns the product name.
    public String getName() { return name; }

    // Returns the product price.
    public double getPrice() { return price; }

    // Returns how many items are still in stock.
    public int getStock() { return stock; }

    // Returns the category string.
    public String getCategory() { return category; }

    // Returns the emoji placeholder image string.
    public String getEmoji() { return emoji; }

    // Updates the stock after adding to cart or checkout.
    public void setStock(int stock) { this.stock = stock; }

    // Updates the price (admin can edit).
    public void setPrice(double price) { this.price = price; }
}
