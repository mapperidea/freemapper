package freemind.main;

import java.awt.Color;
import java.util.Properties;

public class ThemeManager {

    private static final String THEME_PROPERTY = "theme";
    private static final String LIGHT_THEME = "light";
    private static final String DARK_THEME = "dark";

    private final FreeMindMain frame;
    private String currentTheme;

    public ThemeManager(FreeMindMain frame) {
        this.frame = frame;
        this.currentTheme = frame.getProperty(THEME_PROPERTY);
        if (this.currentTheme == null) {
            this.currentTheme = LIGHT_THEME;
        }
    }

    public String getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(String theme) {
        this.currentTheme = theme;
        frame.setProperty(THEME_PROPERTY, theme);
        applyTheme();
    }

    public void applyTheme() {
        Properties props = frame.getProperties();
        props.setProperty(FreeMind.RESOURCES_BACKGROUND_COLOR, getThemeColor(FreeMind.RESOURCES_BACKGROUND_COLOR));
        props.setProperty(FreeMind.RESOURCES_NODE_TEXT_COLOR, getThemeColor(FreeMind.RESOURCES_NODE_TEXT_COLOR));
        props.setProperty(FreeMind.RESOURCES_SELECTED_NODE_COLOR, getThemeColor(FreeMind.RESOURCES_SELECTED_NODE_COLOR));
        props.setProperty(FreeMind.RESOURCES_SELECTED_NODE_RECTANGLE_COLOR, getThemeColor(FreeMind.RESOURCES_SELECTED_NODE_RECTANGLE_COLOR));
        props.setProperty(FreeMind.RESOURCES_EDGE_COLOR, getThemeColor(FreeMind.RESOURCES_EDGE_COLOR));
        props.setProperty(FreeMind.RESOURCES_CLOUD_COLOR, getThemeColor(FreeMind.RESOURCES_CLOUD_COLOR));
        props.setProperty(FreeMind.RESOURCES_LINK_COLOR, getThemeColor(FreeMind.RESOURCES_LINK_COLOR));
        ((FreeMind) frame).updateLookAndFeel();
    }

    private String getThemeColor(String property) {
        String themedProperty = currentTheme + "." + property;
        String color = frame.getProperty(themedProperty);
        if (color == null) {
            color = ((FreeMind) frame).getDefaultProperty(property);
        }
        return color;
    }

    public static String[] getThemeNames() {
        return new String[] { LIGHT_THEME, DARK_THEME };
    }
}
