package king.task;

/**
 * Represents an event that occurs over a time period.
 *
 * <p>An Event is a task that starts at a specific date/time and ends at
 * a specific date/time. Both times are stored as strings and displayed
 * in the format {@code [E][ ] <description> (from: <start> to: <end>)}.
 */
public class Event extends Task {

    /** The start date/time of this event, stored as a string. */
    private final String from;

    /** The end date/time of this event, stored as a string. */
    private final String to;

    /**
     * Creates a new Event task with the given description, start time, and end time,
     * initially marked as not done.
     *
     * @param description the text of the task.
     * @param from the start date/time as a string.
     * @param to the end date/time as a string.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    /**
     * Returns the description of this event followed by its time range,
     * e.g. {@code project meeting (from: Aug 6th 2pm to: 4pm)}.
     */
    @Override
    public String getDescription() {
        return super.getDescription() + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns the type icon of an event task.
     */
    @Override
    public String getTaskType() {
        return "E";
    }
}
