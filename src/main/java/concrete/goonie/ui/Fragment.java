package concrete.goonie.ui;

import concrete.goonie.core.NavigableComponent;
import concrete.goonie.core.NavigationController;

import javax.swing.*;

public abstract class Fragment extends AnimatablePanel implements NavigableComponent {
    protected NavigationController navigationController;

    private final String title;

    public Fragment() {
        this.title = "";
    }

    protected Fragment(String title) {
        this.title = title;
    }

    @Override
    public void onAttached() {
        // Default implementation does nothing
    }

    @Override
    public void onDetached() {
        // Default implementation does nothing
    }

    @Override
    public String getDestinationId() {
        return title.isEmpty() ? getClass().getSimpleName() : title;
    }


    public void setNavigationController(NavigationController controller) {
        this.navigationController = controller;
    }
}

