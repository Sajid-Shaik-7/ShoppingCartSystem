
package utils;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.*;

/**
 * Centralized UI styling utility for ShopEase.
 *
 * Responsibilities:
 * 1. Application color palette.
 * 2. Font management.
 * 3. Button styling.
 * 4. Panel and border styling.
 * 5. Label styling.
 * 6. Table and input styling.
 *
 * Concepts:
 * 1. Static Methods
 * 2. Encapsulation
 * 3. Code Reusability
 */
public final class UITheme {

    // Prevent object creation.
    private UITheme() {
        throw new AssertionError("Utility class cannot be instantiated.");
    }

    // ================= COLOR PALETTE =================

    public static final Color BG         = new Color(0xF5F6FA);
    public static final Color CARD       = Color.WHITE;
    public static final Color PRIMARY    = new Color(0x1A73E8);
    public static final Color PRIMARY_DK = new Color(0x1557B0);
    public static final Color ACCENT     = new Color(0xFF6B00);
    public static final Color SUCCESS    = new Color(0x2E7D32);
    public static final Color WARN       = new Color(0xE65100);
    public static final Color DANGER     = new Color(0xD32F2F);

    public static final Color TEXT_DARK  = new Color(0x212121);
    public static final Color TEXT_MED   = new Color(0x555555);
    public static final Color TEXT_MUTED = new Color(0x999999);

    public static final Color BORDER     = new Color(0xE0E0E0);
    public static final Color SIDEBAR_BG = Color.WHITE;
    public static final Color NAV_BG     = Color.WHITE;
    public static final Color BADGE_BG   = new Color(0xFFEBEE);

    public static final Color HOVER_BG  = new Color(0xE8F0FE);
    public static final Color SELECT_BG = new Color(0xE3F2FD);

    // ================= FONT STYLES =================

    public static final Font FONT_TITLE =
        new Font("SansSerif", Font.BOLD, 22);

    public static final Font FONT_HEADING =
        new Font("SansSerif", Font.BOLD, 15);

    public static final Font FONT_SUBHEAD =
        new Font("SansSerif", Font.BOLD, 13);

    public static final Font FONT_BODY =
        new Font("SansSerif", Font.PLAIN, 13);

    public static final Font FONT_SMALL =
        new Font("SansSerif", Font.PLAIN, 11);

    public static final Font FONT_EMOJI =
        new Font("Segoe UI Emoji", Font.PLAIN, 36);

    public static final Font FONT_MONO =
        new Font("Monospaced", Font.PLAIN, 12);

    // ================= BUTTON FACTORIES =================

    /**
     * Creates a primary blue button.
     */
    public static JButton makePrimaryBtn(String text) {

        JButton button = new JButton(text);

        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_SUBHEAD);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        addHoverEffect(button, PRIMARY, PRIMARY_DK);

