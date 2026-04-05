package co.edu.uptc.view;

import co.edu.uptc.control.MenuController;

public class Main {

    public static void main(String[] args) {
        // 1. Instanciamos unicamente la Vista
        ConsoleView view = new ConsoleView();

        // 2. Instanciamos SOLO el controlador principal del menu
        MenuController menuController = new MenuController(view);
        
        // 3. Arrancamos
        menuController.runStartMenu();
    }
}