package concrete.goonie.ui;

import concrete.goonie.animation.AnimationHelper;
import concrete.goonie.core.AnimationType;
import concrete.goonie.core.NavigationController;
import concrete.goonie.core.NavigationEvent;
import concrete.goonie.core.NavigationListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public abstract class AppFrame extends JFrame implements NavigationController {
    protected CardLayout cardLayout;
    protected Panel rootPanel;
    protected Toolbar toolbar;
    protected JPanel bottomNav;
    protected JPanel drawer;
    protected JPanel leftPanel;
    protected JPanel rightPanel;
    private JPanel mainContainer;
    protected Stack<String> backStack = new Stack<>();
    protected Map<String, Fragment> fragments = new HashMap<>();
    protected NavigationListener navigationListener;

    private AnimationType defaultAnimation = AnimationType.NONE;


    public AppFrame(String title) {
        super(title);
        initializeUI();
    }

    protected void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

//        // Create root panel that will contain everything
        rootPanel = new Panel(new BorderLayout());
        rootPanel.enableGradientBackground(true);
        add(rootPanel, BorderLayout.CENTER);
//
//        // Initialize main container with CardLayout
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        rootPanel.add(mainContainer, BorderLayout.CENTER);

        toolbar = new Toolbar();
        toolbar.setBackButtonListener(e -> navigateBack());
        rootPanel.add(toolbar, BorderLayout.NORTH);

//        // Initialize side panels (initially empty and hidden)
        leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(200, getHeight()));
        leftPanel.setVisible(false);
        rootPanel.add(leftPanel, BorderLayout.WEST);
//
        rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(200, getHeight()));
        rightPanel.setVisible(false);
        rootPanel.add(rightPanel, BorderLayout.EAST);
//
//        // Bottom navigation
        bottomNav = new JPanel(new GridLayout(1, 3));
        rootPanel.add(bottomNav, BorderLayout.SOUTH);

//
//        // Drawer
        drawer = new JPanel();
        drawer.setPreferredSize(new Dimension(300, getHeight()));
        drawer.setVisible(false);
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
        if (show) {
            add(drawer, BorderLayout.WEST);
        } else {
            remove(drawer);
        }
        drawer.setVisible(show);
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
            mainContainer.remove(this.toolbar);
        }

        if (customToolbar == null) {
            // Restore default toolbar
            this.toolbar = new Toolbar();
            this.toolbar.setBackButtonListener(e -> navigateBack());
            mainContainer.add(this.toolbar, BorderLayout.NORTH);
        } else {
            // Use custom toolbar
            this.toolbar = customToolbar;
            mainContainer.add(this.toolbar, BorderLayout.NORTH);
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
        leftPanel = (JPanel) component;
        mainContainer.add(component, BorderLayout.WEST);
    }

    public void setRightComponent(JComponent component) {
        rightPanel = (JPanel) component;
        mainContainer.add(component, BorderLayout.EAST);
    }

    public void removeLeftComponent() {
        mainContainer.remove(leftPanel);
    }

    public void removeRightComponent() {
        mainContainer.remove(rightPanel);
    }

}
