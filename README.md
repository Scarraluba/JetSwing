# Goonie Navigation Framework for Swing

A modern navigation framework for Java Swing inspired by Jetpack Compose Navigation, providing familiar mobile-style navigation patterns for desktop applications.

## Features

- **Fragment-based navigation** with lifecycle awareness
- **Multiple navigation patterns**:
  - Bottom navigation bar
  - Navigation drawer
  - Toolbar with back navigation
- **Animated transitions** between screens
- **Navigation stack** management
- **Top-level navigation** that doesn't clutter back stack
- **Flexible architecture** with interfaces and base classes

## Installation

Add the package to your Java project. The framework has no external dependencies beyond standard Java Swing.

## Basic Usage

### 1. Create your main application frame:

```java
public class MyApp extends AppFrame {
    public MyApp() {
        super("My Application");
        
        // Register fragments
        registerFragment(new HomeFragment());
        registerFragment(new ProfileFragment());
        
        // Setup navigation components
        setupBottomNavigation();
        setupNavigationDrawer();
        setupToolbar();
        
        // Set initial state
        setRoot("home");
        setToolbarTitle("Home");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MyApp app = new MyApp();
            app.setVisible(true);
        });
    }
}
```

### 2. Create fragments:

```java
class HomeFragment extends Fragment {
    public HomeFragment() {
        setLayout(new BorderLayout());
        add(new JLabel("Home Screen", SwingConstants.CENTER));
        
        JButton profileBtn = new JButton("Go to Profile");
        profileBtn.addActionListener(e -> 
            navigationController.navigateTo("profile"));
        add(profileBtn, BorderLayout.SOUTH);
    }
    
    @Override public String getDestinationId() { return "home"; }
    @Override public JComponent getView() { return this; }
}
```

## Navigation Options

### Basic navigation:

```java
// Navigate to a destination (adds to back stack)
navigationController.navigateTo("destinationId");

// Navigate without adding to back stack (for top-level destinations)
navigationController.navigateTo("home", false);

// Navigate with custom animation
navigationController.navigateTo("details", true, AnimationType.SLIDE_LEFT);

// Navigate back
navigationController.navigateBack();

// Navigate back with custom animation
navigationController.navigateBack(AnimationType.SLIDE_RIGHT);
```

### Available Animations:

- `SLIDE_LEFT` / `SLIDE_RIGHT`
- `FADE_IN` / `FADE_OUT`
- `ZOOM_IN` / `ZOOM_OUT`
- `NONE` (default)

## Advanced Configuration

### Customizing animations:

```java
// Set default animation for all navigations
setDefaultAnimation(AnimationType.FADE_IN);

// Implement custom animations by extending AnimationHelper
```

### Navigation Events:

```java
navigationController.setNavigationListener(event -> {
    switch (event) {
        case NAVIGATED_TO:
            // Handle navigation to new destination
            break;
        case NAVIGATED_BACK:
            // Handle back navigation
            break;
        case DRAWER_TOGGLED:
            // Handle drawer open/close
            break;
    }
});
```

## Best Practices

1. Use `navigateTo(destination, false)` for top-level destinations (bottom nav, drawer items)
2. Use regular `navigateTo()` for sub-screens that should be in back stack
3. Set appropriate titles in `onAttached()` method of fragments
4. For complex apps, consider creating a navigation graph that defines all destinations

## Example Applications

See the `samples` package for complete example implementations including:

- Basic navigation demo
- Animated transitions demo
- Complex app with multiple navigation patterns

## License

MIT License - free for commercial and personal use
