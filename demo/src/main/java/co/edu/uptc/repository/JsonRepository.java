package co.edu.uptc.repository;

import com.google.gson.*;
import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

// ¡AQUÍ ESTÁ LA MAGIA! Ahora sí implementa la interfaz
public class JsonRepository<T> implements Repository<T> {

    private final String filePath;
    private final Type type;
    private final Gson gson;

    public JsonRepository(String filePath, Type type) {
        this.filePath = filePath;
        this.type = type;
        this.gson = createGson();
    }

    private Gson createGson() {
        return new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) 
        (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
    
        .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) 
            (json, typeOfT, context) -> LocalDate.parse(json.getAsString()))
        
        .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) 
            (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
        
        .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) 
            (json, typeOfT, context) -> LocalTime.parse(json.getAsString()))
        
        .setPrettyPrinting()
        .create();
    }

    @Override
    public List<T> findAll() {
        try (Reader reader = new FileReader(filePath)) {
            List<T> data = gson.fromJson(reader, type);
            return data != null ? data : new ArrayList<>();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (JsonSyntaxException e) {

            return new ArrayList<>();
        }
    }

    public void saveAll(List<T> data) {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(T element) {
        List<T> data = findAll();
        data.add(element);
        saveAll(data);
    }

    @Override
    public void replaceAll(List<T> data) {
        saveAll(data);
    }

    // NUEVO: Método para buscar genéricamente
    @Override
    public Optional<T> findBy(Predicate<T> condition) {
        return findAll().stream().filter(condition).findFirst();
    }

    // NUEVO: Método para eliminar genéricamente
    @Override
    public void deleteBy(Predicate<T> condition) {
        List<T> data = findAll();
        boolean removed = data.removeIf(condition);
        if (removed) {
            saveAll(data);
        }
    }
}