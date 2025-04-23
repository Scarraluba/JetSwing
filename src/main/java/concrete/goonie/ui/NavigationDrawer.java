package concrete.goonie.ui;

import concrete.goonie.core.NavigationController;

import javax.swing.*;
import java.awt.*;

public class NavigationDrawer {
    private JPanel drawerPanel;
    
    public NavigationDrawer(NavigationController controller) {
        drawerPanel = new JPanel();
        drawerPanel.setLayout(new BoxLayout(drawerPanel, BoxLayout.Y_AXIS));
        drawerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // User header
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel avatar = new JLabel("👤", SwingConstants.CENTER);
        avatar.setFont(new Font("SansSerif", Font.PLAIN, 48));
        
        JLabel userName = new JLabel("John Doe", SwingConstants.CENTER);
        userName.setFont(userName.getFont().deriveFont(Font.BOLD));
        
        userPanel.add(avatar, BorderLayout.CENTER);
        userPanel.add(userName, BorderLayout.SOUTH);
        drawerPanel.add(userPanel);
        
        // Navigation items
        addDrawerItem("Home", "home", controller);
        addDrawerItem("Profile", "profile", controller);
        addDrawerItem("Notifications", "notifications", controller);
        addDrawerItem("Settings", "settings", controller);
        
        drawerPanel.add(Box.createVerticalStrut(20));
        
        // Footer items
        addDrawerItem("Help & Feedback", "help", controller);
        addDrawerItem("Logout", null, controller);
    }
    
    private void addDrawerItem(String text, String destination, NavigationController controller) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        if (destination != null) {
            button.addActionListener(e -> {
                controller.navigateTo(destination, false);
                controller.showDrawer(false);
            });
        } else {
            button.addActionListener(e -> {
                // Handle logout
                System.out.println("Logging out...");
            });
        }
        
        drawerPanel.add(button);
    }
    
    public JComponent getView() {
        return drawerPanel;
    }
}