package controllersAmineM;

import controllers.MenuController;

public class ApplicationContext {
    private static MenuController menuController;

    public static void setMenuController(MenuController controller) {
        menuController = controller;
    }

    public static MenuController getMenuController() {
        return menuController;
    }
}