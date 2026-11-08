// CONCEPT: Interface - FlatDiscount implements Discountable
public class FlatDiscount implements Discountable {
    private double flatAmount;

    // Saves a fixed rupee amount to subtract, for example 200.
    public FlatDiscount(double flatAmount) {
        this.flatAmount = flatAmount;
    }

    // Returns the flat amount, but never more than the bill amount.
    public double getDiscount(double amount) {
        if (amount < flatAmount) {
            return amount;
        }
        return flatAmount;
    }
}
