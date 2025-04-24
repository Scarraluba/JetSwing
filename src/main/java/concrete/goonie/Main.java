package concrete.goonie;

import concrete.goonie.core.NavigationEvent;
import concrete.goonie.ui.AppFrame;
import concrete.goonie.ui.BottomNavigation;
import concrete.goonie.ui.Fragment;
import concrete.goonie.ui.NavigationDrawer;

import javax.swing.*;
import java.awt.*;

public class Main  extends AppFrame {
    public Main () {
        super("Full-Stack Navigation Demo");
        //setDefaultAnimation(AnimationType.SLIDE_RIGHT);
        // Create and register all fragments
        registerFragment(new HomeFragment());
        registerFragment(new SearchFragment());
        registerFragment(new ProfileFragment());
        registerFragment(new DetailsFragment());
        registerFragment(new NotificationsFragment());

        // Set up navigation components
         setupBottomNavigation();
        setupNavigationDrawer();
        setupToolbar();

       //  Set initial state
        setRoot("home");
        setToolbarTitle("Home");
        showBottomNav(true);
        showToolbar(true);

        toolbar.setBackgroundColor(new Color(50, 50, 50));
        toolbar.setForegroundColor(Color.WHITE);

        // Set menu button action
        setToolbarMenuAction(e -> showDrawer(!drawer.isVisible()));
    }

    private void setupBottomNavigation() {
        BottomNavigation bottomNav = new BottomNavigation(this);
        this.bottomNav.removeAll(); // Clear existing components
        this.bottomNav.add(bottomNav.getView());
        this.bottomNav.revalidate();
        this.bottomNav.repaint();
    }
    
    private void setupNavigationDrawer() {
        NavigationDrawer drawer = new NavigationDrawer(this);
        this.drawer.add(drawer.getView());
        
        // Add drawer toggle button to toolbar
        JButton menuButton = new JButton("☰");

      //  menuButton.addActionListener(e -> showDrawer(!d ));
        toolbar.add(menuButton, 0); // Add at beginning of toolbar
    }
    
    private void setupToolbar() {
        // Add a back button that's initially hidden
        JButton backButton = new JButton("←");
        backButton.setVisible(false);
        backButton.addActionListener(e -> navigateBack());
        toolbar.add(backButton, 0);

        // Listen for navigation events to show/hide back button
        setNavigationListener(event -> {
            // Only show back button when we have somewhere to go back to
            boolean showBack = backStack.size() > 1;
            backButton.setVisible(showBack);

            // Also update toolbar title
            if (event == NavigationEvent.NAVIGATED_TO || event == NavigationEvent.NAVIGATED_BACK) {
                String current = backStack.peek();
                Fragment fragment = fragments.get(current);
                if (fragment instanceof HomeFragment) {
                    setToolbarTitle("Home");
                } else if (fragment instanceof ProfileFragment) {
                    setToolbarTitle("Profile");
                }
                // ... other fragment titles ...
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }

    class HomeFragment extends Fragment {
        public HomeFragment() {
          setOpaque(true);
          setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Header
            JLabel header = new JLabel("Welcome to MyApp", SwingConstants.CENTER);
            header.setFont(new Font("SansSerif", Font.BOLD, 24));
            add(header, BorderLayout.NORTH);

            // Content cards
            JPanel cardsPanel = new JPanel(new GridLayout(0, 2, 10, 10));

            addCard(cardsPanel, "Profile", "👤", "profile");
            addCard(cardsPanel, "Search", "🔍", "search");
            addCard(cardsPanel, "Settings", "⚙️", "settings");
            addCard(cardsPanel, "Notifications", "🔔", "notifications");

            add(new JScrollPane(cardsPanel), BorderLayout.CENTER);
        }

        private void addCard(JPanel parent, String title, String icon, String destination) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
            iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 36));

            JButton button = new JButton(title);
            button.addActionListener(e -> navigationController.navigateTo(destination));

            card.add(iconLabel, BorderLayout.CENTER);
            card.add(button, BorderLayout.SOUTH);

            parent.add(card);
        }

        @Override public String getDestinationId() { return "home"; }
        @Override public JComponent getView() { return this; }
    }

    class SearchFragment extends Fragment {
        private JTextField searchField;

        public SearchFragment() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Search panel
            JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
            searchField = new JTextField();
            JButton searchButton = new JButton("Search");

            searchPanel.add(new JLabel("Find what you're looking for:"), BorderLayout.NORTH);
            searchPanel.add(searchField, BorderLayout.CENTER);
            searchPanel.add(searchButton, BorderLayout.EAST);

            // Results area
            JTextArea resultsArea = new JTextArea();
            resultsArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(resultsArea);

            // Button actions
            searchButton.addActionListener(e -> {
                String query = searchField.getText();
                if (!query.isEmpty()) {
                    resultsArea.setText("Search results for: " + query + "\n\n" +
                            "1. Result one\n2. Result two\n3. Result three");
                }
            });

