import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

// CONCEPT: Static methods - UITheme acts as a utility class with only static members
public class UITheme {

    // ── Colour Palette ──────────────────────────────────────────────────────────
    public static final Color BG          = new Color(0xF5F6FA);   // page background
    public static final Color CARD        = Color.WHITE;            // card surface
    public static final Color PRIMARY     = new Color(0x1A73E8);   // blue primary
    public static final Color PRIMARY_DK  = new Color(0x1557B0);   // darker blue (hover)
    public static final Color ACCENT      = new Color(0xFF6B00);   // orange accent
    public static final Color SUCCESS     = new Color(0x2E7D32);   // green
    public static final Color WARN        = new Color(0xE65100);   // orange-red
    public static final Color TEXT_DARK   = new Color(0x212121);   // near-black
    public static final Color TEXT_MED    = new Color(0x555555);   // medium grey
    public static final Color TEXT_MUTED  = new Color(0x999999);   // light grey
    public static final Color BORDER      = new Color(0xE0E0E0);   // card border
    public static final Color SIDEBAR_BG  = new Color(0xFFFFFF);   // sidebar white
    public static final Color NAV_BG      = Color.WHITE;           // navbar white
    public static final Color BADGE_BG    = new Color(0xFFEBEE);   // cart badge bg

    // ── Fonts ───────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FONT_SUBHEAD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_BODY    = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_EMOJI   = new Font("Segoe UI Emoji", Font.PLAIN, 36);
    public static final Font FONT_MONO    = new Font("Monospaced",  Font.PLAIN, 12);

    // ── Button Factories ────────────────────────────────────────────────────────

    // Primary solid blue button
    public static JButton makePrimaryBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFont(FONT_SUBHEAD);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // Secondary ghost-style button (white bg, blue border)
    public static JButton makeSecondaryBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(Color.WHITE);
        b.setForeground(PRIMARY);
        b.setFont(FONT_BODY);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createLineBorder(PRIMARY, 1));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // Small accent (orange) button
    public static JButton makeAccentBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
        b.setFont(FONT_SMALL);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // Success (green) button
    public static JButton makeSuccessBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(SUCCESS);
        b.setForeground(Color.WHITE);
        b.setFont(FONT_SUBHEAD);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // Nav-rail button: selected state shown with blue left bar
    public static JButton makeNavBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(SIDEBAR_BG);
        b.setForeground(TEXT_DARK);
        b.setFont(FONT_BODY);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Panel / Border Helpers ──────────────────────────────────────────────────

    // White card panel with a thin light border
    public static JPanel makeCard() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return p;
    }

    // Compound border: coloured title + padding
    public static Border titledBorder(String title) {
        TitledBorder tb = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER), title,
            TitledBorder.LEFT, TitledBorder.TOP, FONT_SUBHEAD, TEXT_MED);
        return BorderFactory.createCompoundBorder(
            tb, BorderFactory.createEmptyBorder(4, 6, 4, 6));
    }

    // Simple padding border
    public static Border pad(int top, int left, int bottom, int right) {
        return BorderFactory.createEmptyBorder(top, left, bottom, right);
    }

    // ── Label Factories ─────────────────────────────────────────────────────────

    public static JLabel makeLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
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

    // ── JTable Styling ──────────────────────────────────────────────────────────

    public static void styleTable(JTable t) {
        t.setBackground(CARD);
        t.setForeground(TEXT_DARK);
        t.setFont(FONT_BODY);
        t.setRowHeight(32);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.getTableHeader().setBackground(BG);
        t.getTableHeader().setForeground(TEXT_MED);
        t.getTableHeader().setFont(FONT_SUBHEAD);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        t.setSelectionBackground(new Color(0xE3F2FD));
        t.setSelectionForeground(TEXT_DARK);
    }

    // ── JTextField Styling ──────────────────────────────────────────────────────

    public static JTextField makeTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(FONT_BODY);
        tf.setForeground(TEXT_DARK);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return tf;
    }

    public static JPasswordField makePasswordField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setFont(FONT_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return pf;
    }

    // ── Scroll pane ─────────────────────────────────────────────────────────────

    public static JScrollPane makeScroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        sp.getViewport().setBackground(CARD);
        return sp;
    }
}
