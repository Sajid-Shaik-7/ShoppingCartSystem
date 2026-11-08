import java.awt.*;

/**
 * WrapLayout - FlowLayout that wraps components to the next row
 * when the container is too narrow.
 * CONCEPT: Inheritance - WrapLayout extends FlowLayout
 */
public class WrapLayout extends FlowLayout {

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    // Returns the preferred size that wraps components across multiple rows.
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= getHgap() + 1;
        return minimum;
    }

    // Computes the layout size, wrapping when width is exceeded.
    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;
            if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);

            Dimension dim    = new Dimension(0, 0);
            int rowWidth = 0, rowHeight = 0;

            int nmembers = target.getComponentCount();
            int i = 0;
            while (i < nmembers) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                    if (rowWidth + d.width > maxWidth) {
                        addRow(dim, rowWidth, rowHeight);
                        rowWidth = 0;
                        rowHeight = 0;
                    }
                    if (rowWidth != 0) rowWidth += hgap;
                    rowWidth  += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                }
                i = i + 1;
            }
            addRow(dim, rowWidth, rowHeight);

            dim.width   += insets.left + insets.right + hgap * 2;
            dim.height  += insets.top  + insets.bottom + vgap * 2;
            return dim;
        }
    }

    private void addRow(Dimension dim, int rowWidth, int rowHeight) {
        dim.width  = Math.max(dim.width, rowWidth);
        if (dim.height > 0) dim.height += getVgap();
        dim.height += rowHeight;
    }
}
