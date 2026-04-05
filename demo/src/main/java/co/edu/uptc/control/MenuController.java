package co.edu.uptc.control;

import co.edu.uptc.exception.OperationCancelledException;
import co.edu.uptc.service.AssetService;
import co.edu.uptc.service.InvestmentService;
import co.edu.uptc.service.InvestorService;
import co.edu.uptc.service.PortfolioService;
import co.edu.uptc.view.ConsoleView;

public class MenuController {

    private final ConsoleView view;
    private final AssetController assetController;
    private final InvestorController investorController;
    private final InvestmentController investmentController;
    private final PortfolioController portfolioController;

    public MenuController(ConsoleView view) {
        this.view = view;

        // Servicios únicos
        AssetService assetService = new AssetService();
        InvestorService investorService = new InvestorService();
        InvestmentService investmentService = new InvestmentService();
        PortfolioService portfolioService = new PortfolioService(investmentService, assetService);

        // Controladores inyectados
        this.assetController = new AssetController(assetService, view);
        this.investorController = new InvestorController(investorService, view);
        this.investmentController = new InvestmentController(investmentService, assetService, investorService, view);
        this.portfolioController = new PortfolioController(portfolioService, investmentService, view);
    }

    /**
     * MENÚ DE INICIO (Selector de acceso)
     * Usa tus llaves: menu.start.title, menu.start.option.x
     */
    public void runStartMenu() {
        boolean exit = false;
        while (!exit) {
            try {
                view.showMessageByKey("menu.start.title");
                view.showMessageByKey("menu.start.option.1");
                view.showMessageByKey("menu.start.option.2");
                view.showMessageByKey("menu.start.option.3");
                view.showMessageByKey("menu.start.option.4");
                view.showMessageByKey("menu.start.option.0");

                int option = view.readIntInput("msg.input.select");

                switch (option) {
                    case 1: investorController.handleCreateInvestor(); break;
                    case 2: runInvestorPortal(); break;
                    case 3: 
                        if (handleAdminLogin()) {
                            runAdminMenu(); 
                        }
                        break;
                    case 4: changeLanguage(); break;
                    case 0: 
                        exit = true; 
                        view.showMessageByKey("msg.goodbye"); 
                        break;
                    default: view.showMessageByKey("msg.error.invalid");
                }
            } catch (OperationCancelledException e) {
                view.printText(e.getMessage());
            } catch (Exception e) {
                view.showMessageByKey("msg.error.invalidInput");
            }
        }
    }

    /**
     * MENÚ DEL ADMINISTRADOR
     * Usa tus llaves: menu.title, menu.option.x
     */
    private void runAdminMenu() {
        boolean back = false;
        while (!back) {
            try {
                view.showMessageByKey("menu.title");
                view.showMessageByKey("menu.option.1");
                view.showMessageByKey("menu.option.2");
                view.showMessageByKey("menu.option.3");
                view.showMessageByKey("menu.option.4");
                view.showMessageByKey("menu.option.5");
                view.showMessageByKey("menu.option.0");

                int option = view.readIntInput("msg.input.select");

                switch (option) {
                    case 1: runInvestorMenu(); break;
                    case 2: runAssetMenu(); break;
                    case 3: runInvestmentMenu(); break;
                    case 4: runPortfolioMenu(); break;
                    case 5: changeLanguage(); break;
                    case 0: back = true; break;
                    default: view.showMessageByKey("msg.error.invalid");
                }
            } catch (OperationCancelledException e) {
                view.printText(e.getMessage());
            } catch (Exception e) {
                view.showMessageByKey("msg.error.invalidInput");
            }
        }
    }

