package todolistapp.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDate;

/**
 * Represents a single task in the TODO list application.
 * It contains properties for the task's description, completion status,
 * priority, due date, and creation date.
 */
public class TodoItem {

    /**
     * Enum representing the priority levels for a TodoItem.
     */
    public enum Priority {
        /** High priority task. */
        HIGH,
        /** Medium priority task. */
        MEDIUM,
        /** Low priority task. */
        LOW
    }

    private final StringProperty description;
    private final BooleanProperty done;
    private final ObjectProperty<Priority> priority;
    private final ObjectProperty<LocalDate> dueDate;
    private final LocalDate creationDate;

    /**
     * Constructs a new TodoItem with the specified description, priority, and due date.
     * The creation date is set to the current date, and the task is initially marked as not done.
     *
     * @param description The description of the task.
     * @param priority The priority of the task.
     * @param dueDate The due date of the task.
     */
    public TodoItem(String description, Priority priority, LocalDate dueDate) {
        this.description = new SimpleStringProperty(description);
        this.priority = new SimpleObjectProperty<>(priority);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.creationDate = LocalDate.now();
        this.done = new SimpleBooleanProperty(false);
    }

    // Description Property
    /**
     * Returns the JavaFX StringProperty for the task's description.
     * This allows binding the description to UI elements.
     * @return The StringProperty for the description.
     */
    public StringProperty descriptionProperty() {
        return description;
    }

    /**
     * Gets the description of the task.
     * @return The task description.
     */
    public String getDescription() {
        return description.get();
    }

    /**
     * Sets the description of the task.
     * @param description The new task description.
     */
    public void setDescription(String description) {
        this.description.set(description);
    }

    // Done Property
    /**
     * Returns the JavaFX BooleanProperty for the task's completion status.
     * This allows binding the status to UI elements like CheckBoxes.
     * @return The BooleanProperty for the completion status.
     */
    public BooleanProperty doneProperty() {
        return done;
    }

    /**
     * Checks if the task is marked as done.
     * @return True if the task is done, false otherwise.
     */
    public boolean isDone() {
        return done.get();
    }

    /**
     * Sets the completion status of the task.
     * @param done True to mark the task as done, false otherwise.
     */
    public void setDone(boolean done) {
        this.done.set(done);
    }

    // Priority Property
    /**
     * Returns the JavaFX ObjectProperty for the task's priority.
     * This allows binding the priority to UI elements.
     * @return The ObjectProperty for the priority.
     */
    public ObjectProperty<Priority> priorityProperty() {
        return priority;
    }

    /**
     * Gets the priority of the task.
     * @return The task priority.
     */
    public Priority getPriority() {
        return priority.get();
    }

    /**
     * Sets the priority of the task.
     * @param priority The new task priority.
     */
    public void setPriority(Priority priority) {
        this.priority.set(priority);
    }

    // DueDate Property
    /**
     * Returns the JavaFX ObjectProperty for the task's due date.
     * This allows binding the due date to UI elements.
     * @return The ObjectProperty for the due date.
     */
    public ObjectProperty<LocalDate> dueDateProperty() {
        return dueDate;
    }

    /**
     * Gets the due date of the task.
     * @return The task due date.
     */
    public LocalDate getDueDate() {
        return dueDate.get();
    }

    /**
     * Sets the due date of the task.
     * @param dueDate The new task due date.
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate.set(dueDate);
    }

    // CreationDate (read-only, only getter)
    /**
     * Gets the creation date of the task. This date is set when the task is created and cannot be changed.
     * @return The task creation date.
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }
} 