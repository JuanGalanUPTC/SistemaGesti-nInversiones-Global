package co.edu.uptc.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Inversion {
    private String id;
    private String inversionistId;
    private String assetId;
    private double amount;
    private double currentValue;
    private double YieldPercentage;
    private double purchasePrice;
    private LocalDate date;
    private LocalTime time;

    /** Constructor vacío de la clase Inversion. */
    public Inversion() {
    }

    public Inversion(String id, String inversionistId, String assetId, double amount, double currentValue,
            double yieldPercentage, double purchasePrice, LocalDate date, LocalTime time) {
        this.id = id;
        this.inversionistId = inversionistId;
        this.assetId = assetId;
        this.amount = amount;
        this.currentValue = currentValue;
        YieldPercentage = yieldPercentage;
        this.purchasePrice = purchasePrice;
        this.date = date;
        this.time = time;
    }



    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getInversionistId() {
        return inversionistId;
    }
    public void setInversionistId(String inversionistId) {
        this.inversionistId = inversionistId;
    }
    public String getAssetId() {
        return assetId;
    }
    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public double getPurchasePrice() {
        return purchasePrice;
    }
    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice= purchasePrice;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public LocalTime getTime() {
        return time;
    }
    public void setTime(LocalTime time) {
        this.time = time;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public double getYieldPercentage() {
        return YieldPercentage;
    }

    public void setYieldPercentage(double yieldPercentage) {
        YieldPercentage = yieldPercentage;
    }

    @Override
    public String toString() {
        return "Inversion [id=" + id + ", inversionistId=" + inversionistId + ", assetId=" + assetId + ", amount="
                + amount + ", currentValue=" + currentValue + ", YieldPercentage=" + YieldPercentage
                + ", purchasePrice=" + purchasePrice + ", date=" + date + ", time=" + time + "]";
    }

    
}
