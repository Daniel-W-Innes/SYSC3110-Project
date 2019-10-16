package view;

import java.util.EventObject;

/**
 * The observer interface following Observer pattern (which is normally implemented by a view).
 * The observer is notified via the method: update(instance of the new updated model).
 */
public interface Observer {

    /**
     * Update method is called to notify the observer instance that the Board Model has been changed.
     * This method is responsible for updating the observer according to the new Board Model.
     *
     * @param eventObject The a event from the Board Model
     */
    void update(EventObject eventObject);

    void exit();
}
