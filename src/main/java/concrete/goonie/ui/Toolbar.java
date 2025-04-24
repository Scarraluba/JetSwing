package concrete.goonie.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;

public class Toolbar extends JToolBar {
    private JButton backButton;
    private JLabel titleLabel;
    private JButton menuButton;

    public Toolbar() {
        setFloatable(false);
        setLayout(new BorderLayout());

        // Back button (initially hidden)
        backButton = new JButton("←");
        backButton.setVisible(false);
        add(backButton, BorderLayout.WEST);
        
        // Title label
        titleLabel = new JLabel("", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.CENTER);
        
        // Menu button
        menuButton = new JButton("☰");
        add(menuButton, BorderLayout.EAST);
    }
    
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
    
    public void showBackButton(boolean show) {
        backButton.setVisible(show);
    }
    
    public void setBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
    
    public void setMenuButtonListener(ActionListener listener) {
        menuButton.addActionListener(listener);
    }
    
    public void addCustomButton(JButton button, String position) {
        if ("left".equalsIgnoreCase(position)) {
            add(button, BorderLayout.WEST);
        } else {
            add(button, BorderLayout.EAST);
        }
    }
    
    public void setBackgroundColor(Color color) {
        setBackground(color);
        backButton.setBackground(color);
        menuButton.setBackground(color);
    }
    
    public void setForegroundColor(Color color) {
        setForeground(color);
        backButton.setForeground(color);
        menuButton.setForeground(color);
        titleLabel.setForeground(color);
    }

    @Override
    public Border getBorder() {
        return null;
    }
}