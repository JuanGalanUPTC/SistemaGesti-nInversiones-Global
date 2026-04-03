package co.edu.uptc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.gson.reflect.TypeToken;

import co.edu.uptc.model.Asset;
import co.edu.uptc.model.Inversion;
import co.edu.uptc.model.enums.AssetType;
import co.edu.uptc.repository.JsonRepository;

class PortfolioServiceTest {

    @TempDir
    private Path tempDir;

    private PortfolioService portfolioService;

    @BeforeEach
    void setUp() {
        Type assetListType = new TypeToken<List<Asset>>() {}.getType();
        JsonRepository<Asset> assetRepo = new JsonRepository<>(tempDir.resolve("assets.json").toString(), assetListType);
        AssetService assetService = new AssetService(assetRepo);

        Type invListType = new TypeToken<List<Inversion>>() {}.getType();
        JsonRepository<Inversion> invRepo = new JsonRepository<>(tempDir.resolve("inversions.json").toString(), invListType);
        InversionService inversionService = new InversionService(invRepo);

        portfolioService = new PortfolioService(inversionService, assetService);

        assetService.createAsset("A1", "Activo prueba", AssetType.BOND, 10.0, 0.0);
    }

    @Test
    void calculateEarningsByPeriod_throwsWhenStartAfterEnd() {
        assertThrows(IllegalArgumentException.class,
                () -> portfolioService.calculateEarningsByPeriod(
                        List.of(),
                        LocalDate.of(2026, 2, 1),
                        LocalDate.of(2026, 1, 1)));
    }

    @Test
    void calculateEarningsByPeriod_sumsOnlyInversionsInRange() {
        Inversion inside = new Inversion("1", "inv", "A1", 2, 4.0, LocalDate.of(2026, 1, 15), LocalTime.NOON);
        Inversion outside = new Inversion("2", "inv", "A1", 1, 1.0, LocalDate.of(2025, 12, 1), LocalTime.NOON);

        double total = portfolioService.calculateEarningsByPeriod(
                List.of(inside, outside),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31));

        assertEquals(12.0, total, 0.0001);
    }

    @Test
    void calculateEarningsByPeriod_returnsZeroWhenNoMatches() {
        Inversion outside = new Inversion("2", "inv", "A1", 1, 1.0, LocalDate.of(2025, 12, 1), LocalTime.NOON);

        double total = portfolioService.calculateEarningsByPeriod(
                List.of(outside),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31));

        assertEquals(0.0, total, 0.0001);
    }
}
