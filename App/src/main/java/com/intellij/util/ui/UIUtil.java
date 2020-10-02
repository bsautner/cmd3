
package com.intellij.util.ui;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;


public class UIUtil extends DrawUtil {


    private static final StyleSheet DEFAULT_HTML_KIT_CSS;

    static {

        // save the default JRE CSS and ..
        HTMLEditorKit kit = new HTMLEditorKit();
        DEFAULT_HTML_KIT_CSS = kit.getStyleSheet();
        // .. erase global ref to this CSS so no one can alter it
        kit.setStyleSheet(null);
    }


    public static void applyStyle(@NotNull ComponentStyle componentStyle, @NotNull Component comp) {
        if (!(comp instanceof JComponent)) return;

        JComponent c = (JComponent) comp;

        if (isUnderAquaBasedLookAndFeel()) {
            c.putClientProperty("JComponent.sizeVariant", StringUtil.toLowerCase(componentStyle.name()));
        }
        FontSize fontSize = componentStyle == ComponentStyle.MINI
                ? FontSize.MINI
                : componentStyle == ComponentStyle.SMALL
                ? FontSize.SMALL
                : FontSize.NORMAL;
        c.setFont(getFont(fontSize, c.getFont()));
        Container p = c.getParent();
        if (p != null) {
            SwingUtilities.updateComponentTreeUI(p);
        }
    }


    public enum FontSize {NORMAL, SMALL, MINI}

    public enum ComponentStyle {SMALL, MINI}




    @NonNls
    public static final String TABLE_FOCUS_CELL_BACKGROUND_PROPERTY = "Table.focusCellBackground";

    private static final Color BORDER_COLOR = Color.LIGHT_GRAY;

    private static volatile Pair<String, Integer> ourSystemFontData;

    public static final float DEF_SYSTEM_FONT_SIZE = 12f; // TODO: consider 12 * 1.33 to compensate JDK's 72dpi font scale

    @NonNls
    private static final String ROOT_PANE = "JRootPane.future";

    private UIUtil() {
    }

    /**
     * @param component a Swing component that may hold a client property value
     * @param key       the client property key that specifies a return type
     * @return the property value from the specified component or {@code null}
     */
    public static Object getClientProperty(Object component, @NotNull Object key) {
        return component instanceof JComponent ? ((JComponent) component).getClientProperty(key) : null;
    }

    public static <T> void putClientProperty(@NotNull JComponent component, @NotNull Key<T> key, T value) {
        component.putClientProperty(key, value);
    }


    public static void setActionNameAndMnemonic(@NotNull String text, @NotNull Action action) {
        assignMnemonic(text, action);

        text = text.replaceAll("&", "");
        action.putValue(Action.NAME, text);
    }

    public static void assignMnemonic(@NotNull String text, @NotNull Action action) {
        int mnemoPos = text.indexOf('&');
        if (mnemoPos >= 0 && mnemoPos < text.length() - 2) {
            String mnemoChar = text.substring(mnemoPos + 1, mnemoPos + 2).trim();
            if (mnemoChar.length() == 1) {
                action.putValue(Action.MNEMONIC_KEY, Integer.valueOf(mnemoChar.charAt(0)));
            }
        }
    }


    public static Font getLabelFont(@NotNull FontSize size) {
        return getFont(size, null);
    }

    @NotNull
    public static Font getFont(@NotNull FontSize size, @Nullable Font base) {
        if (base == null) base = getLabelFont();

        return base.deriveFont(getFontSize(size));
    }

    public static float getFontSize(FontSize size) {
        int defSize = getLabelFont().getSize();
        switch (size) {
            case SMALL:
                return Math.max(defSize - JBUI.scale(2f), JBUI.scale(11f));
            case MINI:
                return Math.max(defSize - JBUI.scale(4f), JBUI.scale(9f));
            default:
                return defSize;
        }
    }

    public static Font getLabelFont() {
        return UIManager.getFont("Label.font");
    }

    public static Color getLabelDisabledForeground() {
        final Color color = UIManager.getColor("Label.disabledForeground");
        if (color != null) return color;
        return UIManager.getColor("Label.disabledText");
    }


    public static Color getTableHeaderBackground() {
        return UIManager.getColor("TableHeader.background");
    }

