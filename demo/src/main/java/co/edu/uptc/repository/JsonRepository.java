package co.edu.uptc.repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class JsonRepository<T> implements Repository<T> { //AMBOS TIENEN <T>
    private String filename;
    private Type type;
    private Gson gson;

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                    @Override
                    public void write(JsonWriter out, LocalDate value) throws IOException {
                        if (value == null) {
                            out.nullValue();
                        } else {
                            out.value(value.toString());
                        }
                    }

                    @Override
                    public LocalDate read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) {
                            in.nextNull();
                            return null;
                        }
                        return LocalDate.parse(in.nextString());
                    }
                })
                .registerTypeAdapter(LocalTime.class, new TypeAdapter<LocalTime>() {
                    @Override
                    public void write(JsonWriter out, LocalTime value) throws IOException {
                        if (value == null) {
                            out.nullValue();
                        } else {
                            out.value(value.toString());
                        }
                    }

                    @Override
                    public LocalTime read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) {
                            in.nextNull();
                            return null;
                        }
                        return LocalTime.parse(in.nextString());
                    }
                })
                .create();
    }

    public JsonRepository(String filename, Type type) {
        this.filename = filename;
        this.type = type;
        this.gson = createGson();
    }

    @Override
    public void save(T entity) {
        List<T>data=findAll();
        data.add(entity);
        try (FileWriter writer= new FileWriter(filename)){
            gson.toJson(data,writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<T> findAll() {
        try (FileReader reader= new FileReader(filename)){
            List<T>data=gson.fromJson(reader, type);
            if (data==null) {
                return new ArrayList<>();
            }
            return data;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void replaceAll(List<T> data) {
        try (FileWriter writer= new FileWriter(filename)){
            gson.toJson(data,writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
