package co.edu.uptc.control;

import java.util.ArrayList;
import java.util.List;

import co.edu.uptc.exception.InvestorNotFoundException;
import co.edu.uptc.model.Investor;
import co.edu.uptc.model.enums.RiskProfile;
import co.edu.uptc.service.InvestorService;
import co.edu.uptc.view.ConsoleView;

public class InvestorController {

    private final InvestorService investorService;
    private final ConsoleView view;

    public InvestorController(InvestorService investorService, ConsoleView view) {
        this.investorService = investorService;
        this.view = view;
    }

    /**
     * Maneja el registro de un nuevo inversionista.
     */
    public void handleCreateInvestor() {
        try {
            view.showMessageByKey("msg.title.createInvestor");

            String id = view.readStringInput("msg.input.investorId");
            String name = view.readStringInput("msg.input.investorName");
            String email = view.readStringInput("msg.input.investorEmail");
            double capital = view.readDoubleInput("msg.input.availableCapital");

            String riskStr = view.readStringInput("msg.input.riskProfile"); // Ej: CONSERVADOR, MODERADO, AGRESIVO
            RiskProfile riskProfile = RiskProfile.valueOf(riskStr.toUpperCase());

            // Enviamos la lista vacía al momento de la creación tal como lo requiere tu servicio
            investorService.createInvestor(id, name, email, capital, riskProfile, new ArrayList<>());
            view.showMessageByKey("msg.success.investorCreated");

        } catch (IllegalArgumentException e) {
            view.printText("❌ Error: Perfil de riesgo inválido. Debe ser CONSERVADOR, MODERADO o AGRESIVO.");
        } catch (RuntimeException e) {
            view.printText("❌ Error del sistema: " + e.getMessage());
        }
    }

    /**
     * Maneja el listado general de inversionistas.
     */
    public void handleListInvestors() {
        try {
            view.showMessageByKey("msg.title.listInvestors");
            
            // Usamos tu método listInversionists()
            List<Investor> investors = investorService.listInversionists();

            if (investors.isEmpty()) {
                view.showMessageByKey("msg.error.noInvestors");
            } else {
                for (Investor inv : investors) {
                    view.printText(String.format("- [ID: %s] %s | Correo: %s | Capital Disp: $%.2f | Riesgo: %s",
                            inv.getId(), inv.getName(), inv.getEmail(), inv.getAvailableCapital(), inv.getRiskProfile()));
                }
            }
        } catch (RuntimeException e) {
            view.printText("❌ Error al listar inversionistas: " + e.getMessage());
        }
    }

    /**
     * Maneja la modificación de datos de un inversionista existente.
     */
    public void handleUpdateInvestor() {
        try {
            view.showMessageByKey("msg.title.updateInvestor");

            String id = view.readStringInput("msg.input.investorId");
            view.printText("ℹ️  (Deja en blanco y presiona Enter si no deseas modificar un campo)");

            String newName = view.readStringInput("msg.input.newName");
            String newEmail = view.readStringInput("msg.input.newEmail");
            String newRiskProfile = view.readStringInput("msg.input.newRiskProfile");

            // Pasamos los textos directo al servicio. Tu lógica en el servicio ignora los vacíos maravillosamente.
            investorService.updateInvestor(id, newName, newEmail, newRiskProfile);
            view.showMessageByKey("msg.success.investorUpdated");

        } catch (InvestorNotFoundException e) {
            view.printText("❌ " + e.getMessage());
        } catch (IllegalArgumentException e) {
            view.printText("❌ " + e.getMessage()); // Atrapa el error que lanzaste al validar el Enum
        } catch (RuntimeException e) {
            view.printText("❌ Error al actualizar: " + e.getMessage());
        }
    }

    /**
     * Maneja la eliminación de un inversionista.
     */
    public void handleDeleteInvestor() {
        try {
            view.showMessageByKey("msg.title.deleteInvestor");
            String id = view.readStringInput("msg.input.investorId");

            boolean deleted = investorService.delete(id);
            if (deleted) {
                view.showMessageByKey("msg.success.investorDeleted");
            } else {
                view.printText("❌ No se pudo eliminar: No se encontró un inversionista con ese ID.");
            }
        } catch (RuntimeException e) {
            view.printText("❌ Error al eliminar: " + e.getMessage());
        }
    }
}