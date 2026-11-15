
package service;

import java.util.Locale;

import model.CartItem;
import model.ShoppingCart;
import discount.Discountable;
import discount.PercentageDiscount;
import discount.FlatDiscount;

/**
 * Generates customer bills for ShopEase.
 *
 * Responsibilities:
 * 1. Calculate automatic discounts.
 * 2. Apply coupon discounts.
 * 3. Calculate product-level savings.
 * 4. Calculate GST.
 * 5. Generate formatted bill text.
 *
 * Concepts:
 * 1. StringBuilder
 * 2. String.format
 * 3. Interfaces
 * 4. Method Reusability
 */
public class BillGenerator {

    private static final double GST_RATE = 18.0;

    /**
     * Calculates additional order-level discounts.
     *
     * Automatic discounts:
     * Above ₹5000 = 10%
     * Above ₹2000 = 5%
     *
     * Supported coupons:
     * SAVE10 = Additional 10%
     * FLAT200 = Additional ₹200
     * SAVE200 = Legacy alias for FLAT200
     *
     * The input subtotal already includes product-level discounts
     * calculated by ShoppingCart.
     *
     * @param subtotal cart subtotal after product discounts
     * @param coupon coupon code
     * @return total additional discount
     */
    public static double getDiscountAmount(
            double subtotal, String coupon) {

        if (!Double.isFinite(subtotal) || subtotal < 0) {
            throw new IllegalArgumentException(
                "Subtotal must be a valid non-negative amount."
            );
        }

        double discount = 0;

        // Automatic discount based on purchase amount.
        if (subtotal > 5000) {

            Discountable percent = new PercentageDiscount(10);
            discount += percent.getDiscount(subtotal);

        } else if (subtotal > 2000) {

            Discountable percent = new PercentageDiscount(5);
            discount += percent.getDiscount(subtotal);
        }

        // Apply coupon discount.
        if (coupon != null) {

            String couponCode = coupon.trim().toUpperCase(Locale.ROOT);

            switch (couponCode) {

                case "SAVE10":
                    Discountable percentage = new PercentageDiscount(10);
                    discount += percentage.getDiscount(subtotal);
                    break;

                case "FLAT200":
                case "SAVE200":
                    Discountable flat = new FlatDiscount(200);
                    discount += flat.getDiscount(subtotal);
                    break;

                default:
                    // Unknown coupon: no additional coupon discount.
                    break;
            }
        }

        // Discount must never exceed the subtotal.
        return Math.min(discount, subtotal);
    }

    /**
     * Calculates product-level savings already applied in the cart.
     */
    public static double getProductSavings(ShoppingCart cart) {

        if (cart == null) {
            throw new IllegalArgumentException("Shopping cart cannot be null.");
        }

        return cart.getOriginalSubtotal() - cart.getSubtotal();
    }

    /**
     * Calculates GST after all discounts.
     */
    public static double getGSTAmount(
            ShoppingCart cart, String coupon) {

        if (cart == null) {
            throw new IllegalArgumentException("Shopping cart cannot be null.");
        }

        double subtotal = cart.getSubtotal();
        double discount = getDiscountAmount(subtotal, coupon);
        double taxableAmount = subtotal - discount;

        return taxableAmount * GST_RATE / 100.0;
    }

    /**
     * Calculates the final amount payable.
     */
    public static double getGrandTotal(
            ShoppingCart cart, String coupon) {

        if (cart == null) {
            throw new IllegalArgumentException("Shopping cart cannot be null.");
        }

        double subtotal = cart.getSubtotal();
        double discount = getDiscountAmount(subtotal, coupon);
        double afterDiscount = subtotal - discount;
        double gst = afterDiscount * GST_RATE / 100.0;

        return afterDiscount + gst;
    }

    /**
     * Builds the complete customer bill.
     *
     * @param cart customer's shopping cart
     * @param coupon applied coupon code
     * @param customerName customer's name
     * @return formatted bill
     */
    public static String buildBill(
            ShoppingCart cart,
            String coupon,
            String customerName) {

        if (cart == null) {
            throw new IllegalArgumentException("Shopping cart cannot be null.");
        }

        if (cart.isEmpty()) {
            throw new IllegalArgumentException(
                "Cannot generate a bill for an empty cart."
            );
        }

        double originalSubtotal = cart.getOriginalSubtotal();
        double productSavings = getProductSavings(cart);
        double subtotal = cart.getSubtotal();

        double orderDiscount = getDiscountAmount(subtotal, coupon);
        double afterDiscount = subtotal - orderDiscount;

        double gst = afterDiscount * GST_RATE / 100.0;
        double grandTotal = afterDiscount + gst;

        StringBuilder sb = new StringBuilder();

        sb.append("================================\n");
        sb.append("          SHOP EASE BILL        \n");
        sb.append("================================\n");

        sb.append("Customer : ")
          .append(customerName == null ? "Customer" : customerName)
          .append("\n");

        sb.append("--------------------------------\n");

        appendItemLines(sb, cart);

        sb.append("--------------------------------\n");

        sb.append(String.format(
            Locale.US, "Original Subtotal     : Rs. %.2f\n",
            originalSubtotal
        ));

        sb.append(String.format(
            Locale.US, "Product Savings       : Rs. %.2f\n",
            productSavings
        ));

        sb.append(String.format(
            Locale.US, "Subtotal After Savings: Rs. %.2f\n",
            subtotal
        ));

        sb.append(String.format(
            Locale.US, "Additional Discount   : Rs. %.2f\n",
            orderDiscount
        ));

        if (coupon != null && !coupon.trim().isEmpty()) {
            sb.append("Coupon Code           : ")
              .append(coupon.trim().toUpperCase(Locale.ROOT))
              .append("\n");
        }

        sb.append(String.format(
            Locale.US, "GST (18%%)             : Rs. %.2f\n",
            gst
        ));

        sb.append("--------------------------------\n");

        sb.append(String.format(
            Locale.US, "GRAND TOTAL           : Rs. %.2f\n",
            grandTotal
        ));

        sb.append("================================\n");
        sb.append("       THANK YOU FOR SHOPPING!  \n");
        sb.append("================================");

        return sb.toString();
    }

    /**
     * Adds individual product details to the bill.
     */
    public static void appendItemLines(
            StringBuilder sb, ShoppingCart cart) {

        if (sb == null || cart == null) {
            throw new IllegalArgumentException(
                "Bill builder and shopping cart cannot be null."
            );
        }

        CartItem[] items = cart.getItems();

        for (CartItem item : items) {

            sb.append(String.format(
                Locale.US,
                "%s x%d = Rs. %.2f\n",
                item.getProduct().getName(),
                item.getQuantity(),
                item.getLineTotal()
            ));

            if (item.hasDiscount()) {

                sb.append(String.format(
                    Locale.US,
                    "   Product savings: Rs. %.2f\n",
                    item.getDiscountAmount()
                ));
            }
        }
    }
}
