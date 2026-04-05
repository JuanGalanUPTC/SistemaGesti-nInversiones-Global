package co.edu.uptc.view;

import java.util.InputMismatchException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Scanner;

import co.edu.uptc.exception.OperationCancelledException;

public class ConsoleView {
    
    private final Scanner scanner;
    private ResourceBundle messages;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
        loadLanguage("es"); // Español por defecto al iniciar
    }

    /**
     * Carga el archivo de propiedades según el idioma solicitado.
     * @param lang "es" para español, "en" para inglés.
     */
    /**
     * Carga el archivo de propiedades según el idioma solicitado.
     * @param lang "es" para español, "en" para inglés.
     */
    public void loadLanguage(String lang) {
        Locale locale = Locale.of(lang); 
        
        // Le indicamos que el archivo base "messages" está dentro de la carpeta/paquete "i18n"
        this.messages = ResourceBundle.getBundle("i18n.messages", locale);
    }

    /**
     * TRUCO SALVAVIDAS: Intenta buscar la llave en el .properties. 
     * Si no existe (porque le pasamos un texto normal concatenado), devuelve el texto tal cual.
     */
    public String getLocalizedText(String textOrKey) {
        try {
            return messages.getString(textOrKey);
        } catch (MissingResourceException e) {
            return textOrKey; // Si no es una llave, asume que es texto plano y lo devuelve
        }
    }

    // ---------------- MÉTODOS DE SALIDA (OUTPUT) ----------------

    public void showMessageByKey(String key) {
        System.out.println(getLocalizedText(key));
    }

    public void printText(String text) {
        System.out.println(getLocalizedText(text));
    }

    // ---------------- MÉTODOS DE ENTRADA (INPUT) ----------------

   public String readStringInput(String messageKey) {
        // Obtenemos el texto principal de la pregunta (ej: "Ingrese ID:")
        String prompt = getLocalizedText(messageKey);
        
        // Obtenemos el texto de la pista para cancelar según el idioma actual
        String cancelHint = getLocalizedText("msg.info.cancelHint");
        
        // Imprimimos la combinación de ambos
        System.out.print(prompt + cancelHint); 
        
        String input = scanner.nextLine().trim();
        
        if (input.equalsIgnoreCase("x")) {
            throw new OperationCancelledException(getLocalizedText("msg.info.processCancelled"));
        }
        
        return input;
    }
    public double readDoubleInput(String messageKey) {
        while (true) {
            try {
                String input = readStringInput(messageKey); // Reutilizamos readStringInput que ya tiene la lógica de la "X"
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println(getLocalizedText("msg.error.invalidNumber"));
            }
        }
    }

    public int readIntInput(String promptKey) {
        while (true) {
            try {
                System.out.print(getLocalizedText(promptKey) + " ");
                int input = scanner.nextInt();
                scanner.nextLine(); // Limpiar el "Enter" que queda en el buffer
                return input;
            } catch (InputMismatchException e) {
                System.out.println(getLocalizedText("msg.error.invalid"));
                scanner.nextLine(); // Limpiar la basura del buffer
            }
        }
    }

    // ---------------- MENÚS DEL SISTEMA ----------------

    public void showStartMenu() {
        System.out.println("\n" + getLocalizedText("menu.start.title"));
        System.out.println(getLocalizedText("menu.start.option.1"));
        System.out.println(getLocalizedText("menu.start.option.2"));
        System.out.println(getLocalizedText("menu.start.option.3"));
        System.out.println(getLocalizedText("menu.start.option.4"));
        System.out.println(getLocalizedText("menu.start.option.0"));
    }

    public void showAdminMenu() {
        System.out.println("\n" + getLocalizedText("menu.title"));
        System.out.println(getLocalizedText("menu.option.1"));
        System.out.println(getLocalizedText("menu.option.2"));
        System.out.println(getLocalizedText("menu.option.3"));
        System.out.println(getLocalizedText("menu.option.4"));
        System.out.println(getLocalizedText("menu.option.0"));
    }

    public void showInvestorDashboard() {
        System.out.println("\n" + getLocalizedText("menu.investor.title"));
        System.out.println(getLocalizedText("menu.investor.option.1"));
        System.out.println(getLocalizedText("menu.investor.option.2"));
        System.out.println(getLocalizedText("menu.investor.option.3"));
        System.out.println(getLocalizedText("menu.investor.option.4"));
        System.out.println(getLocalizedText("menu.investor.option.0"));
    }
}