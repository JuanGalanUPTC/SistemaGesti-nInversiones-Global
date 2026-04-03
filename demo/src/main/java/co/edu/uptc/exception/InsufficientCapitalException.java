package co.edu.uptc.exception;

/**
 * Indica que el inversionista no dispone de capital suficiente para la operación solicitada.
 */
public class InsufficientCapitalException extends RuntimeException {

    public InsufficientCapitalException(String message) {
        super(message);
    }
}
