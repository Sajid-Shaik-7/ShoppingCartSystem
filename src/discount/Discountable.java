
package discount;

/**
 * Interface defining a common contract for discount calculations.
 *
 * Implemented by:
 * 1. PercentageDiscount
 * 2. FlatDiscount
 *
 * Concepts:
 * 1. Interface
 * 2. Abstraction
 * 3. Polymorphism
 */
public interface Discountable {

    /**
     * Calculates the discount amount to subtract
     * from the given original amount.
     *
     * @param amount original price or subtotal
     * @return discount amount
     */
    double getDiscount(double amount);
}
