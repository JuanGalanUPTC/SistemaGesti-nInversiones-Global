package co.edu.uptc.control;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import co.edu.uptc.exception.IncompatibleRiskProfileException;
import co.edu.uptc.exception.InsufficientCapitalException;
import co.edu.uptc.model.Investor; 
import co.edu.uptc.model.Asset;
import co.edu.uptc.model.Investment;
import co.edu.uptc.service.AssetService;
import co.edu.uptc.service.InvestmentService;
import co.edu.uptc.service.InvestorService;
import co.edu.uptc.view.ConsoleView;

public class InvestmentController {

    private final InvestmentService investmentService;
    private final AssetService assetService;
    private final InvestorService investorService;
    private final ConsoleView view;

    // Inyección de dependencias con los nombres actualizados
    public InvestmentController(InvestmentService investmentService, 
                                AssetService assetService, 
                                InvestorService investorService, 
                                ConsoleView view) {
        this.investmentService = investmentService;
        this.assetService = assetService;
        this.investorService = investorService;
        this.view = view;
    }

    /**
     * Maneja el registro de una nueva inversión.
     */
    public void handleCreateInvestment() {
        try {
            view.showMessageByKey("msg.title.createInvestment");

            // 1. Pedimos solo los datos de entrada básicos
            String investmentId = view.readStringInput("msg.input.investmentId");
            String investorId = view.readStringInput("msg.input.investorId");
            String assetId = view.readStringInput("msg.input.assetId");
            double amount = view.readDoubleInput("msg.input.amount");

            // 2. Buscamos las entidades para obtener sus datos internos
            Asset asset = assetService.findById(assetId);
            if (asset == null) {
                view.printText("❌ Error: No se encontró un activo con el ID indicado.");
                return; 
            }

            Investor investor = investorService.findById(investorId);
            if (investor == null) {
                view.printText("❌ Error: No se encontró un inversionista con el ID indicado.");
                return;
            }

            // 3. Preparamos los datos automáticos para la creación
            double purchasePrice = asset.getActualPrice();
            double currentValue = purchasePrice * amount; 
            double yieldPercentage = 0.0; 
            LocalDate date = LocalDate.now();
            LocalTime time = LocalTime.now();

            // 4. Delegamos la creación al servicio con toda la info recopilada
            investmentService.createInvestment(
                investmentId, investorId, assetId, amount, currentValue, yieldPercentage, 
                purchasePrice, date, time, 
                investor.getAvailableCapital(), investor.getRiskProfile(), asset.getAssetType()
            );

            // 5. IMPORTANTE: Aquí deberías descontar el capital del inversionista
            // investorService.deductCapital(investorId, currentValue);

            view.showMessageByKey("msg.success.investmentCreated");

        } catch (InsufficientCapitalException | IncompatibleRiskProfileException e) {
            view.printText("❌ Regla de Negocio: " + e.getMessage());
        } catch (RuntimeException e) {
            view.printText("❌ Error del sistema: " + e.getMessage());
        }
    }

    /**
     * Maneja el listado de todas las inversiones en el sistema.
     */
    public void handleListAllInvestments() {
        try {
            view.showMessageByKey("msg.title.listInvestments");
            List<Investment> investments = investmentService.listInvestments();

            if (investments.isEmpty()) {
                view.showMessageByKey("msg.error.noInvestments");
            } else {
                for (Investment inv : investments) {
                    printInvestmentDetails(inv);
                }
            }
        } catch (RuntimeException e) {
            view.printText("Error al listar inversiones: " + e.getMessage());
        }
    }

    /**
     * Maneja la consulta del portafolio de un inversionista específico.
     */
    public void handleListInvestmentsByInvestor() {
        try {
            view.showMessageByKey("msg.title.investorPortfolio");
            String investorId = view.readStringInput("msg.input.investorId");

            List<Investment> portfolio = investmentService.getInvestmentsByInvestorId(investorId);

            if (portfolio.isEmpty()) {
                view.showMessageByKey("msg.error.noInvestmentsForInvestor");
            } else {
                double totalPortfolioValue = 0.0;

                for (Investment inv : portfolio) {
                    printInvestmentDetails(inv);
                    totalPortfolioValue += inv.getCurrentValue();
                }
                
                view.printText("\n--- VALOR TOTAL DEL PORTAFOLIO: $" + totalPortfolioValue + " ---");
            }
        } catch (RuntimeException e) {
            view.printText("Error al consultar el portafolio: " + e.getMessage());
        }
    }

    /**
     * Actualiza el precio de un activo y detona el recálculo masivo en cascada.
     */
    public void handleUpdateAssetPriceAndRecalculate() {
        try {
            view.showMessageByKey("msg.title.updateMarketPrice");
            
            String assetId = view.readStringInput("msg.input.assetId");
            double newPrice = view.readDoubleInput("msg.input.newPrice");

            investmentService.updateAssetPriceProcces(assetId, newPrice);
            
            view.showMessageByKey("msg.success.marketUpdated");

        } catch (RuntimeException e) {
            view.printText("❌ Error al actualizar el mercado: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar privado para imprimir los detalles de una inversión.
     */
    private void printInvestmentDetails(Investment inv) {
        double initialInvestment = investmentService.calculateInitialInvestment(inv.getPurchasePrice(), inv.getAmount());
        double earnings = investmentService.calculateEarnings(inv.getCurrentValue(), initialInvestment);
        
        String earningsStr = (earnings >= 0) ? "(+$" + earnings + ")" : "(-$" + Math.abs(earnings) + ")";

        view.printText(String.format(
            "- [ID: %s] Activo: %s | Cantidad: %.2f | Valor Actual: $%.2f | Rendimiento: %.2f%% %s",
            inv.getId(), inv.getAssetId(), inv.getAmount(), inv.getCurrentValue(), inv.getYieldPercentage(), earningsStr
        ));
    }
}