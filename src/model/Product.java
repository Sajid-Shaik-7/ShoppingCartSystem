
package model;

/**
 * Product represents an individual item available in ShopEase.
 *
 * Supports:
 * 1. Normal product purchases.
 * 2. Product discounts.
 * 3. Inventory management.
 * 4. Book rental functionality.
 *
 * Concepts:
 * 1. Encapsulation
 * 2. Constructors
 * 3. Getters and Setters
 * 4. Data Validation
 * 5. Business Logic
 */
public class Product {

    // ---------------- BASIC PRODUCT INFORMATION ----------------

    private final int id;
    private String name;
    private String description;
    private String brand;
    private String category;

    // ---------------- PURCHASE PRICING ----------------

    private double price;
    private double discountPercentage;

    // ---------------- PURCHASE INVENTORY ----------------

    private int stock;
    private boolean active;

    // ---------------- PRODUCT DISPLAY ----------------

    private String emoji;
    private String imageUrl;

    // ---------------- CUSTOMER FEEDBACK ----------------

    private double rating;
    private int reviewCount;

    // ---------------- BOOK RENTAL INFORMATION ----------------

    private boolean rentable;
    private double rentalPricePerDay;
    private int rentalStock;
    private double securityDeposit;
    private int maxRentalDays;

    // =====================================================
    // ORIGINAL CONSTRUCTOR
    // =====================================================

    /**
     * Preserves compatibility with existing ShopEase code.
     * Rental is disabled by default.
     */
    public Product(
            int id,
            String name,
            double price,
            int stock,
            String category,
            String emoji) {

        this(
            id, name, "", "", price, stock, category,
            emoji, "", 0.0, 0, 0.0
        );
    }

    // =====================================================
    // COMPLETE PRODUCT CONSTRUCTOR
    // =====================================================