    public static Color getTreeTextBackground() {
        return UIManager.getColor("Tree.textBackground");
    }

    public static Color getListSelectionForeground() {
        final Color color = UIManager.getColor("List.selectionForeground");
        if (color == null) {
            return UIManager.getColor("List[Selected].textForeground");  // Nimbus
        }
        return color;
    }

    public static Color getTableSelectionBackground() {
        if (isUnderNimbusLookAndFeel()) {
            Color color = UIManager.getColor("Table[Enabled+Selected].textBackground");
            if (color != null) return color;
            color = UIManager.getColor("nimbusSelectionBackground");
            if (color != null) return color;
        }
        return UIManager.getColor("Table.selectionBackground");
    }

    public static Color getInactiveTextColor() {
        return UIManager.getColor("textInactiveText");
    }


    /**
     * @deprecated use com.intellij.util.ui.UIUtil#getTextFieldBackground()
     */
    public static Color getActiveTextFieldBackgroundColor() {
        return getTextFieldBackground();
    }

    public static Color getInactiveTextFieldBackgroundColor() {
        return UIManager.getColor("TextField.inactiveBackground");
    }

    public static Font getTreeFont() {
        return UIManager.getFont("Tree.font");
    }

    public static Font getListFont() {
        return UIManager.getFont("List.font");
    }

    /**
     * @deprecated use com.intellij.util.ui.UIUtil#getInactiveTextColor()
     */
    public static Color getTextInactiveTextColor() {
        return getInactiveTextColor();
    }

    public static void installPopupMenuBorder(final JComponent contentPane) {
        LookAndFeel.installBorder(contentPane, "PopupMenu.border");
    }

    public static int getTreeLeftChildIndent() {
        return UIManager.getInt("Tree.leftChildIndent");
    }

    public static Color getComboBoxDisabledForeground() {
        return UIManager.getColor("ComboBox.disabledForeground");
    }

    public static Color getComboBoxDisabledBackground() {
        return UIManager.getColor("ComboBox.disabledBackground");
    }

    public static Color getButtonSelectColor() {
        return UIManager.getColor("Button.select");
    }

    public static Color getTableSelectionForeground() {
        if (isUnderNimbusLookAndFeel()) {
            return UIManager.getColor("Table[Enabled+Selected].textForeground");
        }
        return UIManager.getColor("Table.selectionForeground");
    }


    public static Color getListBackground() {
        if (isUnderNimbusLookAndFeel()) {
            final Color color = UIManager.getColor("List.background");
            //noinspection UseJBColor
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }
        // Under GTK+ L&F "Table.background" often has main panel color, which looks ugly
        return isUnderGTKLookAndFeel() ? getTreeTextBackground() : UIManager.getColor("List.background");
    }

    public static Color getEditorPaneBackground() {
        return UIManager.getColor("EditorPane.background");
    }

    public static Color getTableFocusCellBackground() {
        return UIManager.getColor(TABLE_FOCUS_CELL_BACKGROUND_PROPERTY);
    }

    public static Color getListSelectionBackground() {
        if (isUnderNimbusLookAndFeel()) {
            return UIManager.getColor("List[Selected].textBackground");  // Nimbus
        }
        return UIManager.getColor("List.selectionBackground");
    }

    public static Color getTextFieldForeground() {
        return UIManager.getColor("TextField.foreground");
    }

    public static Color getTextFieldBackground() {
        return isUnderGTKLookAndFeel() ? UIManager.getColor("EditorPane.background") : UIManager.getColor("TextField.background");
    }

    public static Font getToolTipFont() {
        return UIManager.getFont("ToolTip.font");
    }

    public static Color getTabbedPaneBackground() {
        return UIManager.getColor("TabbedPane.background");
    }

    public static Color getLabelTextForeground() {
        return UIManager.getColor("Label.textForeground");
    }

    public static Color getControlColor() {
        return UIManager.getColor("control");
    }

    public static Font getMenuFont() {
        return UIManager.getFont("Menu.font");
    }

    public static Color getSeparatorForeground() {
        return UIManager.getColor("Separator.foreground");
    }

    public static Color getSeparatorBackground() {
        return UIManager.getColor("Separator.background");
    }

    public static Color getSeparatorColorUnderNimbus() {
        return UIManager.getColor("nimbusBlueGrey");
    }

