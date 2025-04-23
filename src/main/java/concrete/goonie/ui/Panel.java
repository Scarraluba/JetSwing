package concrete.goonie.ui;


import concrete.goonie.core.NavigationController;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Panel extends JPanel {

    private List<GradientSettings> gradients = new ArrayList<>();
    private boolean gradientsEnabled = false;

    public Panel(LayoutManager layoutManager) {
        super(layoutManager);
        gradients.add(new GradientSettings(new Color(0, 153, 204, 108), new Color(10, 10, 10, 108), new float[]{0.0f, 0.8f}, 0.15f, 2.5f));
        gradients.add(new GradientSettings(new Color(255, 255, 255, 98), new Color(0, 0, 0, 98), new float[]{0.0f, 0.8f}, 0.41f, 3f));
        gradients.add(new GradientSettings(new Color(204, 0, 0, 111), new Color(10, 10, 10, 111), new float[]{0.0f, 0.8f}, 0.78f, 2.5f));
    }

    // Gradient background methods
    public void enableGradientBackground(boolean enabled) {
        this.gradientsEnabled = enabled;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gradientsEnabled && !gradients.isEmpty()) {
            Graphics2D g2d = (Graphics2D) g;
            paintGradientBackground(g2d);
        }

    }

    private void paintGradientBackground(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        for (GradientSettings gs : gradients) {
            Point2D center = new Point2D.Float(getWidth() * gs.position, getHeight() / 2f);
            int radius = (int) (Math.max(getWidth(), getHeight()) / gs.size);

            RadialGradientPaint gradient = new RadialGradientPaint(center, radius, gs.distribution,
                    new Color[]{gs.centerColor, gs.edgeColor});
            g2d.setPaint(gradient);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, gs.centerColor.getAlpha() / 255f));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        g2d.setComposite(AlphaComposite.SrcOver);
    }

    class GradientSettings {
        public Color centerColor;
        public Color edgeColor;
        public float[] distribution;
        public float position;
        public float size = 5;

        public GradientSettings(Color center, Color edge, float[] dist, float pos, float size) {
            this.centerColor = center;
            this.edgeColor = edge;
            this.distribution = dist;
            this.position = pos;
            this.size = size;
        }

        @Override
        public String toString() {
            return "GradientSettings{\n" +
                    "centerColor=" + centerColor.toString() + ", alpha=" + centerColor.getAlpha() +
                    "\n, edgeColor=" + edgeColor.toString() + ", alpha=" + edgeColor.getAlpha() +
                    "\n, distribution=" + Arrays.toString(distribution) +
                    "\n, position=" + position +
                    "\n, size=" + size +
                    '}';
        }
    }
}