    public Product(
            int id,
            String name,
            String description,
            String brand,
            double price,
            int stock,
            String category,
            String emoji,
            String imageUrl,
            double rating,
            int reviewCount,
            double discountPercentage) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                "Product ID must be positive."
            );
        }

        this.id = id;

        setName(name);
        setDescription(description);
        setBrand(brand);
        setPrice(price);
        setStock(stock);
        setCategory(category);
        setEmoji(emoji);
        setImageUrl(imageUrl);
        setRating(rating);
        setReviewCount(reviewCount);
        setDiscountPercentage(discountPercentage);

        this.active = true;

        // Rental disabled by default.
        this.rentable = false;
        this.rentalPricePerDay = 0.0;
        this.rentalStock = 0;
        this.securityDeposit = 0.0;
        this.maxRentalDays = 30;
    }

    // =====================================================
    // BASIC GETTERS
    // =====================================================

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory() {
        return category;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // =====================================================
    // PURCHASE PRICE METHODS
    // =====================================================

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {

        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException(
                "Price cannot be negative or invalid."
            );
        }

        this.price = price;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {

        if (!Double.isFinite(discountPercentage)
                || discountPercentage < 0
                || discountPercentage > 100) {

            throw new IllegalArgumentException(
                "Discount must be between 0 and 100."
            );
        }

        this.discountPercentage = discountPercentage;
    }

    public double getDiscountedPrice() {
        return price - (price * discountPercentage / 100.0);
    }

    public double getDiscountAmount() {
        return price - getDiscountedPrice();
    }

    public boolean hasDiscount() {
        return discountPercentage > 0;
    }

    // =====================================================
    // PURCHASE INVENTORY METHODS
    // =====================================================

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {

        if (stock < 0) {
            throw new IllegalArgumentException(
                "Stock cannot be negative."
            );
        }

        this.stock = stock;
    }

    /**
     * Checks purchase stock availability.
     */
    public boolean isAvailable(int quantity) {

        return active
            && quantity > 0
            && stock >= quantity;
    }

    /**
     * Reduces stock after a successful purchase.
     */
    public void reduceStock(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Quantity must be greater than zero."
            );
        }

        if (quantity > stock) {
            throw new IllegalArgumentException(
                "Insufficient stock available."
            );
        }

        stock -= quantity;
    }

    /**
     * Adds new purchase stock.
     */
    public void increaseStock(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Quantity must be greater than zero."
            );
        }

        if (quantity > Integer.MAX_VALUE - stock) {
            throw new IllegalArgumentException(
                "Stock value is too large."
            );
        }

        stock += quantity;
    }

    // =====================================================
    // BOOK RENTAL METHODS
    // =====================================================

    /**
     * Returns whether this product supports rental.
     */
    public boolean isRentable() {
        return rentable;
    }

    /**
     * Enables or disables book rental.
     *
     * Rental can only be enabled for products in the Books category.
     */
    public void setRentable(boolean rentable) {

        if (rentable
                && !"Books".equalsIgnoreCase(category)) {

            throw new IllegalArgumentException(
                "Only products in the Books category can be rented."
            );
        }

        this.rentable = rentable;

        if (!rentable) {
            rentalStock = 0;
        }
    }

    /**
     * Returns the rental price per day.
     */
    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }

    public void setRentalPricePerDay(double rentalPricePerDay) {

        if (!Double.isFinite(rentalPricePerDay)
                || rentalPricePerDay < 0) {

            throw new IllegalArgumentException(
                "Daily rental price cannot be negative or invalid."
            );
        }

        this.rentalPricePerDay = rentalPricePerDay;
    }

    /**
     * Returns the number of copies available for rental.
     */
    public int getRentalStock() {
        return rentalStock;
    }

    public void setRentalStock(int rentalStock) {

        if (rentalStock < 0) {
            throw new IllegalArgumentException(
                "Rental stock cannot be negative."
            );
        }

        this.rentalStock = rentalStock;
    }

    /**
     * Returns the refundable security deposit.
     */
    public double getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(double securityDeposit) {

        if (!Double.isFinite(securityDeposit)
                || securityDeposit < 0) {

            throw new IllegalArgumentException(
                "Security deposit cannot be negative or invalid."
            );
        }

        this.securityDeposit = securityDeposit;
    }

    /**
     * Returns the maximum rental duration.
     */
    public int getMaxRentalDays() {
        return maxRentalDays;
    }

    public void setMaxRentalDays(int maxRentalDays) {

        if (maxRentalDays <= 0) {
            throw new IllegalArgumentException(
                "Maximum rental days must be greater than zero."
            );
        }

        this.maxRentalDays = maxRentalDays;
    }

    /**
     * Checks whether a requested rental duration and
     * number of copies are valid.
     */
    public boolean isRentalAvailable(
            int quantity,
            int rentalDays) {

        return active
            && rentable
            && quantity > 0
            && rentalDays > 0
            && rentalDays <= maxRentalDays
            && rentalStock >= quantity
            && rentalPricePerDay > 0;
    }

    /**
     * Calculates rental charge without the deposit.
     */
    public double calculateRentalCost(int rentalDays) {

        if (!rentable) {
            throw new IllegalStateException(
                "This product is not available for rental."
            );
        }

        if (rentalDays <= 0 || rentalDays > maxRentalDays) {
            throw new IllegalArgumentException(
                "Rental duration must be between 1 and "
                + maxRentalDays + " days."
            );
        }

        return rentalPricePerDay * rentalDays;
    }

    /**
     * Calculates total rental charge for multiple copies.
     */
    public double calculateRentalCost(
            int rentalDays,
            int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Rental quantity must be greater than zero."
            );
        }

        return calculateRentalCost(rentalDays) * quantity;
    }

    /**
     * Calculates total amount collected including deposit.
     */
    public double calculateRentalPayable(
            int rentalDays,
            int quantity) {

        double rentalCost = calculateRentalCost(
            rentalDays,
            quantity
        );

        double totalDeposit = securityDeposit * quantity;

        return rentalCost + totalDeposit;
    }

    /**
     * Reduces rental stock after a successful rental.
     */
    public void reduceRentalStock(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Rental quantity must be greater than zero."
            );
        }

        if (!rentable || !active) {
            throw new IllegalStateException(
                "This book is not available for rental."
            );
        }

        if (quantity > rentalStock) {
            throw new IllegalArgumentException(
                "Insufficient rental copies available."
            );
        }

        rentalStock -= quantity;
    }

    /**
     * Restores rental stock after a returned book
     * has been checked and accepted.
     */
    public void returnRentalStock(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Return quantity must be greater than zero."
            );
        }

        if (quantity > Integer.MAX_VALUE - rentalStock) {
            throw new IllegalArgumentException(
                "Rental stock value is too large."
            );
        }

        rentalStock += quantity;
    }

    // =====================================================
    // PRODUCT DETAILS
    // =====================================================

    public void setName(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Product name cannot be empty."
            );
        }

        this.name = name.trim();
    }

    public void setDescription(String description) {

        this.description = description == null
            ? ""
            : description.trim();
    }

    public void setBrand(String brand) {

        this.brand = brand == null
            ? ""
            : brand.trim();
    }

    public void setCategory(String category) {

        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Product category cannot be empty."
            );
        }

        String newCategory = category.trim();

        if (rentable && !"Books".equalsIgnoreCase(newCategory)) {
            throw new IllegalArgumentException(
                "A rentable product must belong to the Books category."
            );
        }

        this.category = newCategory;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji == null ? "" : emoji;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl == null ? "" : imageUrl.trim();
    }

    // =====================================================
    // RATINGS
    // =====================================================

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {

        if (!Double.isFinite(rating)
                || rating < 0
                || rating > 5) {

            throw new IllegalArgumentException(
                "Rating must be between 0 and 5."
            );
        }

        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {

        if (reviewCount < 0) {
            throw new IllegalArgumentException(
                "Review count cannot be negative."
            );
        }

        this.reviewCount = reviewCount;
    }

    // =====================================================
    // PRODUCT STATUS
    // =====================================================

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // =====================================================
    // UTILITY
    // =====================================================

    @Override
    public String toString() {

        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", discountPrice=" + getDiscountedPrice() +
                ", stock=" + stock +
                ", category='" + category + '\'' +
                ", rentable=" + rentable +
                ", rentalPricePerDay=" + rentalPricePerDay +
                ", rentalStock=" + rentalStock +
                '}';
    }
}
