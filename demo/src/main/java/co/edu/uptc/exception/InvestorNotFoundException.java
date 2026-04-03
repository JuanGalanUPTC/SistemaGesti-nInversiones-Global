package co.edu.uptc.exception;

/**
 * Indica que no existe un inversionista con el identificador consultado.
 */
public class InvestorNotFoundException extends RuntimeException {

    public InvestorNotFoundException(String message) {
        super(message);
    }
}
