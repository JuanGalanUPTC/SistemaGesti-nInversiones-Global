package co.edu.uptc.control;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import co.edu.uptc.exception.IncompatibleRiskProfileException;
import co.edu.uptc.exception.InsufficientCapitalException;
import co.edu.uptc.exception.OperationCancelledException;
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

            String investmentId = view.readStringInput("msg.input.investmentId");
            String investorId = view.readStringInput("msg.input.investorId");
            String assetId = view.readStringInput("msg.input.assetId");
            double amount = view.readDoubleInput("msg.input.amount");

            Asset asset = assetService.findById(assetId);
            if (asset == null) {
                view.showMessageByKey("msg.error.assetNotFound");
                return; 
            }

            Investor investor = investorService.findById(investorId);
            if (investor == null) {
                view.showMessageByKey("msg.error.investorNotFound");
                return;
            }

            double purchasePrice = asset.getActualPrice();
            double currentValue = purchasePrice * amount; 
            double yieldPercentage = 0.0; 
            LocalDate date = LocalDate.now();
            LocalTime time = LocalTime.now();

            investmentService.createInvestment(
                investmentId, investorId, assetId, amount, currentValue, yieldPercentage, 
                purchasePrice, date, time, 
                investor.getAvailableCapital(), investor.getRiskProfile(), asset.getAssetType()
            );

            // IMPORTANTE: Aquí deberías descontar el capital del inversionista
            // investorService.deductCapital(investorId, currentValue);

            view.showMessageByKey("msg.success.investmentCreated");

        } catch (OperationCancelledException e) {
            view.printText(e.getMessage());
        } catch (InsufficientCapitalException | IncompatibleRiskProfileException e) {
            view.printText("Regla de Negocio: " + e.getMessage());
        } catch (RuntimeException e) {
            view.showMessageByKey("msg.error.system");
            view.printText(e.getMessage());
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
            view.showMessageByKey("msg.error.system");
            view.printText(e.getMessage());
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
                
                String totalMsg = String.format(view.getLocalizedText("msg.info.totalPortfolioValue"), totalPortfolioValue);
                view.printText(totalMsg);
            }
        } catch (OperationCancelledException e) {
            view.printText(e.getMessage());
        } catch (RuntimeException e) {
            view.showMessageByKey("msg.error.system");
            view.printText(e.getMessage());
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

        } catch (OperationCancelledException e) {
            view.printText(e.getMessage());
        } catch (RuntimeException e) {
            view.printText("Error: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar privado para imprimir los detalles de una inversión.
     */
    private void printInvestmentDetails(Investment inv) {
        double initialInvestment = investmentService.calculateInitialInvestment(inv.getPurchasePrice(), inv.getAmount());
        double earnings = investmentService.calculateEarnings(inv.getCurrentValue(), initialInvestment);
        
        String earningsStr = (earnings >= 0) ? "(+$" + String.format("%.2f", earnings) + ")" : "(-$" + String.format("%.2f", Math.abs(earnings)) + ")";

        String detailLine = String.format(view.getLocalizedText("msg.format.investmentDetail"),
            inv.getId(), inv.getAssetId(), inv.getAmount(), inv.getCurrentValue(), inv.getYieldPercentage(), earningsStr);
        
        view.printText(detailLine);
    }
}