package co.edu.uptc.control;

import co.edu.uptc.model.Asset;
import co.edu.uptc.model.enums.AssetType;
import co.edu.uptc.service.AssetService;
import co.edu.uptc.view.ConsoleView;
import co.edu.uptc.exception.AssetNotFoundException;

import java.util.List;

public class AssetController {
    
    private final AssetService assetService;
    private final ConsoleView view;

    public AssetController(AssetService assetService, ConsoleView view) {
        this.assetService = assetService;
        this.view = view;
    }

    /**
     * Maneja la creación de un nuevo activo.
     */
    public void handleCreateAsset() {
        try {
            // Usamos las llaves del archivo properties para los mensajes
            view.showMessageByKey("msg.title.createAsset");
            
            String id = view.readStringInput("msg.input.assetId");
            String name = view.readStringInput("msg.input.assetName");
            String typeStr = view.readStringInput("msg.input.assetType");
            
            // Convertimos el texto ingresado al Enum correspondiente
            AssetType type = AssetType.valueOf(typeStr.toUpperCase());
            
            // Usamos el nuevo método de la vista que valida y lee decimales automáticamente
            double price = view.readDoubleInput("msg.input.assetPrice");
            double volatility = view.readDoubleInput("msg.input.assetVolatility");

            assetService.createAsset(id, name, type, price, volatility);
            view.showMessageByKey("msg.success.assetCreated");

        } catch (IllegalArgumentException e) {
            // Se ejecuta si el usuario escribe algo como "PERRO" en lugar de "ACCION"
            view.showMessageByKey("msg.error.invalidAssetType");
        } catch (RuntimeException e) {
            // Imprimimos el error dinámico usando printText
            view.printText("Error del sistema: " + e.getMessage());
        }
    }

    /**
     * Maneja el listado de todos los activos.
     */
    public void handleListAssets() {
        try {
            view.showMessageByKey("msg.title.listAssets");
            List<Asset> assets = assetService.listAssets();

            if (assets.isEmpty()) {
                view.showMessageByKey("msg.error.noAssets");
            } else {
                for (Asset asset : assets) {
                    // Usamos printText porque estamos combinando datos dinámicos de los objetos
                    view.printText("- [" + asset.getId() + "] " + asset.getName() + 
                            " | Tipo: " + asset.getAssetType() + 
                            " | Precio: $" + asset.getActualPrice());
                }
            }
        } catch (RuntimeException e) {
            view.printText("Error al listar los activos: " + e.getMessage());
        }
    }

    /**
     * Maneja la actualización del precio de un activo.
     */
    public void handleUpdateAssetPrice() {
        try {
            view.showMessageByKey("msg.title.updatePrice");
            
            String id = view.readStringInput("msg.input.assetId");
            double newPrice = view.readDoubleInput("msg.input.newPrice");

            assetService.updAssetPrice(id, newPrice);
            view.showMessageByKey("msg.success.priceUpdated");

        } catch (AssetNotFoundException e) {
            // Como la excepción trae su propio mensaje, usamos printText
            view.printText(e.getMessage());
        } catch (RuntimeException e) {
            view.printText("Error al actualizar el precio: " + e.getMessage());
        }
    }

    /**
     * Maneja la búsqueda por rango de precio.
     */
    public void handleFindByPriceRange() {
        try {
            view.showMessageByKey("msg.title.priceRange");
            
            double minPrice = view.readDoubleInput("msg.input.minPrice");
            double maxPrice = view.readDoubleInput("msg.input.maxPrice");

            List<Asset> assets = assetService.findByPriceRange(minPrice, maxPrice);

            if (assets.isEmpty()) {
                view.showMessageByKey("msg.error.noAssetsInRange");
            } else {
                for (Asset asset : assets) {
                    view.printText("- " + asset.getName() + " ($" + asset.getActualPrice() + ")");
                }
            }
        } catch (RuntimeException e) {
            view.printText("Error al buscar: " + e.getMessage());
        }
    }
}