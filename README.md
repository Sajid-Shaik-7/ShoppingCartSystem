# Online Shopping Cart System (Java + Swing)

College beginner project. **JDK only** — no Maven, no database, no extra libraries.
All data stays in memory and resets when the app closes.

## Compile and run

Open a terminal in this folder (the folder that contains `ShoppingApp.java`):

```
javac *.java
java ShoppingApp
```

You need JDK 8 or later. Check with `javac -version`.

## Demo logins

| Username | Password | Role  |
|----------|----------|-------|
| user1    | user123  | User  |
| user2    | user456  | User  |
| admin    | admin123 | Admin |

Pick the matching role in the dropdown. Wrong password **or** wrong role shows an error.

## How to demo (viva)

1. Wrong password for `user1`. Wrong role: `user1` / `user123` with role **Admin**.
2. Login as `user1`. Search `lap`. Add Laptop twice (quantity increases). Checkout.
3. Add Phone, type coupon `SAVE200`, checkout (second order).
4. **My Orders** shows only `user1` orders, both **Ongoing**.
5. Logout. Login as `admin`. Dashboard shows 2 Ongoing orders.
6. Select one row → **Mark as Completed**. Ongoing, Completed and Revenue update.
7. Try checkout with empty cart, quantity `0`, quantity bigger than stock, and letters in Qty.

Coupon: `SAVE200` = Rs. 200 off. Subtotal above Rs. 5000 = 10% off, above Rs. 2000 = 5% off. GST is 18%.

## Syllabus concept map

| Concept | Where it is used |
|---------|------------------|
| Classes & Objects | `Product`, `CartItem`, `User`, `Order`, `ShoppingCart`, `OrderManager` |
| Arrays (no ArrayList) | `Product[]` in `ShoppingApp`, `CartItem[]` in `ShoppingCart`, `Order[]` in `OrderManager`, `User[]` in `ShoppingApp` |
| Array shift on remove | `ShoppingCart.removeItem()` moves later items left |
| Strings: search | `ShoppingWindow.fillProductTable()` uses `toLowerCase()` and `contains()` |
| Strings: login | `LoginWindow.login()` uses `equals()` (password) and `equalsIgnoreCase()` (username/role) |
| Strings: bill | `BillGenerator` uses `StringBuilder` and `String.format()` |
| Overloaded methods | `ShoppingCart.addToCart(Product p)` and `addToCart(Product p, int qty)` |
| Interface | `Discountable` implemented by `PercentageDiscount` and `FlatDiscount` |
| Interface (Swing) | `LoginWindow`, `ShoppingWindow`, `AdminWindow` implement `ActionListener` |
| Exception handling | `OutOfStockException`, `InvalidQuantityException`, `EmptyCartException`, `InvalidLoginException` |
| try / catch / finally | Checkout in `ShoppingWindow.doCheckout()`; login `finally` clears password |
| NumberFormatException | Quantity box in `ShoppingWindow.readQty()` |
| Shared object | One `OrderManager` created in `ShoppingApp` and passed to User and Admin windows |

## 5 viva questions (simple answers)

**1. Why is there no database or file?**  
This project stores products, users, cart and orders in arrays in RAM. When the program closes, the arrays are gone. That keeps the code short for a first Java course.

**2. What is an overloaded method here?**  
`addToCart(Product p)` and `addToCart(Product p, int qty)` have the same name but different parameters. The first one just calls the second with quantity 1.

**3. What does the `Discountable` interface do?**  
It is a contract: any discount class must have `getDiscount(double amount)`. Percentage and flat coupon classes both implement it, so `BillGenerator` can call the same method on both.

**4. What happens if checkout is clicked with an empty cart?**  
`EmptyCartException` is thrown, caught, and shown in `JOptionPane`. The app does not crash.

**5. How does Admin see a User’s order?**  
When the user checks out, `ShoppingWindow` calls `orderManager.addOrder(...)`. Admin uses the **same** `OrderManager` object, so its table reads the same `Order[]` array.
