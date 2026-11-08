// CONCEPT: Exception Handling - custom exception when checkout cart is empty
public class EmptyCartException extends Exception {
    // Stores a simple error message for the dialog box.
    public EmptyCartException(String message) {
        super(message);
    }
}
