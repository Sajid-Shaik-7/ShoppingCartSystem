// CONCEPT: Exception Handling - custom exception when stock is not enough
public class OutOfStockException extends Exception {
    // Stores a simple error message for the dialog box.
    public OutOfStockException(String message) {
        super(message);
    }
}
