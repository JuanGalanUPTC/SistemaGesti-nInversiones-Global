package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import co.edu.uptc.exception.AssetNotFoundException;
import co.edu.uptc.model.Asset;
import co.edu.uptc.model.enums.AssetType;
import co.edu.uptc.repository.JsonRepository;

/**
 * Servicio de gestión de activos financieros: registro, listado y consulta del precio
 * vigente por identificador, alineado con los requisitos de administración de activos.
 */
public class AssetService {
    private final JsonRepository<Asset> repo;

    public AssetService() {
        Type type = new TypeToken<List<Asset>>() {}.getType();
        this.repo = new JsonRepository<>("assets.json", type);
    }

    public AssetService(JsonRepository<Asset> repo) {
        this.repo = repo;
    }

    /**
     * Registra un activo con código (identificador), nombre, tipo, precio actual y volatilidad.
     *
     * @param id código o identificador único del activo
     * @param name nombre descriptivo del activo
     * @param assetType tipo de activo (por ejemplo acción, bono o ETF)
     * @param actualPrice precio de mercado actual
     * @param volatility volatilidad expresada en porcentaje
     */
    public void createAsset(String id, String name, AssetType assetType, double actualPrice, double volatility) {
        try {
            repo.save(new Asset(id, name, assetType, actualPrice, volatility));
        } catch (RuntimeException e) {
            throw new RuntimeException("Error trying to save the asset.", e);
        }
    }

    /**
     * Devuelve todos los activos almacenados (consulta general).
     *
     * @return lista de {@link Asset}; puede estar vacía
     */
    public List<Asset> listAssets() {
        try {
            return repo.findAll();
        } catch (RuntimeException e) {
            throw new RuntimeException("Error trying to list the assets.", e);
        }
    }

    //Consultar activos filtrando por tipo o rango de precio (Falta)

    //Actualizar precio del activo y recalcular rendimientos automáticamente (Falta)

    /**
     * Obtiene el precio actual de un activo por su identificador, necesario para calcular
     * el valor corriente de las posiciones y los rendimientos.
     *
     * @param assetId identificador del activo
     * @return precio actual del activo
     * @throws AssetNotFoundException si no existe un activo con el identificador indicado
     */
    public double getPrice(String assetId) {
        try {
            List<Asset> assets = repo.findAll();

            for (Asset asset : assets) {
                if (asset.getId().equals(assetId)) {
                    return asset.getActualPrice();
                }
            }

            throw new AssetNotFoundException("Asset not found with that id.");
        } catch (AssetNotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error getting the price of the asset.", e);
        }
    }
}
