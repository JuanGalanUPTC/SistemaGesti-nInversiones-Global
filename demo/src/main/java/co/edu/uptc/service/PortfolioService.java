package co.edu.uptc.service;

import java.time.LocalDate;
import java.util.List;

import co.edu.uptc.model.Inversion;

/**
 * Servicio de agregación de portafolio: cálculos sobre conjuntos de inversiones,
 * como el total de ganancias o pérdidas en un intervalo de fechas (reporte por periodo).
 */
public class PortfolioService {
    
    private InversionService inversionService;
    private AssetService assetService;

    /**
     * Construye el servicio de portafolio con las dependencias necesarias para valorar
     * cada inversión con el precio actual del activo.
     *
     * @param inversionService servicio que aporta fórmulas de valor, inversión inicial y ganancia
     * @param assetService servicio que resuelve el precio actual por activo
     */
    public PortfolioService(InversionService inversionService, AssetService assetService) {
        this.inversionService = inversionService;
        this.assetService = assetService;
    }

    /**
     * Calcula la suma de las ganancias o pérdidas monetarias de las inversiones cuya fecha
     * cae dentro del periodo indicado (inclusive en los extremos). Para cada operación se usa
     * el precio actual del activo frente al valor de compra, cumpliendo el reporte de
     * ganancias totales en un rango de tiempo.
     *
     * @param inversions lista de inversiones a considerar (habitualmente todas o las filtradas)
     * @param startDate fecha inicial del periodo (inclusive)
     * @param endDate fecha final del periodo (inclusive)
     * @return suma de (valor actual − inversión inicial) de las inversiones en el periodo
     * @throws IllegalArgumentException si la fecha de inicio es posterior a la fecha final
     */
    public double calculateEarningsByPeriod(List<Inversion> inversions, LocalDate startDate, LocalDate endDate) {
        double total = 0;

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }


        for (Inversion inv : inversions) {
            if ((inv.getDate().isEqual(startDate) || inv.getDate().isAfter(startDate))
                    && (inv.getDate().isEqual(endDate) || inv.getDate().isBefore(endDate))) {


                double actualValue = inversionService.calculateActualValue(inv);
                double purchaseValue = inversionService.calculateInitialInvestment(inv.getPurchasePrice(), inv.getAmount());

                total += inversionService.calculateEarnings(actualValue, purchaseValue);
            }
        }

        return total;

    }
}
