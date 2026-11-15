# 🛒 ShopEase – Online Shopping Cart System

**A B.Tech Information Technology Mini Project**

ShopEase is a shopping cart application developed to demonstrate online shopping operations through two separate interfaces:

- **Java Swing Desktop Application** – Developed using Java, Swing and AWT.
- **Browser-Based Web UI** – Developed using HTML5, CSS3 and JavaScript.

Both interfaces demonstrate related shopping workflows but do not share a backend database. The desktop application stores working data in memory, while the Web UI uses browser state and `localStorage` for selected features.

---

## 📑 Table of Contents

1. [Features](#-features)
2. [Technology Stack](#-technology-stack)
3. [Project Structure](#-project-structure)
4. [Requirements](#-requirements)
5. [Run the Java Swing Application](#-run-the-java-swing-application)
6. [Run the Web UI](#-run-the-web-ui)
7. [Demo Login Credentials](#-demo-login-credentials)
8. [Billing Rules](#-billing-rules)
9. [Book Rental Configuration](#-book-rental-configuration)
10. [Delivery Locations](#-delivery-locations)
11. [Data Storage and Limitations](#-data-storage-and-limitations)
12. [Testing Suggestions](#-testing-suggestions)
13. [Deployment](#-deployment)

---

## ✨ Features

### 🛍️ Shopping Features

- User and Admin login for the desktop application.
- Product catalogue with categories, prices and stock information.
- Product search and category filtering in the Web UI.
- Add products to the shopping cart.
- Update product quantities and remove cart items.
- Quantity and stock validation.
- Automatic discount and coupon calculations.
- GST calculation.
- Checkout and itemised invoice generation.
- My Orders / Order History.
- Admin order dashboard in the desktop application.

### ❤️ Wishlist Management

- Add products to the wishlist using the heart icon.
- Remove products from the wishlist.
- View all saved products in the Wishlist section.
- Add wishlist products directly to the shopping cart.
- Store wishlist information separately for each username using browser `localStorage`.

### 🎯 Smart Budget Tracker

- Enter a personal shopping budget.
- Calculate the remaining budget based on the cart total.
- Display a warning when shopping expenses exceed the budget.
- Visual progress indicator for budget usage.

### 📚 Book Rental System

- Select books for rental.
- Choose rental quantity and duration.
- Check rental stock availability.
- Validate maximum rental duration.
- Calculate rental charges and refundable security deposits.

### 📍 Delivery Location Selection

- Select a delivery location during shopping.
- Display the configured delivery type and estimated delivery period.
- Store the selected delivery location in browser `localStorage`.
- Include delivery information in the checkout and invoice flow.

### 🧾 Detailed Tax Invoice

The Web UI invoice displays relevant order information, including:

- Order ID and date.
- Customer information.
- Selected delivery location.
- Estimated delivery time.
- Product details and quantities.
- Discounts and GST.
- Final payable amount.

---

## 💻 Technology Stack

| Area | Technology |
|---|---|
| Desktop Application | Java |
| Desktop GUI | Java Swing, AWT |
| Web Interface | HTML5, CSS3, JavaScript |
| Desktop Data Structures | Java Arrays and Model Objects |
| Browser Persistence | `localStorage` |
| Static Web Deployment | Vercel |
| Database | No database configured |

---

## 📂 Project Structure

```text
ShoppingCartSystem/
│
├── src/
│   ├── ShoppingApp.java
│   │
│   ├── discount/
│   │   ├── Discountable.java
│   │   ├── FlatDiscount.java
│   │   └── PercentageDiscount.java
│   │
│   ├── exception/
│   │   ├── EmptyCartException.java
│   │   ├── InvalidLoginException.java
│   │   ├── InvalidQuantityException.java
│   │   └── OutOfStockException.java
│   │
│   ├── gui/
│   │   ├── AdminWindow.java
│   │   ├── LoginWindow.java
│   │   └── ShoppingWindow.java
│   │
│   ├── model/
│   │   ├── BookRental.java
│   │   ├── CartItem.java
│   │   ├── Order.java
│   │   ├── Product.java
│   │   ├── ShoppingCart.java
│   │   └── User.java
│   │
│   ├── service/
│   │   ├── BillGenerator.java
│   │   ├── OrderManager.java
│   │   └── RentalManager.java
│   │
│   └── utils/
│       ├── UITheme.java
│       └── WrapLayout.java
│
├── web/
│   └── index.html
│
├── RunShopEase.bat
├── vercel.json
└── README.md
```

---

## ⚙️ Requirements

### Java Desktop Application

- JDK 8 or later.
- Windows, Linux or macOS.
- Terminal or Java IDE.

Check whether Java is installed:

```bash
java -version
javac -version
```

### Web UI

- Modern browser (Chrome, Edge, Firefox or Safari).
- JavaScript enabled.
- Internet connection only if required by externally hosted resources.

---

## ▶️ Run the Java Swing Application

**Step 1: Open the project folder**

Open a terminal in the project root directory, where the `src` folder is located.

**Step 2: Compile the Java source files**

```bash
javac -d out src/ShoppingApp.java src/discount/*.java src/exception/*.java src/gui/*.java src/model/*.java src/service/*.java src/utils/*.java
```

**Step 3: Run the application**

```bash
java -cp out ShoppingApp
```

**Windows users:** You can also run the application using the included `RunShopEase.bat` file, provided the JDK is installed and available in the system PATH.

> **Note:** If compilation fails, verify that `javac` is installed and that the terminal is opened in the project root directory.

---

## 🌐 Run the Web UI

The Web UI is a static browser-based application.

**Option 1: Open directly**

Open the following file in a modern browser:

```text
web/index.html
```

**Option 2: Run using Python's local server**

Open a terminal and execute:

```bash
cd web
python -m http.server 8000
```

Open this address in your browser:

```text
http://localhost:8000
```

No Java server or database is required for the supplied Web UI demonstration.

---

## 🔐 Demo Login Credentials

The following demonstration accounts are defined in `src/ShoppingApp.java` for the Java Swing application.

| Username | Password | Role |
|---|---|---|
| `user1` | `user123` | User |
| `user2` | `user456` | User |
| `admin` | `admin123` | Admin |

Select the matching role on the login screen.

> These are sample demonstration credentials and must not be used in a production application.

---

## 💰 Billing Rules

ShopEase supports the following billing calculations:

| Billing Rule | Description |
|---|---|
| Automatic Discount | 5% discount for eligible subtotals above ₹2,000 |
| Higher Discount | 10% discount for eligible subtotals above ₹5,000 |
| `SAVE200` / `FLAT200` | Flat ₹200 coupon discount |
| `SAVE10` | Additional 10% coupon supported by the Java `BillGenerator` |
| GST | 18% on the taxable amount after applicable discounts |

The Java billing implementation also accounts for product-level savings where configured.

The final amount depends on cart contents and the applicable discount and coupon combination.

---

## 📚 Book Rental Configuration

The Web UI includes the following demonstration rental catalogue:

| Book | Rent per Day | Stock | Security Deposit | Maximum Days |
|---|---:|---:|---:|---:|
| Clean Code | ₹10 | 3 | ₹200 | 60 |
| Java Programming | ₹8 | 5 | ₹150 | 60 |
| Design Patterns | ₹12 | 2 | ₹250 | 45 |

**Rental Calculation:**

```text
Rental Charge = Daily Rent × Quantity × Rental Days

Total Payable = Rental Charge + Security Deposit
```

The security deposit is refundable according to the rental demonstration's stated conditions.

> Rental information in the current Web UI demonstration is temporary. It is not a complete persistent rental-return or refund service.

---

## 📍 Delivery Locations

The Web UI provides the following predefined delivery estimates:

| Location | Delivery Type | Estimated Delivery |
|---|---|---|
| Odalarevu | Fast | Tomorrow |
| Amalapuram | Fast | Tomorrow |
| Ravulapalem | Standard | 2–3 days |
| Rajahmundry | Standard | 2–3 days |
| Bhimavaram | Standard | 3–5 days |
| Hyderabad | Standard | 3–5 days |

> These are predefined demonstration estimates. They are not live courier tracking results or guaranteed delivery commitments.

---

## 💾 Data Storage and Limitations

| Component | Data Handling |
|---|---|
| Java Swing | Products, users, cart and orders are maintained in application memory using arrays and objects. |
| Web UI Wishlist | Stored in browser `localStorage`, separately for each username. |
| Delivery Selection | Stored in browser `localStorage`. |
| Book Rental | Temporary information in the current Web UI demonstration. |
| Backend / Database | No backend API or database configuration is included. |
| Payments | Checkout and invoice generation are demonstrations; no payment gateway is integrated. |
| Delivery | Uses predefined estimates; no live courier API or real-time tracking is integrated. |

**Important:** Desktop application data resets when the application closes. Browser storage is local to the browser profile and device; it is not a central account database.

---

## 🧪 Testing Suggestions

Try the following test cases when demonstrating the project:

1. Log in using valid credentials.
2. Verify that an incorrect password or role is rejected.
3. Search for products and filter them by category.
4. Add a product to the cart.
5. Update product quantities and remove cart items.
6. Test invalid quantities such as `0`, negative values and quantities greater than available stock.
7. Verify the automatic discount thresholds.
8. Apply the `SAVE200` coupon.
9. Check whether GST is calculated after discounts.
10. Add and remove wishlist products.
11. Move a wishlist product into the cart.
12. Set a budget below and above the cart total.
13. Test rental quantities and maximum rental durations.
14. Select different delivery locations and verify their displayed estimates.
15. Generate an invoice and verify the displayed order and delivery details.

Record actual pass/fail results during testing. This README does not claim that every test has been executed.

---

## 🚀 Deployment

### Deploy the Web UI using Vercel

The repository includes a `vercel.json` configuration with the `web` directory as the static output directory.

Deployment steps:

1. Push the project repository to GitHub.
2. Sign in to [Vercel](https://vercel.com/).
3. Import the ShopEase GitHub repository.
4. Review the project configuration.
5. Deploy the project.

The Java Swing application is a separate desktop application and is not deployed as part of the static Web UI.

---

## 👨‍🎓 Project Information

| Field | Details |
|---|---|
| Project Name | ShopEase – Online Shopping Cart System |
| Project Type | B.Tech Information Technology Mini Project |
| Desktop Technology | Java Swing and AWT |
| Web Technology | HTML, CSS and JavaScript |
| Academic Year | 2026–2027 |

---

**ShopEase – Making Shopping Cart Operations Simple and Interactive!** 🛒