    public static Color getTableFocusCellForeground() {
        return UIManager.getColor("Table.focusCellForeground");
    }

    /**
     * @deprecated use com.intellij.util.ui.UIUtil#getPanelBackground() instead
     */
    public static Color getPanelBackgound() {
        return getPanelBackground();
    }

    public static Border getButtonBorder() {
        return UIManager.getBorder("Button.border");
    }

    public static Border getTableHeaderCellBorder() {
        return UIManager.getBorder("TableHeader.cellBorder");
    }
    public static Color getOptionPaneBackground() {
        return UIManager.getColor("OptionPane.background");
    }


    @SuppressWarnings({"HardCodedStringLiteral"})
    public static boolean isUnderNimbusLookAndFeel() {
        return UIManager.getLookAndFeel().getName().contains("Nimbus");
    }

    @SuppressWarnings({"HardCodedStringLiteral"})
    public static boolean isUnderAquaLookAndFeel() {
        return SystemInfo.isMac && UIManager.getLookAndFeel().getName().contains("Mac OS X");
    }



    @SuppressWarnings({"HardCodedStringLiteral"})
    public static boolean isUnderAquaBasedLookAndFeel() {
        return SystemInfo.isMac && (isUnderAquaLookAndFeel() || isUnderDarcula() || isUnderIntelliJLaF());
    }

    @SuppressWarnings({"HardCodedStringLiteral"})
    public static boolean isUnderGTKLookAndFeel() {
        return SystemInfo.isXWindow && UIManager.getLookAndFeel().getName().contains("GTK");
    }

