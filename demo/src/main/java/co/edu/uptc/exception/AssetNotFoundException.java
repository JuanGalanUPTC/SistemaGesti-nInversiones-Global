package co.edu.uptc.exception;

/**
 * Indica que no existe un activo con el identificador consultado.
 */
public class AssetNotFoundException extends RuntimeException {

    public AssetNotFoundException(String message) {
        super(message);
    }
}
