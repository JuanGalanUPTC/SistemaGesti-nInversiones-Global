package co.edu.uptc.exception;

public class OperationCancelledException extends RuntimeException {
    public OperationCancelledException(String message) {
        super(message);
    }
}
