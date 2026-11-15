
package discount;

/**
 * Implements a percentage-based discount.
 *
 * Example:
 * PercentageDiscount(10) means the customer gets
 * 10% off the bill.
 *
 * Concepts:
 * 1. Interface Implementation
 * 2. Abstraction
 * 3. Encapsulation
 * 4. Polymorphism
 */
public class PercentageDiscount implements Discountable {

    private final double percent;

    /**
     * Creates a percentage discount.
     *
     * @param percent discount percentage (0 to 100)
     */
    public PercentageDiscount(double percent) {

        if (!Double.isFinite(percent) || percent < 0 || percent > 100) {
            throw new IllegalArgumentException(
                "Discount percentage must be between 0 and 100."
            );
        }

        this.percent = percent;
    }

    /**
     * Calculates the percentage discount.
     *
     * @param amount original bill amount
     * @return calculated discount amount
     */
    @Override
    public double getDiscount(double amount) {

        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException(
                "Bill amount must be a valid non-negative number."
            );
        }

        return amount * percent / 100.0;
    }

    public double getPercent() {
        return percent;
    }
}
