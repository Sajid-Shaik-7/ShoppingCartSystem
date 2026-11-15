
package exception;

/**
 * Custom exception thrown when a customer tries to
 * checkout without adding products to the cart.
 *
 * Concepts:
 * 1. Exception Handling
 * 2. Inheritance
 * 3. Custom Exceptions
 */
public class EmptyCartException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a custom error message.
     */
    public EmptyCartException(String message) {
        super(message);
    }
}
