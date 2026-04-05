package co.edu.uptc.service;

import co.edu.uptc.model.Investment;
import co.edu.uptc.repository.JsonRepository;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private final JsonRepository<Investment> investmentRepo;

    public ReportService() {
        Type type = new TypeToken<List<Investment>>() {}.getType();
        this.investmentRepo = new JsonRepository<>("investments.json", type);
    }

    
    private LocalDate getDateSafe(Investment inv) {
        return inv.getDate();
    }

    private double getProfitSafe(Investment inv) {
        return inv.getPurchasePrice();
    }

    private String getInvestorSafe(Investment inv) {
        return inv.getInversionistId();
    }

    private double getAmountSafe(Investment inv) {
        return inv.getAmount();
    }

    //Ganancias totales por periodo
    public double getTotalProfits(LocalDate start, LocalDate end) {
        double total = 0;

        for (Investment inv : investmentRepo.findAll()) {
            LocalDate date = getDateSafe(inv);

            if (date != null &&
                (date.isEqual(start) || date.isAfter(start)) &&
                (date.isEqual(end) || date.isBefore(end))) {

                total += getProfitSafe(inv);
            }
        }

        return total;
    }

    //Top 5 inversionistas
    public List<Map.Entry<String, Double>> getTopInvestors() {

        Map<String, Double> map = new HashMap<>();

        for (Investment inv : investmentRepo.findAll()) {
            String investor = getInvestorSafe(inv);
            double profit = getProfitSafe(inv);

            if (investor == null) continue;

            map.put(investor, map.getOrDefault(investor, 0.0) + profit);
        }

        return map.entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .collect(Collectors.toList());
    }

    //Exportar CSV
    public void exportToCSV(String path) {

        try (FileWriter writer = new FileWriter(path)) {

            writer.append("Investor,Amount,Profit\n");

            for (Investment inv : investmentRepo.findAll()) {

                writer.append(safe(getInvestorSafe(inv))).append(",")
                        .append(String.valueOf(getAmountSafe(inv))).append(",")
                        .append(String.valueOf(getProfitSafe(inv)))
                        .append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Exportar JSON
    public void exportToJSON(String path) {

        try (FileWriter writer = new FileWriter(path)) {

            String json = new com.google.gson.GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(investmentRepo.findAll());

            writer.write(json);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}