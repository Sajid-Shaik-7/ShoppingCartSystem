// CONCEPT: Interface - PercentageDiscount implements Discountable
public class PercentageDiscount implements Discountable {
    private double percent;

    // Saves the percent off, for example 10 means 10%.
    public PercentageDiscount(double percent) {
        this.percent = percent;
    }

    // Returns percent of the amount as the discount.
    public double getDiscount(double amount) {
        return amount * percent / 100.0;
    }
}
