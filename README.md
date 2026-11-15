ShopEase – Online Shopping Cart System
A B.Tech mini-project that demonstrates shopping-cart operations through two separate interfaces:
Java Swing desktop application – built with standard Java and Swing/AWT.
Browser-based Web UI – built with HTML, CSS and JavaScript.
The two interfaces demonstrate related shopping workflows, but they do not share a backend database. The desktop application stores its working data in memory. The Web UI uses browser state and `localStorage` for selected features.
---
Table of Contents
Features
Technology Stack
Project Structure
Requirements
Run the Java Swing Application
Run the Web UI
Demo Login Credentials
Billing Rules
Book Rental Configuration
Delivery Locations
Data Storage and Limitations
Testing Suggestions
Deployment
Features
Shopping features
User and Admin login for the desktop demonstration.
Product catalogue with categories, prices and stock information.
Product search and category filtering in the Web UI.
Add products to the cart, update quantities and remove items.
Quantity and stock validation.
Automatic discounts, coupon handling and GST calculation.
Checkout and itemised invoice generation.
My Orders / order history and an Admin order dashboard in the desktop application.
Additional Web UI features
Wishlist: add or remove products using the heart control, view saved products and add wishlist items to the cart. Wishlist data is stored per username in browser `localStorage`.
Smart Budget Tracker: enter a shopping budget and view the remaining amount, exceeded-budget status and progress indicator based on the cart total.
Book Rental: choose a rentable book, quantity and rental duration. The interface checks rental stock and maximum rental days, then calculates rental charges and the security deposit.
Delivery Location: select a delivery area and view its configured delivery type and estimated delivery period. The selection is stored in browser `localStorage` and included in the checkout/invoice flow.
Detailed invoice: displays order and customer details, selected delivery information and itemised billing details in the Web UI.
Technology Stack
Area	Technology
Desktop application	Java, Java Swing, AWT
Web interface	HTML5, CSS3, JavaScript
Desktop data structures	Java arrays and model objects
Browser persistence	`localStorage` for wishlist and delivery selections
Static web deployment configuration	Vercel (`vercel.json`)
Database	No database is configured in this project
Project Structure
```text
ShoppingCartSystem/
├── src/
│   ├── ShoppingApp.java
│   ├── discount/
│   │   ├── Discountable.java
│   │   ├── FlatDiscount.java
│   │   └── PercentageDiscount.java
│   ├── exception/
│   │   ├── EmptyCartException.java
│   │   ├── InvalidLoginException.java
│   │   ├── InvalidQuantityException.java
│   │   └── OutOfStockException.java
│   ├── gui/
│   │   ├── AdminWindow.java
│   │   ├── LoginWindow.java
│   │   └── ShoppingWindow.java
│   ├── model/
│   │   ├── BookRental.java
│   │   ├── CartItem.java
│   │   ├── Order.java
│   │   ├── Product.java
│   │   ├── ShoppingCart.java
│   │   └── User.java
│   ├── service/
│   │   ├── BillGenerator.java
│   │   ├── OrderManager.java
│   │   └── RentalManager.java
│   └── utils/
│       ├── UITheme.java
│       └── WrapLayout.java
├── web/
│   └── index.html
├── RunShopEase.bat
├── vercel.json
└── README.md
```
Requirements
Java desktop application
JDK 8 or later.
Windows, Linux or macOS.
A terminal or Java IDE.
Check the installed Java compiler:
```bash
javac -version
```
Web UI
A modern browser such as Chrome, Edge, Firefox or Safari.
JavaScript enabled.
Run the Java Swing Application
Open a terminal in the project root (the folder containing `src/`) and compile all Java source files:
```bash
javac -d out src/ShoppingApp.java src/discount/*.java src/exception/*.java src/gui/*.java src/model/*.java src/service/*.java src/utils/*.java
```
Run the application:
```bash
java -cp out ShoppingApp
```
On Windows, the repository also includes `RunShopEase.bat`. Run it from the project root if the JDK is installed and available on the system PATH.
> If compilation fails, confirm that `javac` is available and that the terminal's current directory is the project root.
Run the Web UI
Open `web/index.html` in a modern browser. The Web UI is a static client-side application and does not require a Java server or database for the supplied demonstration features.
For local development, a simple static file server may be used. For example, if Python is installed:
```bash
cd web
python -m http.server 8000
```
Then open `http://localhost:8000` in the browser.
Demo Login Credentials
The following demonstration accounts are defined in `src/ShoppingApp.java` for the Java Swing application:
Username	Password	Role
`user1`	`user123`	User
`user2`	`user456`	User
`admin`	`admin123`	Admin
Select the matching role in the login screen. These are sample credentials only; do not use them in a production application.
Billing Rules
The project billing logic includes:
Automatic 5% discount when the eligible subtotal is above ₹2,000.
Automatic 10% discount when the eligible subtotal is above ₹5,000.
`SAVE200` / `FLAT200`: flat ₹200 coupon discount.
`SAVE10`: additional 10% coupon supported by the Java `BillGenerator` source.
GST: 18%, calculated on the taxable amount after applicable discounts.
The Java billing implementation also accounts for product-level savings where configured. The final amount depends on the cart contents and the discount/coupon combination.
Book Rental Configuration
The Web UI demonstration uses the following rental catalogue:
Book	Rent per day	Rental stock	Security deposit	Maximum rental period
Clean Code	₹10	3	₹200	60 days
Java Programming	₹8	5	₹150	60 days
Design Patterns	₹12	2	₹250	45 days
The rental charge is calculated from the daily rate, quantity and selected number of days. The security deposit is added to the amount payable. Rental information in the current Web UI demonstration is temporary and is not a complete persistent rental-return or refund service.
Delivery Locations
The Web UI provides these configured demonstration delivery estimates:

