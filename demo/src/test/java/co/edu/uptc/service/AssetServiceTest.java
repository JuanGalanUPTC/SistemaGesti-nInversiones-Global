package co.edu.uptc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.gson.reflect.TypeToken;

import co.edu.uptc.exception.AssetNotFoundException;
import co.edu.uptc.model.Asset;
import co.edu.uptc.model.enums.AssetType;
import co.edu.uptc.repository.JsonRepository;

class AssetServiceTest {

    @TempDir
    Path tempDir;

    private AssetService service;

    @BeforeEach
    void setUp() {
        Type type = new TypeToken<List<Asset>>() {}.getType();
        JsonRepository<Asset> repo = new JsonRepository<>(tempDir.resolve("assets.json").toString(), type);
        service = new AssetService(repo);
    }

    @Test
    void getPrice_returnsPriceWhenAssetExists() {
        service.createAsset("AS-1", "Test Bond", AssetType.BOND, 12.5, 0.02);

        assertEquals(12.5, service.getPrice("AS-1"), 0.0001);
    }

    @Test
    void getPrice_throwsWhenAssetMissing() {
        assertThrows(AssetNotFoundException.class, () -> service.getPrice("unknown"));
    }
}
