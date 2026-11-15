
package exception;

/**
 * Custom exception thrown when the requested product
 * quantity is greater than the available stock.
 *
 * Concepts:
 * 1. Exception Handling
 * 2. Inheritance
 * 3. Custom Exceptions
 */
public class OutOfStockException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a custom error message.
     */
    public OutOfStockException(String message) {
        super(message);
    }
}