Location	Delivery type	Estimated delivery
Odalarevu	Fast	Tomorrow
Amalapuram	Fast	Tomorrow
Ravulapalem	Standard	2–3 days
Rajahmundry	Standard	2–3 days
Bhimavaram	Standard	3–5 days
Hyderabad	Standard	3–5 days
These are predefined demonstration estimates. They are not live courier tracking results or guaranteed delivery commitments.
Data Storage and Limitations
Java Swing: products, users, cart and orders are maintained in application memory using arrays and objects. The data resets when the desktop application closes.
Web UI: wishlist and delivery selections are stored in browser `localStorage`. This storage is local to the browser profile/device and is not a central account database.
Book rental: rental information is temporary in the current Web UI demonstration and may be cleared when the page is refreshed.
Backend/database: no backend API or database configuration is included in the supplied project.
Payments: checkout and invoice generation are demonstrations; no payment gateway is integrated.
Delivery: delivery estimates are static demonstration values; no live courier API or real-time tracking is integrated.
Testing Suggestions
Try the following cases when demonstrating the project:
Log in with valid credentials and verify that an incorrect password or role is rejected.
Search for a product and filter by category in the Web UI.
Add an item to the cart, update its quantity and remove it.
Try quantity values such as `0`, a negative number, a non-numeric value and a quantity greater than stock.
Check the automatic discount thresholds and apply `SAVE200`.
Confirm that GST is calculated after discounts.
Add and remove a wishlist item, then add it to the cart.
Set a budget below and above the cart total.
Test rental quantities and maximum rental durations.
Select each delivery area and verify its displayed estimate and invoice details.
Record actual pass/fail results during testing; this README does not claim that every test has been executed.
Deployment
The repository includes `vercel.json` with `web` as the static output directory. To deploy the Web UI, import the repository into Vercel and use the included project configuration. The Java Swing application is a separate desktop program and is not deployed as part of the static Web UI.
---
Project: ShopEase – Online Shopping Cart System using Java Swing and Web UI  
Project type: B.Tech Information Technology Mini Project