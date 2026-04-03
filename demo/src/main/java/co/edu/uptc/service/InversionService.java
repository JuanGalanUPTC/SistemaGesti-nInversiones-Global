package co.edu.uptc.service;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import com.google.gson.reflect.TypeToken;

import co.edu.uptc.exception.IncompatibleRiskProfileException;
import co.edu.uptc.exception.InsufficientCapitalException;
import co.edu.uptc.model.Inversion;
import co.edu.uptc.model.enums.AssetType;
import co.edu.uptc.model.enums.RiskProfile;
import co.edu.uptc.repository.JsonRepository;

/**
 * Servicio de inversiones individuales: creación con validaciones de negocio (capital y perfil de
 * riesgo frente al activo) y cálculos de valor actual, rendimiento porcentual y ganancia o pérdida monetaria.
 */
public class InversionService {
    private final JsonRepository<Inversion> repo;

    public InversionService() {
        Type type = new TypeToken<List<Inversion>>() {}.getType();
        this.repo = new JsonRepository<>("inversions.json", type);
    }

    public InversionService(JsonRepository<Inversion> repo) {
        this.repo = repo;
    }

    /**
     * Crea una inversión asociando un inversionista, un activo y la cantidad adquirida (junto al precio
     * de compra y la fecha de la operación). Antes de persistir, comprueba que el capital disponible
     * cubra la inversión inicial y que el perfil de riesgo del inversionista sea compatible con el
     * tipo de activo.
     *
     * @param id identificador único de la operación de inversión
     * @param inversionistId identificador del inversionista
     * @param assetId identificador del activo
     * @param amount cantidad de unidades compradas
     * @param purchasePrice precio unitario al momento de la compra
     * @param date fecha de la operación
     * @param time hora de la operación
     * @param availableCapital capital disponible del inversionista en el momento de validar (debe ser ≥ inversión inicial)
     * @param riskProfile perfil de riesgo del inversionista
     * @param assetType tipo de activo, usado para la validación de riesgo
     * @throws InsufficientCapitalException si el capital es insuficiente
     * @throws IncompatibleRiskProfileException si el perfil de riesgo no permite el activo
     */
    public void createInversion(String id, String inversionistId, String assetId, double amount, double purchasePrice, 
            LocalDate date, LocalTime time, double availableCapital, RiskProfile riskProfile, AssetType assetType){
        
        double initialInvestment = calculateInitialInvestment(purchasePrice, amount);

        // validar capital
        if (!validateAvailableCapital(availableCapital, initialInvestment)) {
            throw new InsufficientCapitalException("Capital insuficiente para registrar la inversión.");
        }

        // validar riesgo
        validateRiskProfile(riskProfile, assetType);

        Inversion inversion=new Inversion(id, inversionistId, assetId, amount, purchasePrice, date, time);

        try {
            repo.save(inversion);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error trying to save the inversion.", e);
        }
    }

    /**
     * Devuelve todas las inversiones registradas en persistencia.
     *
     * @return lista de {@link Inversion}; puede estar vacía
     */
    public List<Inversion> listInversions() {
        try {
            return repo.findAll();
        } catch (RuntimeException e) {
            throw new RuntimeException("Error trying to list the inversions.", e);
        }
    }

    /**
     * Calcula el valor actual de la posición: cantidad multiplicada por el precio vigente del activo.
     * Corresponde al requisito de valor actual = cantidad × precio actual.
     *
     * @param actualPrice precio de mercado actual del activo
     * @param amount cantidad de unidades
     * @return valor actual de la inversión
     */
    public double calculateActualValue(double actualPrice, double amount){
        return actualPrice*amount;
    }

    /**
     * Calcula la inversión inicial (desembolso en la compra): precio de compra por unidad multiplicado
     * por la cantidad adquirida.
     *
     * @param purchasePrice precio unitario en la compra
     * @param amount cantidad de unidades
     * @return capital invertido inicialmente
     */
    public double calculateInitialInvestment(double purchasePrice, double amount){
        return purchasePrice*amount;
    }

    /**
     * Calcula la ganancia o pérdida monetaria como diferencia entre el valor actual y la inversión inicial.
     * Un resultado positivo indica ganancia; uno negativo, pérdida.
     *
     * @param actualValue valor actual de la posición
     * @param initialInvestment inversión inicial (valor de compra total)
     * @return ganancia o pérdida en unidades monetarias
     */
    public double calculateEarnings(double actualValue, double initialInvestment){
        return actualValue-initialInvestment;
    }

    //Consultar historial de inversiones de un inversionista (Falta)

    /**
     * Calcula el rendimiento porcentual respecto a la inversión inicial: {@code (ganancia / inversión inicial) × 100},
     * donde la ganancia suele ser {@code valor actual − inversión inicial}.
     *
     * @param earning ganancia o pérdida monetaria (valor actual − inversión inicial)
     * @param initialInvestment inversión inicial; no puede ser cero
     * @return rendimiento en porcentaje
     * @throws ArithmeticException si la inversión inicial es cero
     */
    public double calculatePerformance(double earning, double initialInvestment){
        if (initialInvestment==0) {
            throw new ArithmeticException("Purchase Cannot be Zero");
        }
        return (earning/initialInvestment)*100;
    }

    /**
     * Comprueba que el capital disponible sea mayor o igual al monto de la inversión inicial,
     * cumpliendo la regla de no invertir más capital del disponible.
     *
     * @param availableCapital capital disponible del inversionista
     * @param initialInvestment monto total de la operación (precio de compra × cantidad)
     * @return {@code true} si la inversión es viable con el capital indicado
     */
    public boolean validateAvailableCapital(double availableCapital, double initialInvestment){
        return availableCapital>=initialInvestment;
    }

    /**
     * Verifica que el nivel de riesgo del tipo de activo no exceda el máximo permitido por el perfil
     * de riesgo del inversionista; en caso contrario lanza una excepción.
     *
     * @param riskProfile perfil de riesgo del inversionista
     * @param assetType tipo de activo, con un nivel de riesgo asociado
     * @throws IncompatibleRiskProfileException si el activo no es admisible para el perfil de riesgo
     */
    public void validateRiskProfile(RiskProfile riskProfile, AssetType assetType){
        if (assetType.getRiskLevel()>riskProfile.getMaxRisk()) {
            throw new IncompatibleRiskProfileException("Risk Profile "+riskProfile+" does not allows to investing in "+assetType);
        }
    }
}
