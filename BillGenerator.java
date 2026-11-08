// CONCEPT: Strings - bill text built with StringBuilder and String.format()
public class BillGenerator {

    // Picks 10% above Rs.5000, 5% above Rs.2000, plus SAVE200 flat coupon.
    public static double getDiscountAmount(double subtotal, String coupon) {
        double discount = 0;
        if (subtotal > 5000) {
            Discountable percent = new PercentageDiscount(10);
            discount = discount + percent.getDiscount(subtotal);
        } else if (subtotal > 2000) {
            Discountable percent = new PercentageDiscount(5);
            discount = discount + percent.getDiscount(subtotal);
        }
        if (coupon != null && coupon.trim().equalsIgnoreCase("SAVE200")) {
            Discountable flat = new FlatDiscount(200);
            discount = discount + flat.getDiscount(subtotal);
        }
        if (discount > subtotal) {
            discount = subtotal;
        }
        return discount;
    }

    // Builds the full bill text: items, subtotal, discount, 18% GST, grand total.
    public static String buildBill(ShoppingCart cart, String coupon, String customerName) {
        double subtotal = cart.getSubtotal();
        double discount = getDiscountAmount(subtotal, coupon);
        double afterDiscount = subtotal - discount;
        double gst = afterDiscount * 0.18;
        double grand = afterDiscount + gst;
        StringBuilder sb = new StringBuilder();
        sb.append("========== BILL ==========\n");
        sb.append("Customer : ").append(customerName).append("\n");
        appendItemLines(sb, cart);
        sb.append("--------------------------\n");
        sb.append(String.format("Subtotal : Rs. %.2f\n", subtotal));
        sb.append(String.format("Discount : Rs. %.2f\n", discount));
        sb.append(String.format("GST 18%%  : Rs. %.2f\n", gst));
        sb.append(String.format("GRAND    : Rs. %.2f\n", grand));
        sb.append("==========================");
        return sb.toString();
    }

    // Adds each cart line to the bill using String.format.
    public static void appendItemLines(StringBuilder sb, ShoppingCart cart) {
        CartItem[] items = cart.getItems();
        int i = 0;
        while (i < cart.getItemCount()) {
            CartItem line = items[i];
            sb.append(String.format("%s x%d = Rs. %.2f\n",
                    line.getProduct().getName(),
                    line.getQuantity(),
                    line.getLineTotal()));
            i = i + 1;
        }
    }

    // Returns the grand total used when saving the Order.
    public static double getGrandTotal(ShoppingCart cart, String coupon) {
        double subtotal = cart.getSubtotal();
        double discount = getDiscountAmount(subtotal, coupon);
        double afterDiscount = subtotal - discount;
        double gst = afterDiscount * 0.18;
        return afterDiscount + gst;
    }
}
