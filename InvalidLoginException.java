// CONCEPT: Exception Handling - custom exception for wrong login details
public class InvalidLoginException extends Exception {
    // Stores a simple error message for the dialog box.
    public InvalidLoginException(String message) {
        super(message);
    }
}
