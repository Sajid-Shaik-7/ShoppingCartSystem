import javax.swing.SwingUtilities;

// CONCEPT: Classes & Objects - ShoppingApp creates shared Product[], User[], OrderManager
public class ShoppingApp {

    // Entry point: build shared data then open the login window on the EDT.
    public static void main(String[] args) {
        Product[]    catalogue    = createCatalogue();
        User[]       users        = createUsers();
        OrderManager orderManager = new OrderManager();

        // CONCEPT: Strings - SwingUtilities.invokeLater for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginWindow login = new LoginWindow(catalogue, users, orderManager);
                login.setVisible(true);
            }
        });
    }

    // Hard-codes 20 products across 5 categories with emoji icons.
    public static Product[] createCatalogue() {
        Product[] list = new Product[20];

        // Electronics
        list[0]  = new Product(1,  "Laptop Pro",        65999, 5,  "Electronics", "\uD83D\uDCBB");
        list[1]  = new Product(2,  "Smartphone X",      29999, 8,  "Electronics", "\uD83D\uDCF1");
        list[2]  = new Product(3,  "Wireless Mouse",      799, 20, "Electronics", "\uD83D\uDDB1");
        list[3]  = new Product(4,  "Mechanical Keyboard",2499, 15, "Electronics", "\u2328");
        list[4]  = new Product(5,  "Noise-Cancel Earbuds",3499,10, "Electronics", "\uD83C\uDFA7");

        // Fashion
        list[5]  = new Product(6,  "Running Shoes",      3299, 12, "Fashion",     "\uD83D\uDC5F");
        list[6]  = new Product(7,  "Casual T-Shirt",      699, 30, "Fashion",     "\uD83D\uDC55");
        list[7]  = new Product(8,  "Denim Jeans",        1799, 18, "Fashion",     "\uD83D\uDC56");
        list[8]  = new Product(9,  "Leather Wallet",     1299, 25, "Fashion",     "\uD83D\uDC5B");
        list[9]  = new Product(10, "Sports Backpack",    2199, 10, "Fashion",     "\uD83C\uDF92");

        // Kitchen
        list[10] = new Product(11, "Air Fryer",          5499,  7, "Kitchen",     "\uD83C\uDF73");
        list[11] = new Product(12, "Coffee Maker",       3299,  9, "Kitchen",     "\u2615");
        list[12] = new Product(13, "Non-stick Pan Set",  1899, 14, "Kitchen",     "\uD83E\uDD58");
        list[13] = new Product(14, "Smart Water Bottle",  699, 40, "Kitchen",     "\uD83D\uDEB0");
        list[14] = new Product(15, "Blender Pro",        2799,  6, "Kitchen",     "\uD83E\uDD64");

        // Books
        list[15] = new Product(16, "Clean Code",          699,  5, "Books",       "\uD83D\uDCDA");
        list[16] = new Product(17, "Java Programming",    549, 20, "Books",       "\u2615");
        list[17] = new Product(18, "Design Patterns",     799,  8, "Books",       "\uD83D\uDCDA");

        // Sports
        list[18] = new Product(19, "Yoga Mat",           1199, 15, "Sports",      "\uD83E\uDDD8");
        list[19] = new Product(20, "Dumbbell Set 10kg",  2999,  6, "Sports",      "\uD83C\uDFCB");

        return list;
    }

    // Hard-codes demo accounts: 2 customers and 1 admin.
    public static User[] createUsers() {
        User[] users = new User[3];
        users[0] = new User("user1",  "user123", "User");
        users[1] = new User("user2",  "user456", "User");
        users[2] = new User("admin",  "admin123","Admin");
        return users;
    }
}
