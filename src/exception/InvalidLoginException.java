
package exception;

/**
 * Custom exception thrown when login credentials are invalid.
 *
 * Concepts:
 * 1. Exception Handling
 * 2. Inheritance
 * 3. Custom Exceptions
 */
public class InvalidLoginException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a custom error message.
     */
    public InvalidLoginException(String message) {
        super(message);
    }
}
