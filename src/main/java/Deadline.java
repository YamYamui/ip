/**
 * Represents a task with a deadline.
 *
 * <p>A Deadline is a task that must be completed by a specific date/time.
 * The deadline is stored as a string and displayed in the format
 * {@code [D][ ] <description> (by: <deadline>)}.
 */
public class Deadline {

    /** Human-readable text of the task. */
    private String description;

    /** Whether the task has been completed. */
    private boolean isDone;

    /** The deadline for this task, stored as a string. */
    protected String by;

    /**
     * Creates a new Deadline task with the given description and deadline,
     * initially marked as not done.
     *
     * @param description the text of the task.
     * @param by the deadline date/time as a string.
     */
    public Deadline(String description, String by) {
        this.description = description;
        this.isDone = false;
        this.by = by;
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

    /** Returns the description and deadline of this task. */
    public String getDescription() {
        return description + " (by: " + by + ")";
    }
}