        return button;
    }

    /**
     * Creates a secondary white button.
     */
    public static JButton makeSecondaryBtn(String text) {

        JButton button = new JButton(text);

        button.setBackground(Color.WHITE);
        button.setForeground(PRIMARY);
        button.setFont(FONT_BODY);
        button.setOpaque(true);
        button.setBorder(
            BorderFactory.createLineBorder(PRIMARY, 1)
        );
        button.setFocusPainted(false);
        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        addHoverEffect(button, Color.WHITE, HOVER_BG);

        return button;
    }

    /**
     * Creates an orange accent button.
     */
    public static JButton makeAccentBtn(String text) {

        JButton button = new JButton(text);

        button.setBackground(ACCENT);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_SMALL);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        addHoverEffect(
            button,
            ACCENT,
            new Color(0xE65C00)
        );

        return button;
    }

    /**
     * Creates a green success button.
     */
    public static JButton makeSuccessBtn(String text) {

        JButton button = new JButton(text);

        button.setBackground(SUCCESS);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_SUBHEAD);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        addHoverEffect(
            button,
            SUCCESS,
            new Color(0x1B5E20)
        );

        return button;
    }

    /**
     * Creates a navigation button.
     */
    public static JButton makeNavBtn(String text) {

        JButton button = new JButton(text);

        button.setBackground(SIDEBAR_BG);
        button.setForeground(TEXT_DARK);
        button.setFont(FONT_BODY);
        button.setOpaque(true);
        button.setBorder(
            BorderFactory.createEmptyBorder(10, 14, 10, 10)
        );
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        addHoverEffect(
            button,
            SIDEBAR_BG,
            HOVER_BG
        );

        return button;
    }

    /**
     * Adds a simple background hover effect to buttons.
     */
    private static void addHoverEffect(
            JButton button,
            Color normalColor,
            Color hoverColor) {

        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {

                button.setBackground(normalColor);
            }
        });
    }

    // ================= PANEL AND BORDER HELPERS =================

    /**
     * Creates a white card panel.
     */
    public static JPanel makeCard() {

        JPanel panel = new JPanel();

        panel.setBackground(CARD);
        panel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
        );

        return panel;
    }

    /**
     * Creates a titled border with padding.
     */
    public static Border titledBorder(String title) {

        TitledBorder titledBorder =
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FONT_SUBHEAD,
                TEXT_MED
            );

        return BorderFactory.createCompoundBorder(
            titledBorder,
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        );
    }

    /**
     * Creates an empty padding border.
     */
    public static Border pad(
            int top,
            int left,
            int bottom,
            int right) {

        return BorderFactory.createEmptyBorder(
            top, left, bottom, right
        );
    }

    // ================= LABEL FACTORIES =================

    public static JLabel makeLabel(
            String text,
            Font font,
            Color color) {

        JLabel label = new JLabel(text);

        label.setFont(font);
        label.setForeground(color);

        return label;
    }

    public static JLabel makeTitleLabel(String text) {
        return makeLabel(text, FONT_TITLE, TEXT_DARK);
    }

    public static JLabel makeHeadingLabel(String text) {
        return makeLabel(text, FONT_HEADING, TEXT_DARK);
    }

    public static JLabel makeBodyLabel(String text) {
        return makeLabel(text, FONT_BODY, TEXT_MED);
    }

    public static JLabel makeSmallLabel(String text) {
        return makeLabel(text, FONT_SMALL, TEXT_MUTED);
    }

    /**
     * Creates a colored status label.
     */
    public static JLabel makeStatusLabel(
            String text,
            Color color) {

        JLabel label = makeLabel(text, FONT_SUBHEAD, color);

        label.setBorder(
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        );

        return label;
    }

    // ================= TABLE STYLING =================

    public static void styleTable(JTable table) {

        table.setBackground(CARD);
        table.setForeground(TEXT_DARK);
        table.setFont(FONT_BODY);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        table.setSelectionBackground(SELECT_BG);
        table.setSelectionForeground(TEXT_DARK);

        table.setFillsViewportHeight(true);

        JTableHeaderStyle.apply(table);
    }

    /**
     * Helper for styling table headers.
     */
    private static class JTableHeaderStyle {

        private static void apply(JTable table) {

            if (table.getTableHeader() == null) {
                return;
            }

            table.getTableHeader().setBackground(BG);
            table.getTableHeader().setForeground(TEXT_MED);
            table.getTableHeader().setFont(FONT_SUBHEAD);

            table.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(
                    0, 0, 1, 0, BORDER
                )
            );
        }
    }

    // ================= TEXT FIELD STYLING =================

    public static JTextField makeTextField(int cols) {

        JTextField textField = new JTextField(cols);

        textField.setFont(FONT_BODY);
        textField.setForeground(TEXT_DARK);
        textField.setBackground(Color.WHITE);

        textField.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
            )
        );

        return textField;
    }

    public static JPasswordField makePasswordField(int cols) {

        JPasswordField passwordField =
            new JPasswordField(cols);

        passwordField.setFont(FONT_BODY);
        passwordField.setForeground(TEXT_DARK);
        passwordField.setBackground(Color.WHITE);

        passwordField.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
            )
        );

        return passwordField;
    }

    // ================= SCROLL PANE =================

    public static JScrollPane makeScroll(Component component) {

        JScrollPane scrollPane = new JScrollPane(component);

        scrollPane.setBorder(
            BorderFactory.createLineBorder(BORDER, 1)
        );

        scrollPane.getViewport().setBackground(CARD);

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }
}
