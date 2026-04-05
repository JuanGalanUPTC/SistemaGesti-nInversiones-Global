package co.edu.uptc.control;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import co.edu.uptc.exception.OperationCancelledException;
import co.edu.uptc.model.Investment;
import co.edu.uptc.model.Investor;
import co.edu.uptc.service.InvestmentService;
import co.edu.uptc.service.PortfolioService;
import co.edu.uptc.view.ConsoleView;

public class PortfolioController {

    private final PortfolioService portfolioService;
    private final InvestmentService investmentService;
    private final ConsoleView view;

    public PortfolioController(PortfolioService portfolioService, InvestmentService investmentService, ConsoleView view) {
        this.portfolioService = portfolioService;
        this.investmentService = investmentService;
        this.view = view;
    }

    public void handleTop5InvestorsReport() {
        try {
            view.showMessageByKey("msg.title.top5Investors");

            List<Investor> topInvestors = portfolioService.getTop5InvestorsByYield();

            if (topInvestors.isEmpty()) {
                view.showMessageByKey("msg.error.notEnoughData");
                return;
            }

            int rank = 1;
            for (Investor inv : topInvestors) {
                double totalInvested = portfolioService.calculateTotalInvested(inv);
                double currentValue = portfolioService.calculateCurrentPortfolioValue(inv);
                double yieldPercent = ((currentValue - totalInvested) / totalInvested) * 100.0;

                String formattedLine = String.format(view.getLocalizedText("msg.format.topInvestor"), 
                        rank, inv.getName(), yieldPercent, currentValue);
                
                view.printText(formattedLine);
                rank++;
            }
        } catch (RuntimeException e) {
            view.showMessageByKey("msg.error.system");
            view.printText(e.getMessage());
        }
    }

    /**
     * Genera un reporte de ganancias y pérdidas de TODAS las inversiones del sistema en un rango de fechas.
     */
    public void handleGlobalEarningsReport() {
        try {
            view.showMessageByKey("msg.title.globalReport");
            
            LocalDate startDate = promptForDate("msg.input.startDate");
            LocalDate endDate = promptForDate("msg.input.endDate");

            List<Investment> allInvestments = investmentService.listInvestments();
            double totalEarnings = portfolioService.calculateEarningsByPeriod(allInvestments, startDate, endDate);

            printReportResult(startDate, endDate, totalEarnings);

        } catch (OperationCancelledException e) {
            // Atrapa limpiamente la cancelación ("X")
            view.printText(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Atrapa errores de negocio (fechas invertidas)
            view.printText("Error: " + e.getMessage()); 
        } catch (RuntimeException e) {
            // Atrapa cualquier otro fallo inesperado
            view.showMessageByKey("msg.error.system");
            view.printText(e.getMessage());
        }
    }

    /**
     * Genera un reporte de ganancias y pérdidas de UN SOLO INVERSIONISTA en un rango de fechas.
     */
    public void handleInvestorEarningsReport() {
        try {
            view.showMessageByKey("msg.title.investorReport");
            
            String investorId = view.readStringInput("msg.input.investorId");
            
            // Si obtuvimos el ID y el usuario no cancelo, procedemos con las fechas
            LocalDate startDate = promptForDate("msg.input.startDate");
            LocalDate endDate = promptForDate("msg.input.endDate");

            List<Investment> investorPortfolio = investmentService.getInvestmentsByInvestorId(investorId);

            if (investorPortfolio.isEmpty()) {
                view.showMessageByKey("msg.error.noInvestmentsForInvestor");
                return;
            }

            double totalEarnings = portfolioService.calculateEarningsByPeriod(investorPortfolio, startDate, endDate);

            printReportResult(startDate, endDate, totalEarnings);

        } catch (OperationCancelledException e) {
            view.printText(e.getMessage());
        } catch (IllegalArgumentException e) {
            view.printText("Error: " + e.getMessage());
        } catch (RuntimeException e) {
            view.showMessageByKey("msg.error.system");
            view.printText(e.getMessage());
        }
    }

    // ---------------- MÉTODOS AUXILIARES PRIVADOS ----------------

    /**
     * Método auxiliar para pedir una fecha y manejar el error de formato automáticamente.
     */
    private LocalDate promptForDate(String messageKey) {
        while (true) {
            String dateStr = view.readStringInput(messageKey);
            try {
                return LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                // Ahora usamos el properties en lugar del texto en duro
                view.showMessageByKey("msg.error.invalidDateFormat");
            }
        }
    }

    /**
     * Método auxiliar para imprimir el resultado de forma estilizada y 100% bilingüe.
     */
    private void printReportResult(LocalDate start, LocalDate end, double earnings) {
        view.printText("\n=========================================");
        
        // Imprime el titulo usando String.format para inyectar las fechas en el texto traducido
        view.printText(String.format(view.getLocalizedText("msg.report.header"), start, end));
        
        if (earnings > 0) {
            view.printText(String.format(view.getLocalizedText("msg.report.profit"), earnings));
            view.showMessageByKey("msg.report.profitDesc");
        } else if (earnings < 0) {
            view.printText(String.format(view.getLocalizedText("msg.report.loss"), Math.abs(earnings)));
            view.showMessageByKey("msg.report.lossDesc");
        } else {
            view.showMessageByKey("msg.report.even");
            view.showMessageByKey("msg.report.evenDesc");
        }
        
        view.printText("=========================================\n");
    }
}