    public static String getGtkThemeName() {
        final LookAndFeel laf = UIManager.getLookAndFeel();
        if (laf != null && "GTKLookAndFeel".equals(laf.getClass().getSimpleName())) {
            try {
                final Method method = laf.getClass().getDeclaredMethod("getGtkThemeName");
                method.setAccessible(true);
                final Object theme = method.invoke(laf);
                if (theme != null) {
                    return theme.toString();
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }


    public static Color shade(final Color c, final double factor, final double alphaFactor) {
        assert factor >= 0 : factor;
        //noinspection UseJBColor
        return new Color(
                Math.min((int) Math.round(c.getRed() * factor), 255),
                Math.min((int) Math.round(c.getGreen() * factor), 255),
                Math.min((int) Math.round(c.getBlue() * factor), 255),
                Math.min((int) Math.round(c.getAlpha() * alphaFactor), 255)
        );
    }

    public static Color mix(final Color c1, final Color c2, final double factor) {
        assert 0 <= factor && factor <= 1.0 : factor;
        final double backFactor = 1.0 - factor;
        //noinspection UseJBColor
        return new Color(
                Math.min((int) Math.round(c1.getRed() * backFactor + c2.getRed() * factor), 255),
                Math.min((int) Math.round(c1.getGreen() * backFactor + c2.getGreen() * factor), 255),
                Math.min((int) Math.round(c1.getBlue() * backFactor + c2.getBlue() * factor), 255)
        );
    }

    public static boolean isUnderNativeMacLookAndFeel() {
        return isUnderAquaLookAndFeel() || isUnderDarcula();
    }



    public static boolean isToUseDottedCellBorder() {
        return !isUnderNativeMacLookAndFeel();
    }



    public static String displayPropertiesToCSS(Font font, Color fg) {
        @NonNls StringBuilder rule = new StringBuilder("body {");
        if (font != null) {
            rule.append(" font-family: ");
            rule.append(font.getFamily());
            rule.append(" ; ");
            rule.append(" font-size: ");
            rule.append(font.getSize());
            rule.append("pt ;");
            if (font.isBold()) {
                rule.append(" font-weight: 700 ; ");
            }
            if (font.isItalic()) {
                rule.append(" font-style: italic ; ");
            }
        }
        if (fg != null) {
            rule.append(" color: #");
            appendColor(fg, rule);
            rule.append(" ; ");
        }
        rule.append(" }");
        return rule.toString();
    }

    public static void appendColor(final Color color, final StringBuilder sb) {
        if (color.getRed() < 16) sb.append('0');
        sb.append(Integer.toHexString(color.getRed()));
        if (color.getGreen() < 16) sb.append('0');
        sb.append(Integer.toHexString(color.getGreen()));
        if (color.getBlue() < 16) sb.append('0');
        sb.append(Integer.toHexString(color.getBlue()));
    }

    /**
     * This method is intended to use when user settings are not accessible yet.
     * Use it to set up default RenderingHints.
     *
     * @param g
     */
    public static void applyRenderingHints(final Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Toolkit tk = Toolkit.getDefaultToolkit();
        //noinspection HardCodedStringLiteral
        Map map = (Map) tk.getDesktopProperty("awt.font.desktophints");
        if (map != null) {
            g2d.addRenderingHints(map);
        }
    }

    @Deprecated
    public static <T extends Component> T findParentByClass(@NotNull Component c, Class<T> cls) {
        return getParentOfType(cls, c);
    }

    public static boolean isWinLafOnVista() {
        return SystemInfo.isWinVistaOrNewer && "Windows".equals(UIManager.getLookAndFeel().getName());
    }

    public static boolean isStandardMenuLAF() {
        return isWinLafOnVista() ||
                isUnderNimbusLookAndFeel() ||
                isUnderGTKLookAndFeel();
    }

    /**
     * @use JBColor.border()
     * @deprecated
     */
    public static Color getBorderColor() {
        return  BORDER_COLOR;
    }

    /**
     * @deprecated use getBorderColor instead
     */
    public static Color getBorderInactiveColor() {
        return getBorderColor();
    }

    /**
     * @deprecated use getBorderColor instead
     */
    public static Color getBorderActiveColor() {
        return getBorderColor();
    }

    /**
     * @deprecated use getBorderColor instead
     */
    public static Color getBorderSeparatorColor() {
        return getBorderColor();
    }

    public static HTMLEditorKit getHTMLEditorKit(boolean noGapsBetweenParagraphs) {
        Font font = getLabelFont();
        @NonNls String family = !SystemInfo.isWindows && font != null ? font.getFamily() : "Tahoma";
        int size = font != null ? font.getSize() : JBUI.scale(11);

        String customCss = String.format("body, div, p { font-family: %s; font-size: %s; }", family, size);
        if (noGapsBetweenParagraphs) {
            customCss += " p { margin-top: 0; }";
        }

        final StyleSheet style = new StyleSheet();
        style.addStyleSheet(isUnderDarcula() ? (StyleSheet) UIManager.getDefaults().get("StyledEditorKit.JBDefaultStyle") : DEFAULT_HTML_KIT_CSS);
        style.addRule(customCss);

        return new HTMLEditorKit() {
            @Override
            public StyleSheet getStyleSheet() {
                return style;
            }
        };
    }

    public static Point getCenterPoint(Rectangle container, Dimension child) {
        return new Point(
                container.x + (container.width - child.width) / 2,
                container.y + (container.height - child.height) / 2
        );
    }

    public static void initSystemFontData() {
        if (ourSystemFontData != null) return;

        // With JB Linux JDK the label font comes properly scaled based on Xft.dpi settings.
        Font font = getLabelFont();

        Float forcedScale = null;

        if (SystemInfo.isLinux && !SystemInfo.isJetbrainsJvm) {
            // With Oracle JDK: derive scale from X server DPI
            float scale = getScreenScale();
            if (scale > 1f) {
                forcedScale = Float.valueOf(scale);
            }
            // Or otherwise leave the detected font. It's undetermined if it's scaled or not.
            // If it is (likely with GTK DE), then the UI scale will be derived from it,
            // if it's not, then IDEA will start unscaled. This lets the users of GTK DEs
            // not to bother about X server DPI settings. Users of other DEs (like KDE)
            // will have to set X server DPI to meet their display.
        } else if (SystemInfo.isWindows) {
            //noinspection HardCodedStringLiteral
            Font winFont = (Font) Toolkit.getDefaultToolkit().getDesktopProperty("win.messagebox.font");
            if (winFont != null) {
                font = winFont; // comes scaled
            }
        }
        if (forcedScale != null) {
            // With forced scale, we derive font from a hard-coded value as we cannot be sure
            // the system font comes unscaled.
            font = font.deriveFont(DEF_SYSTEM_FONT_SIZE * forcedScale.floatValue());
        }
        ourSystemFontData = Pair.create(font.getName(), font.getSize());
    }

    @Nullable
    public static Pair<String, Integer> getSystemFontData() {
        return ourSystemFontData;
    }

    private static float getScreenScale() {
        int dpi = 96;
        try {
            dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        } catch (HeadlessException e) {
        }
        float scale = 1f;
        if (dpi < 120) scale = 1f;
        else if (dpi < 144) scale = 1.25f;
        else if (dpi < 168) scale = 1.5f;
        else if (dpi < 192) scale = 1.75f;
        else scale = 2f;

        return scale;
    }

    @SuppressWarnings("deprecation")
    public static void setComboBoxEditorBounds(int x, int y, int width, int height, JComponent editor) {
        if (SystemInfo.isMac && isUnderAquaLookAndFeel()) {
            // fix for too wide combobox editor, see AquaComboBoxUI.layoutContainer:
            // it adds +4 pixels to editor width. WTF?!
            editor.reshape(x, y, width - 4, height - 1);
        } else {
            editor.reshape(x, y, width, height);
        }
    }

    /**
     * Searches above in the component hierarchy starting from the specified component.
     * Note that the initial component is also checked.
     *
     * @param type      expected class
     * @param component initial component
     * @return a component of the specified type, or {@code null} if the search is failed
     * @see SwingUtilities#getAncestorOfClass
     */
    @Nullable
    public static <T> T getParentOfType(@NotNull Class<? extends T> type, Component component) {
        while (component != null) {
            if (type.isInstance(component)) {
                //noinspection unchecked
                return (T) component;
            }
            component = component.getParent();
        }
        return null;
    }



    @Nullable
    public static JRootPane getRootPane(Component c) {
        JRootPane root = getParentOfType(JRootPane.class, c);
        if (root != null) return root;
        Component eachParent = c;
        while (eachParent != null) {
            if (eachParent instanceof JComponent) {
                @SuppressWarnings({"unchecked"}) WeakReference<JRootPane> pane =
                        (WeakReference<JRootPane>) ((JComponent) eachParent).getClientProperty(ROOT_PANE);
                if (pane != null) return pane.get();
            }
            eachParent = eachParent.getParent();
        }

        return null;
    }

    public static Color getColorAt(final Icon icon, final int x, final int y) {
        if (0 <= x && x < icon.getIconWidth() && 0 <= y && y < icon.getIconHeight()) {
            final BufferedImage image = createImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
            icon.paintIcon(null, image.getGraphics(), 0, 0);

            final int[] pixels = new int[1];
            final PixelGrabber pixelGrabber = new PixelGrabber(image, x, y, 1, 1, pixels, 0, 1);
            try {
                pixelGrabber.grabPixels();
                return new Color(pixels[0]);
            } catch (InterruptedException ignored) {
            }
        }

        return null;
    }


    public static void setAutoRequestFocus(final Window onWindow, final boolean set) {
        if (SystemInfo.isMac) return;
        if (SystemInfo.isJavaVersionAtLeast("1.7")) {
            try {
                Method setAutoRequestFocusMethod = onWindow.getClass().getMethod("setAutoRequestFocus", boolean.class);
                setAutoRequestFocusMethod.invoke(onWindow, set);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {

            }
        }
    }

    //May have no usages but it's useful in runtime (Debugger "watches", some logging etc.)
    public static String getDebugText(Component c) {
        StringBuilder builder = new StringBuilder();
        getAllTextsRecursivelyImpl(c, builder);
        return builder.toString();
    }

    private static void getAllTextsRecursivelyImpl(Component component, StringBuilder builder) {
        String candidate = "";
        int limit = builder.length() > 60 ? 20 : 40;
        if (component instanceof JLabel) candidate = ((JLabel) component).getText();
        if (component instanceof JTextComponent) candidate = ((JTextComponent) component).getText();
        if (component instanceof AbstractButton) candidate = ((AbstractButton) component).getText();
        if (StringUtil.isNotEmpty(candidate)) {
            builder.append(candidate.length() > limit ? (candidate.substring(0, limit - 3) + "...") : candidate).append('|');
        }
        if (component instanceof Container) {
            Component[] components = ((Container) component).getComponents();
            for (Component child : components) {
                getAllTextsRecursivelyImpl(child, builder);
            }
        }
    }


}
