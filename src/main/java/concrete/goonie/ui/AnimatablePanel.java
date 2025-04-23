package concrete.goonie.ui;

import javax.swing.*;
import java.awt.*;

public class AnimatablePanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Handle alpha
        Float alpha = (Float) getClientProperty("alpha");
        if (alpha != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }

        // Handle scale
        Float scale = (Float) getClientProperty("scale");
        if (scale != null) {
            int width = getWidth();
            int height = getHeight();
            int scaledWidth = (int) (width * scale);
            int scaledHeight = (int) (height * scale);
            int x = (width - scaledWidth) / 2;
            int y = (height - scaledHeight) / 2;
            g2.scale(scale, scale);
            g2.translate(x/scale, y/scale);
        }

        super.paintComponent(g2);
        g2.dispose();
    }
}