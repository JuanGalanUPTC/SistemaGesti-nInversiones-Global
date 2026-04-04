package co.edu.uptc.control;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import co.edu.uptc.model.Investment;
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

    /**
     * Genera un reporte de ganancias y pérdidas de TODAS las inversiones del sistema en un rango de fechas.
     */
    public void handleGlobalEarningsReport() {
        try {
            view.showMessageByKey("msg.title.globalReport");
            
            LocalDate startDate = promptForDate("msg.input.startDate");
            LocalDate endDate = promptForDate("msg.input.endDate");

            // Obtenemos todas las inversiones del sistema
            List<Investment> allInvestments = investmentService.listInvestments();

            // Usamos tu servicio para calcular el total
            double totalEarnings = portfolioService.calculateEarningsByPeriod(allInvestments, startDate, endDate);

            printReportResult(startDate, endDate, totalEarnings);

        } catch (IllegalArgumentException e) {
            view.printText("❌ Regla de negocio: " + e.getMessage()); // Atrapa "Start date cannot be after end date"
        } catch (RuntimeException e) {
            view.printText("❌ Operación cancelada o error en el reporte: " + e.getMessage());
        }
    }

    /**
     * Genera un reporte de ganancias y pérdidas de UN SOLO INVERSIONISTA en un rango de fechas.
     */
    public void handleInvestorEarningsReport() {
        try {
            view.showMessageByKey("msg.title.investorReport");
            
            String investorId = view.readStringInput("msg.input.investorId");
            LocalDate startDate = promptForDate("msg.input.startDate");
            LocalDate endDate = promptForDate("msg.input.endDate");

            // Obtenemos solo las inversiones de ese inversionista
            List<Investment> investorPortfolio = investmentService.getInvestmentsByInvestorId(investorId);

            if (investorPortfolio.isEmpty()) {
                view.showMessageByKey("msg.error.noInvestmentsForInvestor");
                return;
            }

            // Usamos tu MISMO servicio (¡Magia de la reutilización de código!)
            double totalEarnings = portfolioService.calculateEarningsByPeriod(investorPortfolio, startDate, endDate);

            printReportResult(startDate, endDate, totalEarnings);

        } catch (IllegalArgumentException e) {
            view.printText("❌ Regla de negocio: " + e.getMessage());
        } catch (RuntimeException e) {
            view.printText("❌ Operación cancelada o error en el reporte: " + e.getMessage());
        }
    }

    // ---------------- MÉTODOS AUXILIARES PRIVADOS ----------------

    /**
     * Método auxiliar para pedir una fecha y manejar el error de formato automáticamente.
     */
    private LocalDate promptForDate(String messageKey) {
        while (true) {
            String dateStr = view.readStringInput(messageKey); // En tu properties debe decir algo como: "Ingrese fecha (AAAA-MM-DD):"
            try {
                return LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                view.printText("❌ Error: Formato de fecha inválido. Por favor use AAAA-MM-DD (Ejemplo: 2023-12-31).");
            }
        }
    }

    /**
     * Método auxiliar para imprimir el resultado de forma estilizada y no repetir código.
     */
    private void printReportResult(LocalDate start, LocalDate end, double earnings) {
        view.printText("\n=========================================");
        view.printText("📊 REPORTE DEL " + start + " AL " + end);
        
        if (earnings > 0) {
            view.printText("📈 Rendimiento Neto: +$" + String.format("%.2f", earnings));
            view.printText("✅ El portafolio generó GANANCIAS en este periodo.");
        } else if (earnings < 0) {
            view.printText("📉 Rendimiento Neto: -$" + String.format("%.2f", Math.abs(earnings)));
            view.printText("⚠️ El portafolio generó PÉRDIDAS en este periodo.");
        } else {
            view.printText("⚖️ Rendimiento Neto: $0.00");
            view.printText("El portafolio se mantuvo en punto de equilibrio.");
        }
        view.printText("=========================================\n");
    }
}