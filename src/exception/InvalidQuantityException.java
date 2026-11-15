
package exception;

/**
 * Custom exception thrown when a customer enters an invalid quantity.
 *
 * Concepts:
 * 1. Exception Handling
 * 2. Inheritance
 * 3. Custom Exceptions
 */
public class InvalidQuantityException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a custom error message.
     */
    public InvalidQuantityException(String message) {
        super(message);
    }
}
