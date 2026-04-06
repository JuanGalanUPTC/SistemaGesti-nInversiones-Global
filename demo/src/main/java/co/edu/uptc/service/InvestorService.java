package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import co.edu.uptc.exception.InsufficientCapitalException;
import co.edu.uptc.exception.InvestorNotFoundException;
import co.edu.uptc.model.Investment;
import co.edu.uptc.model.Investor;
import co.edu.uptc.model.enums.RiskProfile;
import co.edu.uptc.repository.JsonRepository;

/**
 * Servicio de gestión de inversionistas: registro, consulta y actualización del capital
 * disponible según los requisitos de administración de inversionistas.
 */
public class InvestorService {
    private final JsonRepository<Investor> repo;

    public InvestorService() {
        Type type = new TypeToken<List<Investor>>() {}.getType();
        this.repo = new JsonRepository<>("demo\\src\\main\\resources\\investor.json\\", type);
    }

    public InvestorService(JsonRepository<Investor> repo) {
        this.repo = repo;
    }

    /**
     * Registra un nuevo inversionista con los datos obligatorios (identificador, nombre,
     * correo, capital disponible y perfil de riesgo). La lista de inversiones se inicializa vacía.
     *
     * @param id identificador único del inversionista
     * @param name nombre completo
     * @param email correo electrónico
     * @param availableCapital capital disponible para invertir
     * @param riskProfile perfil de riesgo (conservador, moderado o agresivo)
     * @param inversions no utilizado; se persiste siempre una lista vacía al crear
     */
    public void createInvestor(String id, String name, String email, double availableCapital, RiskProfile riskProfile,
            List<Investment> inversions) {
        try {
            repo.save(new Investor(id, name, email, availableCapital, riskProfile, new ArrayList<>()));
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al registrar el inversionista.", e);
        }
    }

    /**
     * Obtiene todos los inversionistas almacenados (consulta general).
     *
     * @return lista de {@link Investor}; puede estar vacía si no hay registros
     */
    public List<Investor> listInversionists() {
        try {
            return repo.findAll();
        } catch (RuntimeException e) {
            throw new RuntimeException("Error trying to list the investors.", e);
        }
    }

    //Modificar 
    // Método para modificar los datos de un inversionista existente
    public void updateInvestor(String id, String newName, String newEmail, String newRiskProfile) {
        List<Investor> investors = repo.findAll();
        boolean isUpdated = false;

        for (Investor investor : investors) {
            if (investor.getId().equals(id)) {
                
                if (newName != null && !newName.trim().isEmpty()) {
                    investor.setName(newName);
                }
                
                if (newEmail != null && !newEmail.trim().isEmpty()) {
                    investor.setEmail(newEmail);
                }
                
                // Conversión del String al Enum
                if (newRiskProfile != null && !newRiskProfile.trim().isEmpty()) {
                    try {
                        // valueOf busca el Enum exacto. Usamos toUpperCase() por si el usuario escribe en minúsculas
                        RiskProfile parsedRisk = RiskProfile.valueOf(newRiskProfile.trim().toUpperCase());
                        investor.setRiskProfile(parsedRisk);
                    } catch (IllegalArgumentException e) {
                        // Si no coincide con CONSERVATIVE, MODERATE o AGRESSIVE, lanzamos este error
                        throw new IllegalArgumentException("Error: El perfil de riesgo '" + newRiskProfile + "' no es válido.");
                    }
                }
                
                isUpdated = true;
                break;
            }
        }

        if (isUpdated) {
            repo.replaceAll(investors);
        } else {
            throw new InvestorNotFoundException(id);
        }
    }
    //Eliminar 
    public boolean delete(String id) {
        List<Investor> investors = repo.findAll();
        boolean beDeleted = investors.removeIf(m -> m.getId().equals(id));
        if (beDeleted) {
            repo.replaceAll(investors);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Busca un inversionista por su identificador.
     *
     * @param id identificador del inversionista
     * @return el {@link Investor} encontrado, o {@code null} si no existe
     */
    public Investor findById(String id) {
        try {
            return repo.findAll()
                    .stream()
                    .filter(i -> i.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error trying to find the inversionist by id.", e);
        }
    }

    /**
     * Descuenta del capital disponible del inversionista el monto indicado tras una operación
     * (por ejemplo una compra), validando que no se invierta más capital del disponible.
     *
     * @param id identificador del inversionista
     * @param purchaseValue monto a descontar del capital disponible
     * @throws InsufficientCapitalException si el capital del inversionista es insuficiente
     * @throws InvestorNotFoundException si no existe el inversionista
     */
    public void updateCapital(String id, double purchaseValue) {
        try {
            List<Investor> investors = repo.findAll();

            for (Investor inv : investors) {
                if (inv.getId().equals(id)) {
                    if (inv.getAvailableCapital() < purchaseValue) {
                        throw new InsufficientCapitalException("Capital insuficiente para completar la operación.");
                    }
                    inv.setAvailableCapital(inv.getAvailableCapital() - purchaseValue);
                    repo.replaceAll(investors);
                    return;
                }
            }

            throw new InvestorNotFoundException("Investor not found with that id.");
        } catch (InsufficientCapitalException | InvestorNotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error trying to update the available capital.", e);
        }
    }

    /**
     * Obtiene el perfil de riesgo del inversionista, necesario para validar inversiones
     * según el tipo de activo.
     *
     * @param id identificador del inversionista
     * @return perfil de riesgo registrado
     * @throws InvestorNotFoundException si no existe un inversionista con el identificador dado
     */
    public RiskProfile getRiskProfile(String id) {
        Investor inv = findById(id);

        if (inv == null) {
            throw new InvestorNotFoundException("Investor not found with that id.");
        }

        return inv.getRiskProfile();
    }

    /**
     * Obtiene el capital disponible actual del inversionista por su identificador,
     * sin exponer el objeto completo (útil para validar una inversión antes de registrarla).
     *
     * @param id identificador del inversionista
     * @return capital disponible
     * @throws InvestorNotFoundException si no existe un inversionista con el identificador dado
     */
    public double getAvailableCapital(String id){
        Investor inv = findById(id);

        if (inv == null) {
            throw new InvestorNotFoundException("Inversionist not found with that id.");
        }

        return inv.getAvailableCapital();
    }
}
