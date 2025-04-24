package concrete.goonie.ui;

import concrete.goonie.animation.AnimationHelper;
import concrete.goonie.core.AnimationType;
import concrete.goonie.core.NavigationController;
import concrete.goonie.core.NavigationEvent;
import concrete.goonie.core.NavigationListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public abstract class AppFrame extends JFrame implements NavigationController {
    protected CardLayout cardLayout;
    protected Panel rootPanel;
    protected Toolbar toolbar;
    protected JPanel bottomNav;
    protected JPanel drawer;
    protected JComponent leftPanel;
    protected JComponent rightPanel;
    private JPanel mainContainer;
    protected Stack<String> backStack = new Stack<>();
    protected Map<String, Fragment> fragments = new HashMap<>();
    protected NavigationListener navigationListener;

    private AnimationType defaultAnimation = AnimationType.NONE;
    private JPanel overlayPanel;


    public AppFrame(String title) {
        super(title);
        initializeUI();
    }

    protected void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);

        // Main root panel that holds the UI
        rootPanel = new Panel(new BorderLayout());
        rootPanel.enableGradientBackground(true);
        rootPanel.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(rootPanel, JLayeredPane.DEFAULT_LAYER);

        // Card layout container
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setOpaque(false);
        rootPanel.add(mainContainer, BorderLayout.CENTER);

        // Toolbar
        toolbar = new Toolbar();
        toolbar.setBackButtonListener(e -> navigateBack());
        rootPanel.add(toolbar, BorderLayout.NORTH);

        // Bottom nav
        bottomNav = new JPanel(new GridLayout(1, 3));
        rootPanel.add(bottomNav, BorderLayout.SOUTH);

        // Drawer panel
        drawer = new JPanel();
        drawer.setBackground(new Color(0, 0, 0, 180)); // semi-transparent
        drawer.setBounds(0, 0, 300, getHeight());
        drawer.setVisible(false);
        layeredPane.add(drawer, JLayeredPane.PALETTE_LAYER);

        // Overlay panel for background clicks
        overlayPanel = new JPanel();
        overlayPanel.setOpaque(false); // transparent
        overlayPanel.setBounds(0, 0, getWidth(), getHeight());
        overlayPanel.setVisible(false);
        overlayPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hideDrawer();
            }
        });
        layeredPane.add(overlayPanel, JLayeredPane.MODAL_LAYER);

        // Resize components when window resizes
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = layeredPane.getWidth();
                int height = layeredPane.getHeight();

                rootPanel.setBounds(0, 0, width, height);
                drawer.setBounds(0, 0, 300, height);
                overlayPanel.setBounds(0, 0, width, height);

                rootPanel.revalidate();
                rootPanel.repaint();
            }
        });
    }


    private void showDrawer() {
        drawer.setVisible(true);
        overlayPanel.setVisible(true);
        overlayPanel.repaint();
    }

    private void hideDrawer() {
        drawer.setVisible(false);
        overlayPanel.setVisible(false);
    }


    public void registerFragment(Fragment fragment) {
        fragments.put(fragment.getDestinationId(), fragment);
        mainContainer.add(fragment.getView(), fragment.getDestinationId());
        fragment.setNavigationController(this);
    }

    @Override
    public void navigateTo(String destination) {
        navigateTo(destination, true, defaultAnimation);
    }

    @Override
    public void navigateTo(String destination, boolean addToBackStack) {
        navigateTo(destination, addToBackStack, defaultAnimation);
    }

    @Override
    public void navigateTo(String destination, boolean addToBackStack, AnimationType animation) {
        if (!fragments.containsKey(destination)) {
            throw new IllegalArgumentException("Unknown destination: " + destination);
        }

        final Fragment newFragment = fragments.get(destination);
        final Fragment oldFragment = backStack.isEmpty() ? null : fragments.get(backStack.peek());

        if (addToBackStack) {
            backStack.push(destination);
        } else {
            // For top-level navigation, replace the current stack
            if (!backStack.isEmpty()) {
                String current = backStack.peek();
                if (!current.equals(destination)) {
                    backStack.pop();
                    backStack.push(destination);
                }
            } else {
                backStack.push(destination);
            }
        }

        Runnable completionHandler = () -> {
            fragments.get(destination).onAttached();
            updateToolbarForFragment(destination);
            if (navigationListener != null) {
                navigationListener.onNavigationEvent(NavigationEvent.NAVIGATED_TO);
            }
        };
        if (oldFragment == null || animation == AnimationType.NONE) {
            cardLayout.show(mainContainer, destination);
            completionHandler.run();

        } else {
            // Prepare components for animation
            oldFragment.getView().setVisible(true);
            newFragment.getView().setVisible(true);

            // Show the new fragment behind the animation
            cardLayout.show(mainContainer, destination);

            // Animate transition
            AnimationHelper.animateTransition(
                    oldFragment.getView(),
                    newFragment.getView(),
                    animation,
                    completionHandler
            );
        }
    }

    @Override
    public void navigateBack() {
        navigateBack(getReverseAnimation(defaultAnimation));
    }

    @Override
    public void navigateBack(AnimationType animation) {
        if (backStack.size() > 1) {
            String current = backStack.pop();
            String previous = backStack.peek();

            final Fragment oldFragment = fragments.get(current);
            final Fragment newFragment = fragments.get(previous);

            Runnable completionHandler = () -> {
                oldFragment.onDetached();
                newFragment.onAttached();
                updateToolbarForFragment(previous);
                if (navigationListener != null) {
                    navigationListener.onNavigationEvent(NavigationEvent.NAVIGATED_BACK);
                }
            };

            if (animation == AnimationType.NONE) {
                cardLayout.show(mainContainer, previous);
                completionHandler.run();
            } else {
                // Prepare components for animation
                oldFragment.getView().setVisible(true);
                newFragment.getView().setVisible(true);

                // Show the previous fragment behind the animation
                cardLayout.show(mainContainer, previous);

                // Animate transition
                AnimationHelper.animateTransition(
                        oldFragment.getView(),
                        newFragment.getView(),
                        animation,
                        completionHandler
                );
            }
        }
    }

    private AnimationType getReverseAnimation(AnimationType animation) {
        switch (animation) {
            case SLIDE_LEFT:
                return AnimationType.SLIDE_RIGHT;
            case SLIDE_RIGHT:
                return AnimationType.SLIDE_LEFT;
            case FADE_IN:
                return AnimationType.FADE_OUT;
            case FADE_OUT:
                return AnimationType.FADE_IN;
            case ZOOM_IN:
                return AnimationType.ZOOM_OUT;
            case ZOOM_OUT:
                return AnimationType.ZOOM_IN;
            default:
                return AnimationType.NONE;
        }
    }

    public void setDefaultAnimation(AnimationType animation) {
        this.defaultAnimation = animation;
    }

    @Override
    public void setRoot(String destination) {
        if (!fragments.containsKey(destination)) {
            throw new IllegalArgumentException("Unknown destination: " + destination);
        }

        backStack.clear();
        backStack.push(destination);
        cardLayout.show(mainContainer, destination);
        fragments.get(destination).onAttached();

        if (navigationListener != null) {
            navigationListener.onNavigationEvent(NavigationEvent.ROOT_CHANGED);
        }
    }

    @Override
    public void showDrawer(boolean show) {
        if (!show) {
            hideDrawer();
        } else {
            showDrawer();
        }

        revalidate();
        repaint();

        if (navigationListener != null) {
            navigationListener.onNavigationEvent(NavigationEvent.DRAWER_TOGGLED);
        }
    }

    @Override
    public void showBottomNav(boolean show) {
        bottomNav.setVisible(show);
    }

    @Override
    public void showToolbar(boolean show) {
        toolbar.setVisible(show);
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    public void setToolbarMenuAction(ActionListener action) {
        toolbar.setMenuButtonListener(action);
    }

    public void updateToolbarForFragment(String fragmentId) {
        if (backStack.size() > 1) {
            toolbar.showBackButton(true);
        } else {
            toolbar.showBackButton(false);
        }

        // You can add fragment-specific toolbar customization here
        if ("home".equals(fragmentId)) {
            toolbar.setTitle("Home");
        } else if ("profile".equals(fragmentId)) {
            toolbar.setTitle("Profile");
        }

    }

    public void setToolbar(Toolbar customToolbar) {
        // Remove existing toolbar
        if (this.toolbar != null) {
            rootPanel.remove(this.toolbar);
        }

        if (customToolbar == null) {
            // Restore default toolbar
            this.toolbar = new Toolbar();
            this.toolbar.setBackButtonListener(e -> navigateBack());
            rootPanel.add(this.toolbar, BorderLayout.NORTH);
        } else {
            // Use custom toolbar
            this.toolbar = customToolbar;
            rootPanel.add(this.toolbar, BorderLayout.NORTH);
        }

        // Update toolbar state for current fragment
        if (!backStack.isEmpty()) {
            updateToolbarForFragment(backStack.peek());
        }

        revalidate();
        repaint();
    }

    @Override
    public void setNavigationListener(NavigationListener listener) {
        this.navigationListener = listener;
    }

    public void setLeftComponent(JComponent component) {
        if (leftPanel != null) {
            removeLeftComponent();
            leftPanel = component;
            mainContainer.add(component, BorderLayout.WEST);
        }
    }

    public void setRightComponent(JComponent component) {
        if (rightPanel != null) {
            removeRightComponent();
            rightPanel = component;
            mainContainer.add(component, BorderLayout.EAST);
        }
    }

    public void removeLeftComponent() {
        mainContainer.remove(leftPanel);
    }

    public void removeRightComponent() {
        mainContainer.remove(rightPanel);
    }

}
