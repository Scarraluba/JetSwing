package concrete.goonie.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


// Helper methods to add to JComponent
class ComponentUtilities {
    public static void setAlpha(JComponent component, float alpha) {
        component.putClientProperty("alpha", alpha);
        if (component.isShowing()) {
            SwingUtilities.invokeLater(component::repaint);
        }
    }

    public static void setScale(JComponent component, float scale) {
        component.putClientProperty("scale", scale);
        if (component.isShowing()) {
            SwingUtilities.invokeLater(component::repaint);
        }
    }
}