// CONCEPT: Exception Handling - custom exception for bad quantity values
public class InvalidQuantityException extends Exception {
    // Stores a simple error message for the dialog box.
    public InvalidQuantityException(String message) {
        super(message);
    }
}
