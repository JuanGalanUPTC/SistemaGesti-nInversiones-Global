package co.edu.uptc.view;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.MissingResourceException;

public class ConsoleView {
    private Scanner scanner;
    private ResourceBundle messages;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
        loadLanguage(Locale.of("es"));
    }

    public void loadLanguage(Locale locale) {
        this.messages = ResourceBundle.getBundle("i18n/messages", locale);
    }

    // ---------------- MENÚS ----------------

    public void showMainMenu() {
        System.out.println("\n" + messages.getString("menu.title"));
        System.out.println(messages.getString("menu.option.1"));
        System.out.println(messages.getString("menu.option.2"));
        System.out.println(messages.getString("menu.option.3"));
        System.out.println(messages.getString("menu.option.4"));
        System.out.println(messages.getString("menu.option.5"));
        System.out.println(messages.getString("menu.option.0"));
    }

    // ---------------- LECTURA DE DATOS ----------------

    public int readOption() {
        System.out.print(messages.getString("msg.input.select") + " ");
        while (!scanner.hasNextInt()) {
            System.out.println(messages.getString("msg.error.invalid"));
            scanner.next(); 
            System.out.print(messages.getString("msg.input.select") + " ");
        }
        int option = scanner.nextInt();
        scanner.nextLine(); // ¡CRUCIAL! Limpia el "Enter" del buffer
        return option;
    }

    // NUEVO: Método para leer textos (pide la llave de i18n para el mensaje)
    public String readStringInput(String promptKey) {
        System.out.print(messages.getString(promptKey) + " ");
        return scanner.nextLine();
    }

    // NUEVO: Método para leer decimales (para los precios)
    public double readDoubleInput(String promptKey) {
        System.out.print(messages.getString(promptKey) + " ");
        while (!scanner.hasNextDouble()) {
            System.out.println(messages.getString("msg.error.invalid"));
            scanner.next(); 
            System.out.print(messages.getString(promptKey) + " ");
        }
        double value = scanner.nextDouble();
        scanner.nextLine(); // Limpia el buffer
        return value;
    }

    // ---------------- IMPRESIÓN DE MENSAJES ----------------

    // Usa este para mensajes estáticos (como "msg.success.created")
    public void showMessageByKey(String key) {
        try {
            System.out.println(messages.getString(key));
        } catch (MissingResourceException e) {
            // Por si te equivocas escribiendo la llave, no se cae el programa
            System.out.println("!" + key + "!"); 
        }
    }

    // NUEVO: Usa este para imprimir datos dinámicos (Ej: El listado de activos)
    public void printText(String text) {
        System.out.println(text);
    }
}