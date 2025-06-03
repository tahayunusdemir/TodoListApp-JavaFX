package todolistapp.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDate;

public class TodoItem {

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    private final StringProperty description;
    private final BooleanProperty done;
    private final ObjectProperty<Priority> priority;
    private final ObjectProperty<LocalDate> dueDate;
    private final LocalDate creationDate;

    public TodoItem(String description, Priority priority, LocalDate dueDate) {
        this.description = new SimpleStringProperty(description);
        this.priority = new SimpleObjectProperty<>(priority);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.creationDate = LocalDate.now();
        this.done = new SimpleBooleanProperty(false);
    }

    // Description Property
    public StringProperty descriptionProperty() {
        return description;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    // Done Property
    public BooleanProperty doneProperty() {
        return done;
    }

    public boolean isDone() {
        return done.get();
    }

    public void setDone(boolean done) {
        this.done.set(done);
    }

    // Priority Property
    public ObjectProperty<Priority> priorityProperty() {
        return priority;
    }

    public Priority getPriority() {
        return priority.get();
    }

    public void setPriority(Priority priority) {
        this.priority.set(priority);
    }

    // DueDate Property
    public ObjectProperty<LocalDate> dueDateProperty() {
        return dueDate;
    }

    public LocalDate getDueDate() {
        return dueDate.get();
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate.set(dueDate);
    }

    // CreationDate (read-only, only getter)
    public LocalDate getCreationDate() {
        return creationDate;
    }
} 