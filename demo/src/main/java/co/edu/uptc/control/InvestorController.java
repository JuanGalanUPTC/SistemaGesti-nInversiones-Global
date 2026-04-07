package co.edu.uptc.control;

import java.util.ArrayList;
import java.util.List;

import co.edu.uptc.exception.InvestorNotFoundException;
import co.edu.uptc.exception.OperationCancelledException;
import co.edu.uptc.model.Investment;
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
        
        // --- VALIDACIÓN DE CAPITAL ---
        double capital = view.readDoubleInput("msg.input.availableCapital");
        if (capital < 0) {
            view.showMessageByKey("msg.error.negativeCapital");
            return; // Cortamos la ejecución aquí
        }
        // -----------------------------

        String riskStr = view.readStringInput("msg.input.riskProfile").trim().toUpperCase(); 
        
        RiskProfile riskProfile;
        switch (riskStr) {
            case "CONSERVADOR": case "CONSERVATIVE": riskProfile = RiskProfile.CONSERVATIVE; break;
            case "MODERADO": case "MODERATE": riskProfile = RiskProfile.MODERATE; break;
            case "AGRESIVO": case "AGGRESSIVE": riskProfile = RiskProfile.AGGRESSIVE; break;
            default: throw new IllegalArgumentException("INVALID_RISK");
        }

        investorService.createInvestor(id, name, email, capital, riskProfile, new ArrayList<>());
        view.showMessageByKey("msg.success.investorCreated");

  } catch (IllegalArgumentException e) {
    // Manejo de errores de validación específicos
    switch (e.getMessage()) {
        case "ID_ALREADY_EXISTS":
            view.showMessageByKey("msg.error.idAlreadyExists");
            break;
        case "NEGATIVE_CAPITAL":
            view.showMessageByKey("msg.error.negativeCapital");
            break;
        case "INVALID_RISK":
            view.showMessageByKey("msg.error.invalidRisk");
            break;
        default:
            view.printText(e.getMessage());
    }
}
}
    /**
     * Maneja el proceso de inicio de sesión de un inversionista.
     * @return El ID del inversionista si el login es exitoso, o null si falla/cancela.
     */
    public String handleLogin() {
        try {
            view.showMessageByKey("msg.title.login");
            // Si el usuario escribe X, readStringInput lanzara OperationCancelledException
            String id = view.readStringInput("msg.input.loginId");

            Investor loggedInUser = investorService.findById(id);

            if (loggedInUser != null) {
                view.showMessageByKey("msg.success.loginWelcome");
                view.printText(loggedInUser.getName()); 
                return loggedInUser.getId(); 
            } else {
                view.showMessageByKey("msg.error.loginFailed");
                return null;
            }
        } catch (OperationCancelledException e) {
            // Atrapamos la excepcion si el usuario decide cancelar el login
            view.printText(e.getMessage());
            return null;
        } catch (RuntimeException e) {
            view.showMessageByKey("msg.error.loginSystem");
            view.printText(e.getMessage());
            return null;
        }
    }

    /**
     * Maneja el listado general de inversionistas.
     */
    public void handleListInvestors() {
        try {
            view.showMessageByKey("msg.title.listInvestors");
            
            List<Investor> investors = investorService.listInversionists();

            if (investors.isEmpty()) {
                view.showMessageByKey("msg.error.noInvestors");
            } else {
                for (Investor inv : investors) {
                    // Cambiamos las palabras en español por términos universales en el formateo de datos
                    view.printText(String.format("- [ID: %s] %s | Email: %s | Capital: $%.2f | Risk: %s",
                            inv.getId(), inv.getName(), inv.getEmail(), inv.getAvailableCapital(), inv.getRiskProfile()));
                }
            }
        } catch (RuntimeException e) {
            view.showMessageByKey("msg.error.listInvestors");
            view.printText(e.getMessage());
        }
    }

    /**
     * Maneja la modificación de datos de un inversionista existente.
     */
    public void handleUpdateInvestor() {
        try {
            view.showMessageByKey("msg.title.updateInvestor");

            String id = view.readStringInput("msg.input.investorId");
            view.showMessageByKey("msg.info.leaveBlank");

            String newName = view.readStringInput("msg.input.newName");
            String newEmail = view.readStringInput("msg.input.newEmail");
            String newRiskProfile = view.readStringInput("msg.input.newRiskProfile").trim().toUpperCase();
            
    
    RiskProfile riskProfile;

            switch (newRiskProfile) {
        // Casos para Conservador
        case "CONSERVADOR": 
        case "CONSERVATIVE": 
            riskProfile = RiskProfile.CONSERVATIVE; 
            break;
            
        // Casos para Moderado
        case "MODERADO": 
        case "MODERATE": 
            riskProfile = RiskProfile.MODERATE; 
            break;
            
        // Casos para Agresivo
        case "AGRESIVO": 
        case "AGGRESSIVE": 
            riskProfile = RiskProfile.AGGRESSIVE; 
            break;
            
        default: 
            throw new IllegalArgumentException("INVALID_RISK");
    }


            investorService.updateInvestorAtributes(id, newName, newEmail, riskProfile);
            view.showMessageByKey("msg.success.investorUpdated");

        } catch (InvestorNotFoundException | IllegalArgumentException e) {
            view.printText(e.getMessage()); 
        } catch (RuntimeException e) {
            view.showMessageByKey("msg.error.update");
            view.printText(e.getMessage());
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
                view.showMessageByKey("msg.error.deleteNotFound");
            }
        } catch (RuntimeException e) {
            view.showMessageByKey("msg.error.delete");
            view.printText(e.getMessage());
        }
    }
}