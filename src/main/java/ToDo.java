/**
 * Represents a todo task with no associated date or deadline.
 *
 * <p>A ToDo is a simple task with just a description.
 * It is displayed with the type icon {@code [T]}.
 */
public class ToDo {

    /** Human-readable text of the task. */
    private String description;

    /** Whether the task has been completed. */
    private boolean isDone;

    /**
     * Creates a new ToDo task with the given description,
     * initially marked as not done.
     *
     * @param description the text of the task.
     */
    public ToDo(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns the done-state icon for this task. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns the description of this task. */
    public String getDescription() {
        return description;
    }
}
