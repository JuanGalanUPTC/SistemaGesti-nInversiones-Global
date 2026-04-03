package co.edu.uptc.service;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import co.edu.uptc.model.Inversion;
import co.edu.uptc.model.enums.AssetType;
import co.edu.uptc.model.enums.RiskProfile;
import co.edu.uptc.repository.JsonRepository;

public class InversionService { //Cálculos individuales
    JsonRepository<Inversion> repo;

    public InversionService() {
        Type type=new TypeToken<List<Inversion>>(){}.getType();
        repo=new JsonRepository<>("inversions.json", type);
    }

    public void createInversion(String id, String inversionistId, String assetId, double amount, double purchasePrice, 
            LocalDate date, LocalTime time, double availableCapital, RiskProfile riskProfile, AssetType assetType){
        
        double purchaseValue = calculatePurchaseValue(purchasePrice, amount);

        // validar capital
        if (!validateAvailableCapital(availableCapital, purchaseValue)) {
            throw new RuntimeException("Insufficient capital");
        }

        // validar riesgo
        validateRiskProfile(riskProfile, assetType);

        Inversion inversion=new Inversion(id, inversionistId, assetId, amount, purchasePrice, date, time);

        repo.save(inversion);
    }

    public List<Inversion> listInversions(){
        return repo.findAll();
    } 

    public double calculateActualValue(double actualPrice, double amount){ //Calcular Valor Actual
        return actualPrice*amount;
    }

    public double calculatePurchaseValue(double purchasePrice, double amount){  //Calcular Valor de Compra
        return purchasePrice*amount;
    };

    public double calculateEarnings(double actualValue, double purchaseValue){  //Calcular Ganancias
        return actualValue-purchaseValue;
    }

    public double calculatePerformance(double earning, double purchaseValue){   //Calcular Rendimiento
        if (purchaseValue==0) {
            throw new ArithmeticException("Purchase Cannot be Zero");
        }
        return (earning/purchaseValue)*100;
    }

    public boolean validateAvailableCapital(double availableCapital, double purchaseValue){
        return availableCapital>=purchaseValue;
    }

    public void validateRiskProfile(RiskProfile riskProfile, AssetType assetType){
        if (assetType.getRiskLevel()>riskProfile.getMaxRisk()) {
            throw new RuntimeException("Risk Profile "+riskProfile+" does not allows to investing in "+assetType);
        }
    }

    public double updateAvailableCapital(double availableCapital, double purchaseValue){
        return availableCapital-purchaseValue;
    }
}