            // Layout
            add(searchPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);

            // Back to home button
            JButton homeButton = new JButton("Back to Home");
            homeButton.addActionListener(e -> navigationController.navigateBack());
            add(homeButton, BorderLayout.SOUTH);
        }

        @Override
        public void onAttached() {
            searchField.setText("");
            navigationController.setToolbarTitle("Search");
        }

        @Override public String getDestinationId() { return "search"; }
        @Override public JComponent getView() { return this; }
    }
    class ProfileFragment extends Fragment {
        public ProfileFragment() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Profile header
            JPanel headerPanel = new JPanel(new BorderLayout());
            JLabel avatar = new JLabel("👤", SwingConstants.CENTER);
            avatar.setFont(new Font("SansSerif", Font.PLAIN, 64));

            JLabel nameLabel = new JLabel("John Doe", SwingConstants.CENTER);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

            headerPanel.add(avatar, BorderLayout.CENTER);
            headerPanel.add(nameLabel, BorderLayout.SOUTH);

            // Details panel
            JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            addDetail(detailsPanel, "Email", "john.doe@example.com");
            addDetail(detailsPanel, "Phone", "+1 (555) 123-4567");
            addDetail(detailsPanel, "Location", "New York, USA");

            // Buttons
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            JButton editButton = new JButton("Edit Profile");
            JButton settingsButton = new JButton("Settings");

            editButton.addActionListener(e -> navigationController.navigateTo("details"));
            settingsButton.addActionListener(e -> navigationController.navigateTo("settings"));

            buttonPanel.add(editButton);
            buttonPanel.add(settingsButton);

            // Layout
            add(headerPanel, BorderLayout.NORTH);
            add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void addDetail(JPanel panel, String label, String value) {
            JPanel detailPanel = new JPanel(new BorderLayout());
            detailPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JLabel labelComponent = new JLabel(label + ":");
            labelComponent.setFont(labelComponent.getFont().deriveFont(Font.BOLD));

            JTextField valueField = new JTextField(value);
            valueField.setEditable(false);
            valueField.setBorder(BorderFactory.createEmptyBorder());

            detailPanel.add(labelComponent, BorderLayout.WEST);
            detailPanel.add(valueField, BorderLayout.CENTER);

            panel.add(detailPanel);
        }

        @Override
        public void onAttached() {
            navigationController.setToolbarTitle("Profile");
        }

        @Override public String getDestinationId() { return "profile"; }
        @Override public JComponent getView() { return this; }
    }
    class DetailsFragment extends Fragment {
        private JTextField nameField;
        private JTextField emailField;

        public DetailsFragment() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Form panel
            JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));

            nameField = new JTextField("John Doe");
            emailField = new JTextField("john.doe@example.com");

            addFormField(formPanel, "Name:", nameField);
            addFormField(formPanel, "Email:", emailField);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            JButton saveButton = new JButton("Save Changes");
            JButton cancelButton = new JButton("Cancel");

            saveButton.addActionListener(e -> {
                // In a real app, save changes here
                navigationController.navigateBack();
            });

            cancelButton.addActionListener(e -> navigationController.navigateBack());

            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);

            // Layout
            add(new JScrollPane(formPanel), BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void addFormField(JPanel panel, String label, JTextField field) {
            JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
            fieldPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JLabel labelComponent = new JLabel(label);
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));

            fieldPanel.add(labelComponent, BorderLayout.WEST);
            fieldPanel.add(field, BorderLayout.CENTER);

            panel.add(fieldPanel);
        }

        @Override
        public void onAttached() {
            navigationController.setToolbarTitle("Edit Profile");
        }

        @Override public String getDestinationId() { return "details"; }
        @Override public JComponent getView() { return this; }
    }
    class NotificationsFragment extends Fragment {
        public NotificationsFragment() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Notification list
            DefaultListModel<String> listModel = new DefaultListModel<>();
            listModel.addElement("New message from Alice");
            listModel.addElement("Your order has shipped");
            listModel.addElement("System update available");
            listModel.addElement("New connection request");
            listModel.addElement("Event reminder: Meeting at 3PM");

            JList<String> notificationList = new JList<>(listModel);
            notificationList.setCellRenderer(new NotificationRenderer());

            add(new JScrollPane(notificationList), BorderLayout.CENTER);

            // Clear button
            JButton clearButton = new JButton("Clear All Notifications");
            clearButton.addActionListener(e -> listModel.clear());
            add(clearButton, BorderLayout.SOUTH);
        }

        private static class NotificationRenderer extends DefaultListCellRenderer {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));

              //  label.setIcon(new ImageIcon(getClass().getResource("/ui/home.png"))); // Use your own icon
                return label;
            }
        }

        @Override
        public void onAttached() {
            navigationController.setToolbarTitle("Notifications");
        }

        @Override public String getDestinationId() { return "notifications"; }
        @Override public JComponent getView() { return this; }
    }
}