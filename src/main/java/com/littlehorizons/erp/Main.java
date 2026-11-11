package com.littlehorizons.erp;

import com.littlehorizons.erp.controller.ControllerFactory;
import com.littlehorizons.erp.dao.DaoFactory;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.ConsoleEncoding;
import com.littlehorizons.erp.util.ConsoleInput;
import com.littlehorizons.erp.util.ConsolePrinter;
import com.littlehorizons.erp.view.MainMenuView;

/**
 * Entry point: prepares the console, builds the controllers and opens the main menu.
 */
public class Main {

    public static void main(String[] args) {
        ConsoleEncoding.configureUtf8();
        ConsolePrinter printer = new ConsolePrinter();
        AppLogger logger = AppLogger.getInstance();
        try {
            logger.info("Main", "Application started");
            ControllerFactory controllers = new ControllerFactory(new DaoFactory());
            new MainMenuView(controllers, new ConsoleInput(printer), printer).show();
            logger.info("Main", "Application closed by the user");
        } catch (Exception e) {
            // Global safety net: any unexpected error is logged and the user sees a friendly message
            logger.error("Main", "Unexpected error", e);
            printer.printError("Ocurrió un error inesperado y la aplicación debe cerrarse. "
                    + "El detalle quedó registrado en error_log.txt.");
        }
    }
}
