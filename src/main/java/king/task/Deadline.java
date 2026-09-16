package king.task;

/**
 * Represents a task with a deadline.
 *
 * <p>A Deadline is a task that must be completed by a specific date/time.
 * The deadline is stored as a string and displayed in the format
 * {@code [D][ ] <description> (by: <deadline>)}.
 */
public class Deadline extends Task {

    /** The deadline for this task, stored as a string. */
    private final String by;

    /**
     * Creates a new Deadline task with the given description and deadline,
     * initially marked as not done.
     *
     * @param description the text of the task.
     * @param by the deadline date/time as a string.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    public String getBy() {
        return by;
    }

    /**
     * Returns the description of this task followed by its deadline,
     * e.g. {@code return book (by: June 6th)}.
     */
    @Override
    public String getDescription() {
        return super.getDescription() + " (by: " + by + ")";
    }

    /**
     * Returns the type icon of a deadline task.
     */
    @Override
    public String getTaskType() {
        return "D";
    }
}