    /**
     * MENÚ DEL INVERSIONISTA (Portal de Usuario)
     * Usa tus llaves: menu.investor.title, menu.investor.option.x
     */
    private void runInvestorPortal() {
        try {
            // Primero el Login como pediste en tus llaves
            investorController.handleLogin();
            
            boolean logout = false;
            while (!logout) {
                view.showMessageByKey("menu.investor.title");
                view.showMessageByKey("menu.investor.option.1");
                view.showMessageByKey("menu.investor.option.2");
                view.showMessageByKey("menu.investor.option.3");
                view.showMessageByKey("menu.investor.option.4");
                view.showMessageByKey("menu.investor.option.0");

                int option = view.readIntInput("msg.input.select");

                switch (option) {
                    case 1: assetController.handleListAssets(); break;
                    case 2: investmentController.handleCreateInvestment(); break;
                    case 3: investmentController.handleListInvestmentsByInvestor(); break;
                    case 4: portfolioController.handleInvestorEarningsReport(); break;
                    case 0: logout = true; break;
                    default: view.showMessageByKey("msg.error.invalid");
                }
            }
        } catch (OperationCancelledException e) {
            view.printText(e.getMessage());
        } catch (Exception e) {
            view.showMessageByKey("msg.error.invalidInput");
        }
    }

    /**
     * Lógica para cambio de idioma
     */
    private void changeLanguage() {
        view.showMessageByKey("menu.lang.options");
        int lang = view.readIntInput("menu.lang.select");
        if (lang == 1) {
            view.loadLanguage("es");
        } else if (lang == 2) {
            view.loadLanguage("en");
        }
        view.showMessageByKey("msg.success.langChanged");
    }

    // --- Submenús de gestión (Se mantienen conectados a los controladores) ---

    private void runAssetMenu() {
        boolean back = false;
        while (!back) {
            view.showMessageByKey("menu.admin.assets.title");
            view.showMessageByKey("menu.admin.assets.options");
            int option = view.readIntInput("msg.input.select");
            switch (option) {
                case 1: assetController.handleCreateAsset(); break;
                case 2: assetController.handleListAssets(); break;
                case 3: assetController.handleUpdateAssetPrice(); break;
                case 4: assetController.handleFindByPriceRange(); break;
                case 0: back = true; break;
                default: view.showMessageByKey("msg.error.invalid");
            }
        }
    }

    private void runInvestorMenu() {
        boolean back = false;
        while (!back) {
            view.showMessageByKey("menu.admin.investors.title");
            view.showMessageByKey("menu.admin.investors.options");
            int option = view.readIntInput("msg.input.selectOption");
            switch (option) {
                case 1: investorController.handleListInvestors(); break;
                case 2: investorController.handleUpdateInvestor(); break;
                case 3: investorController.handleDeleteInvestor(); break;
                case 0: back = true; break;
                default: view.showMessageByKey("msg.error.invalid");
            }
        }
    }

    private void runInvestmentMenu() {
        boolean back = false;
        while (!back) {
            view.showMessageByKey("msg.menu.investment.title");
            view.printText("1. Listar | 2. Recalcular Mercado | 0. Volver");
            int option = view.readIntInput("msg.input.select");
            switch (option) {
                case 1: investmentController.handleListAllInvestments(); break;
                case 2: investmentController.handleUpdateAssetPriceAndRecalculate(); break;
                case 0: back = true; break;
            }
        }
    }
    /**
     * Valida el acceso al menú del administrador.
     */
    private boolean handleAdminLogin() {
        try {
            // Lee la contraseña usando tu llave del properties
            String password = view.readStringInput("msg.input.adminPassword");
            
            // Aquí defines tu contraseña de administrador
            if (password.equals("admin123")) {
                view.showMessageByKey("msg.success.adminLogin");
                return true;
            } else {
                view.showMessageByKey("msg.error.invalidPassword");
                return false;
            }
        } catch (OperationCancelledException e) {
            view.printText(e.getMessage());
            return false;
        }
    }

    private void runPortfolioMenu() {
        boolean back = false;
        while (!back) {
            view.showMessageByKey("msg.menu.portfolio.title");
            view.showMessageByKey("msg.menu.portfolio.opt1");
            view.showMessageByKey("msg.menu.portfolio.opt2");
            view.showMessageByKey("msg.menu.portfolio.opt3");
            view.printText("0. Volver");
            int option = view.readIntInput("msg.input.select");
            switch (option) {
                case 1: portfolioController.handleTop5InvestorsReport(); break;
                case 2: portfolioController.handleGlobalEarningsReport(); break;
                case 3: portfolioController.handleInvestorEarningsReport(); break;
                case 0: back = true; break;
            }
        }
    }
}