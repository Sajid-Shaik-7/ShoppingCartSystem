
package utils;

import java.awt.*;

/**
 * Custom FlowLayout that automatically wraps components
 * into multiple rows when the container width is exceeded.
 *
 * Used in ShopEase to arrange product cards responsively.
 *
 * Concepts:
 * 1. Inheritance
 * 2. Method Overriding
 * 3. Layout Management
 * 4. Synchronization
 */
public class WrapLayout extends FlowLayout {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a wrapping layout.
     *
     * @param align component alignment
     * @param hgap horizontal gap
     * @param vgap vertical gap
     */
    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    /**
     * Calculates the preferred size of the container.
     */
    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    /**
     * Calculates the minimum size of the container.
     */
    @Override
    public Dimension minimumLayoutSize(Container target) {

        Dimension minimum = layoutSize(target, false);

        minimum.width = Math.max(
            0,
            minimum.width - getHgap() - 1
        );

        return minimum;
    }

    /**
     * Calculates container dimensions while wrapping components
     * into additional rows when necessary.
     *
     * @param target container being laid out
     * @param preferred true for preferred size, false for minimum size
     * @return calculated container dimensions
     */
    private Dimension layoutSize(
            Container target,
            boolean preferred) {

        synchronized (target.getTreeLock()) {

            int targetWidth = target.getSize().width;

            // If the container has not been displayed yet,
            // calculate its preferred width without wrapping.
            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();

            Insets insets = target.getInsets();

            int maxWidth = targetWidth
                - insets.left
                - insets.right
                - (hgap * 2);

            Dimension dimension = new Dimension(0, 0);

            int rowWidth = 0;
            int rowHeight = 0;

            int componentCount = target.getComponentCount();

            for (int i = 0; i < componentCount; i++) {

                Component component = target.getComponent(i);

                if (!component.isVisible()) {
                    continue;
                }

                Dimension componentSize = preferred
                    ? component.getPreferredSize()
                    : component.getMinimumSize();

                // Start a new row if the component does not fit.
                if (rowWidth > 0
                        && rowWidth + hgap + componentSize.width > maxWidth) {

                    addRow(
                        dimension,
                        rowWidth,
                        rowHeight
                    );

                    rowWidth = 0;
                    rowHeight = 0;
                }

                // Add horizontal spacing between components.
                if (rowWidth > 0) {
                    rowWidth += hgap;
                }

                rowWidth += componentSize.width;

                rowHeight = Math.max(
                    rowHeight,
                    componentSize.height
                );
            }

            // Add the final row.
            addRow(
                dimension,
                rowWidth,
                rowHeight
            );

            // Include container borders and layout gaps.
            dimension.width +=
                insets.left + insets.right + (hgap * 2);

            dimension.height +=
                insets.top + insets.bottom + (vgap * 2);

            return dimension;
        }
    }

    /**
     * Adds the dimensions of one row to the total layout size.
     */
    private void addRow(
            Dimension dimension,
            int rowWidth,
            int rowHeight) {

        dimension.width = Math.max(
            dimension.width,
            rowWidth
        );

        if (dimension.height > 0) {
            dimension.height += getVgap();
        }

        dimension.height += rowHeight;
    }
}
