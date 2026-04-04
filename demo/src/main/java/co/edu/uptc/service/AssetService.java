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

    //Consultar activos filtrando por tipo
    public List<Asset> findByType(AssetType t){
        List<Asset> allAssets = repo.findAll();
        
        return allAssets.stream()
                .filter(asset -> asset.getAssetType() == t)
                .toList();
    }
      /**
     * Busca un activo por su identificador.
     *
     * @param id identificador del activo
     * @return el {@link asset} encontrado, o {@code null} si no existe
     */
    public Asset findById(String id) {
        try {
            return repo.findAll()
                    .stream()
                    .filter(i -> i.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error trying to find the asset by id.", e);
        }
    }
     //Consultar activos filtrando por  rango de precio 
    public List<Asset> findByPriceRange(double minPrice,double maxPrice){
        List<Asset> allAssets = repo.findAll();
        
        return allAssets.stream()
                .filter(asset -> asset.getActualPrice() >=minPrice && asset.getActualPrice()<=maxPrice)
                .toList();
    }
    //Actualizar precio del activo y recalcular rendimientos automáticamente
    public void updAssetPrice(String assetId, double newPrice) {
        List<Asset> assets = repo.findAll();
        boolean isUpdated = false;

        for (Asset asset : assets) {
            if (asset.getId().equals(assetId)) {
                asset.setActualPrice(newPrice);
                isUpdated = true;
                break;
            }
        }
        if (isUpdated) {
            repo.replaceAll(assets); // Guarda en assets.json
        } else {
            throw new AssetNotFoundException(assetId);
        }
    }

    /**
     * Obtiene el precio actual de un activo por su identificador, necesario para calcular
     * el valor corriente de las posiciones y los rendimientos.
     *
     * @param assetId identificador del activo
     * @return precio actual del activo
     * @throws AssetNotFoundException si no existe un activo con el identificador indicado
     */
    public double getPrice(String assetId) {
        // Reutilizamos la lógica que ya creaste arriba
        Asset asset = findById(assetId);
        
        if (asset != null) {
            return asset.getActualPrice();
        } else {
            throw new AssetNotFoundException("Asset not found with that id: " + assetId);
        }
    }
}
