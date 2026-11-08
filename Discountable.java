// CONCEPT: Interface - discount contract used by percentage and flat discounts
public interface Discountable {
    // Returns how much money to subtract from the given amount.
    double getDiscount(double amount);
}
