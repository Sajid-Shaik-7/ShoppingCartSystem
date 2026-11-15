
package discount;

/**
 * Implements a fixed-amount discount.
 *
 * Example:
 * FlatDiscount(200) means the customer gets
 * ₹200 off the bill.
 *
 * Concepts:
 * 1. Interface Implementation
 * 2. Abstraction
 * 3. Encapsulation
 */
public class FlatDiscount implements Discountable {

    private final double flatAmount;

    /**
     * Creates a fixed discount.
     *
     * @param flatAmount fixed discount amount in rupees
     */
    public FlatDiscount(double flatAmount) {

        if (!Double.isFinite(flatAmount) || flatAmount < 0) {
            throw new IllegalArgumentException(
                "Discount amount must be a valid non-negative number."
            );
        }

        this.flatAmount = flatAmount;
    }

    /**
     * Calculates the discount without exceeding the bill amount.
     *
     * @param amount original bill amount
     * @return applicable discount amount
     */
    @Override
    public double getDiscount(double amount) {

        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException(
                "Bill amount must be a valid non-negative number."
            );
        }

        return Math.min(flatAmount, amount);
    }

    public double getFlatAmount() {
        return flatAmount;
    }
}
