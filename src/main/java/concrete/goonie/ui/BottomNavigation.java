package concrete.goonie.ui;

import concrete.goonie.core.AnimationType;
import concrete.goonie.core.NavigationController;

import javax.swing.*;
import java.awt.*;

public class BottomNavigation {
    private JPanel navPanel;

    public BottomNavigation(NavigationController controller) {
        navPanel = new JPanel(new GridLayout(1, 4));
        navPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        addNavButton("Home", "🏠", "home", controller);
        addNavButton("Search", "🔍", "search", controller);
        addNavButton("Profile", "👤", "profile", controller);
        addNavButton("More", "⋮", "settings", controller);
    }

    private void addNavButton(String text, String icon, String destination, NavigationController controller) {
        JButton button = new JButton("<html><center>" + icon + "<br>" + text + "</center></html>");
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.addActionListener(e -> {
            // For bottom nav items, don't add to back stack
            controller.navigateTo(destination, false);
        });
        navPanel.add(button);
    }
    
    public JComponent getView() {
        return navPanel;
    }
}