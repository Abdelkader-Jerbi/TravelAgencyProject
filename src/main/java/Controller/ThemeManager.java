package Controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ThemeManager {
    private static final BooleanProperty darkMode = new SimpleBooleanProperty(false);

    public static boolean isDarkMode() { return darkMode.get(); }
    public static void setDarkMode(boolean value) { darkMode.set(value); }
    public static BooleanProperty darkModeProperty() { return darkMode; }
} 