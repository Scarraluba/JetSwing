package concrete.goonie.core;

import javax.swing.*;

public interface NavigableComponent {
    String getDestinationId();
    JComponent getView();
    void onAttached();
    void onDetached();
}
