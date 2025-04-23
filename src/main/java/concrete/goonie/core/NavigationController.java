package concrete.goonie.core;



public interface NavigationController {
    void navigateTo(String destination);
    void navigateTo(String destination, boolean addToBackStack);
    void navigateTo(String destination, boolean addToBackStack, AnimationType animation);
    void navigateBack();
    void navigateBack(AnimationType animation);
    void setRoot(String destination);
    void showDrawer(boolean show);
    void showBottomNav(boolean show);
    void showToolbar(boolean show);
    void setToolbarTitle(String title);
    void setNavigationListener(NavigationListener listener);
}