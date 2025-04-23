package concrete.goonie.ui;

import concrete.goonie.core.NavigableComponent;
import concrete.goonie.core.NavigationController;

import javax.swing.*;

public abstract class Fragment extends AnimatablePanel implements NavigableComponent {
    protected NavigationController navigationController;

    private final String destinationId;

    public Fragment() {
        this.destinationId = "";
    }

    protected Fragment(String destinationId) {
        this.destinationId = destinationId;
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
        return destinationId.isEmpty() ? getClass().getSimpleName() : destinationId;
    }


    public void setNavigationController(NavigationController controller) {
        this.navigationController = controller;
    }
}

