package co.edu.uptc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.gson.reflect.TypeToken;

import co.edu.uptc.exception.InsufficientCapitalException;
import co.edu.uptc.exception.InvestorNotFoundException;
import co.edu.uptc.model.Investor;
import co.edu.uptc.model.enums.RiskProfile;
import co.edu.uptc.repository.JsonRepository;

class InvestorServiceTest {

    @TempDir
    Path tempDir;

    private InvestorService service;

    @BeforeEach
    void setUp() {
        Type type = new TypeToken<List<Investor>>() {}.getType();
        JsonRepository<Investor> repo = new JsonRepository<>(tempDir.resolve("investors.json").toString(), type);
        service = new InvestorService(repo);
    }

    @Test
    void createInvestor_and_findById_persisted() {
        service.createInvestor("inv-1", "Ana", "ana@test.com", 5000.0, RiskProfile.MODERATE, Collections.emptyList());

        Investor found = service.findById("inv-1");
        assertNotNull(found);
        assertEquals("Ana", found.getName());
        assertEquals(5000.0, found.getAvailableCapital(), 0.0001);
        assertEquals(RiskProfile.MODERATE, found.getRiskProfile());
    }

    @Test
    void findById_returnsNullWhenMissing() {
        assertNull(service.findById("no-existe"));
    }

    @Test
    void updateCapital_reducesAvailableCapital() {
        service.createInvestor("inv-2", "Luis", "luis@test.com", 1000.0, RiskProfile.CONSERVATIVE, Collections.emptyList());

        service.updateCapital("inv-2", 250.0);

        assertEquals(750.0, service.getAvailableCapital("inv-2"), 0.0001);
    }

    @Test
    void updateCapital_throwsInsufficientCapital() {
        service.createInvestor("inv-3", "Bea", "bea@test.com", 50.0, RiskProfile.AGGRESSIVE, Collections.emptyList());

        assertThrows(InsufficientCapitalException.class, () -> service.updateCapital("inv-3", 100.0));
    }

    @Test
    void updateCapital_throwsWhenInvestorMissing() {
        assertThrows(InvestorNotFoundException.class, () -> service.updateCapital("missing", 10.0));
    }

    @Test
    void getRiskProfile_returnsProfile() {
        service.createInvestor("inv-4", "Cris", "cris@test.com", 100.0, RiskProfile.AGGRESSIVE, Collections.emptyList());

        assertEquals(RiskProfile.AGGRESSIVE, service.getRiskProfile("inv-4"));
    }

    @Test
    void getRiskProfile_throwsWhenMissing() {
        assertThrows(InvestorNotFoundException.class, () -> service.getRiskProfile("no-id"));
    }

    @Test
    void getAvailableCapital_throwsWhenMissing() {
        assertThrows(InvestorNotFoundException.class, () -> service.getAvailableCapital("no-id"));
    }
}
