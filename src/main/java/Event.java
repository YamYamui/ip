/**
 * Represents an event that occurs over a time period.
 *
 * <p>An Event is a task that starts at a specific date/time and ends at
 * a specific date/time. Both times are stored as strings and displayed
 * in the format {@code [E][ ] <description> (from: <start> to: <end>)}.
 */
public class Event {

    /** Human-readable text of the task. */
    private String description;

    /** Whether the task has been completed. */
    private boolean isDone;

    /** The start date/time of this event, stored as a string. */
    protected String from;

    /** The end date/time of this event, stored as a string. */
    protected String to;

    /**
     * Creates a new Event task with the given description, start time, and end time,
     * initially marked as not done.
     *
     * @param description the text of the task.
     * @param from the start date/time as a string.
     * @param to the end date/time as a string.
     */
    public Event(String description, String from, String to) {
        this.description = description;
        this.isDone = false;
        this.from = from;
        this.to = to;
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

    /** Returns the description and time range of this task. */
    public String getDescription() {
        return description + " (from: " + from + " to: " + to + ")";
    }
}
