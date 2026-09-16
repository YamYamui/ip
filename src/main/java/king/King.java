package king;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import king.exception.KingException;
import king.task.Deadline;
import king.task.Event;
import king.task.Task;
import king.task.ToDo;

/**
 * Runs the King chatbot, which stores todos, deadlines, and events.
 * Commands can list tasks and change their completion status. Invalid commands
 * display an explanation and leave the task list unchanged.
 */
public class King {

    /** Line of underscores used to frame the chatbot's messages. */
    private static final String LINE = "____________________________________________________________\n";

    /** Expected syntax for a deadline command. */
    private static final String USAGE_DEADLINE = "deadline <description> /by <date/time>";

    /** Expected syntax for an event command. */
    private static final String USAGE_EVENT = "event <description> /from <start> /to <end>";

    /**
     * Reads commands until the user enters {@code bye} or input ends.
     * Reports command errors without ending the conversation.
     */
    public static void main(String[] args) {
        String banner = "    __    _          \n"
                + "   / /__ (_)__  ___ _\n"
                + "  /  '_// / _ \\/ _ `/\n"
                + " /_/\\_\\/_/_//_/\\_, / \n"
                + "              /___/\n";
        List<Task> tasks = new ArrayList<>();

        System.out.println(LINE);
        System.out.print(banner);
        System.out.println("Hello, my subject. I am King, your faithful chatbot.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        try (Scanner console = new Scanner(System.in)) {
            while (console.hasNextLine()) {
                String command = console.nextLine().trim();
                String[] parts = command.split("\\s+", 2);
                String firstWord = parts[0];
                String arguments = parts.length > 1 ? parts[1].trim() : "";
                System.out.println(LINE);

                try {
                    requireText(command, "please enter a command, such as todo read a book or list.");
                    if (firstWord.equalsIgnoreCase("bye")) {
                        requireNoArguments(arguments, "bye");
                        System.out.println("    Bye. Hope to see you again soon!");
                        System.out.println(LINE);
                        break;
                    } else if (firstWord.equalsIgnoreCase("list")) {
                        requireNoArguments(arguments, "list");
                        printTasks(tasks);
                    } else if (firstWord.equalsIgnoreCase("mark") || firstWord.equalsIgnoreCase("unmark")) {
                        updateTaskStatus(tasks, arguments, firstWord.equalsIgnoreCase("mark"));
                    } else if (firstWord.equalsIgnoreCase("delete")) {
                        deleteTask(tasks, arguments);
                    } else {
                        Task task = parseTask(firstWord, arguments);
                        tasks.add(task);
                        printAddedTask(task, tasks.size());
                    }
                } catch (KingException exception) {
                    System.out.println("    My subject, " + exception.getMessage());
                }
                System.out.println(LINE);
            }
        }
    }

    /**
     * Returns a task parsed from an add command without changing the task list.
     *
     * @throws KingException If the command is unknown or its fields are invalid.
     */
    private static Task parseTask(String command, String arguments) throws KingException {
        if (command.equalsIgnoreCase("todo")) {
            requireText(arguments, "a todo needs a description. Try: todo read a book");
            return new ToDo(arguments);
        } else if (command.equalsIgnoreCase("deadline")) {
            String[] parts = splitClause(arguments, "/by", USAGE_DEADLINE);
            requireText(parts[0], "a deadline needs a description. Try: " + USAGE_DEADLINE);
            requireText(parts[1], "a deadline needs a date/time after /by. Try: " + USAGE_DEADLINE);
            return new Deadline(parts[0], parts[1]);
        } else if (command.equalsIgnoreCase("event")) {
            String[] parts = splitClause(arguments, "/from", USAGE_EVENT);
            // Check the entire command for duplicate /to markers before parsing the times.
            splitClause(arguments, "/to", USAGE_EVENT);
            String[] times = splitClause(parts[1], "/to", USAGE_EVENT);
            requireText(parts[0], "an event needs a description. Try: " + USAGE_EVENT);
            requireText(times[0], "an event needs a start time after /from. Try: " + USAGE_EVENT);
            requireText(times[1], "an event needs an end time after /to. Try: " + USAGE_EVENT);
            return new Event(parts[0], times[0], times[1]);
        }
        throw new KingException("I do not recognize '" + command
                + "'. Use todo, deadline, event, list, mark, unmark, delete, or bye.");
    }

    /**
     * Splits text at exactly one standalone clause marker and trims both fields.
     * Preserves empty fields so callers can explain which value is missing.
     *
     * @throws KingException If the marker is missing or repeated.
     */
    private static String[] splitClause(String text, String marker, String usage) throws KingException {
        String[] parts = text.split("(?<!\\S)" + marker + "(?=\\s|$)", -1);
        if (parts.length != 2) {
            throw new KingException("use exactly one " + marker + " clause. Try: " + usage);
        }
        parts[0] = parts[0].trim();
        parts[1] = parts[1].trim();
        return parts;
    }

    /**
     * Rejects an empty command or field with an explanation for the user.
     *
     * @throws KingException If the text is empty.
     */
    private static void requireText(String text, String message) throws KingException {
        if (text.isEmpty()) {
            throw new KingException(message);
        }
    }

    /**
     * Rejects extra arguments for commands that take none.
     *
     * @throws KingException If arguments were supplied.
     */
    private static void requireNoArguments(String arguments, String command) throws KingException {
        if (!arguments.isEmpty()) {
            throw new KingException("'" + command + "' takes no arguments. Type just: " + command);
        }
    }

    /**
     * Prints all stored tasks as a numbered list.
     */
    private static void printTasks(List<Task> tasks) {
        System.out.println("    Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            printTask(i + 1, tasks.get(i));
        }
    }

    /**
     * Prints a task with its type, done state, and description.
     * Includes a list number only when the index is positive.
     */
    private static void printTask(int index, Task task) {
        if (index > 0) {
            System.out.printf("    %d. ", index);
        }
        System.out.printf("[%s][%s] %s%n", task.getTaskType(), task.getStatusIcon(), task.getDescription());
    }

    /**
     * Returns the zero-based index of an existing task from a user-supplied number.
     *
     * @throws KingException If the number is missing, nonnumeric, or out of range.
     */
    private static int parseTaskIndex(String indexText, int taskCount) throws KingException {
        requireText(indexText, "please supply a task number. Try: mark 1, unmark 1, or delete 1");
        int index;
        try {
            index = Integer.parseInt(indexText);
        } catch (NumberFormatException exception) {
            throw new KingException("a task number must be a whole number. Use list to see valid numbers.");
        }
        if (taskCount == 0) {
            throw new KingException("your list is empty. Add a task first, for example: todo read a book");
        }
        if (index < 1 || index > taskCount) {
            throw new KingException("there is no task " + index + ". Choose a number from 1 to "
                    + taskCount + "; use list to see your tasks.");
        }
        return index - 1;
    }

    /**
     * Updates an existing task's completion status and prints a confirmation.
     *
     * @throws KingException If the task number is invalid.
     */
    private static void updateTaskStatus(List<Task> tasks, String indexText, boolean isDone) throws KingException {
        Task task = tasks.get(parseTaskIndex(indexText, tasks.size()));
        if (isDone) {
            task.markAsDone();
            System.out.println("    Nice! I've marked this task as done:");
        } else {
            task.markAsNotDone();
            System.out.println("    OK, I've marked this task as not done yet:");
        }
        System.out.print("      ");
        printTask(0, task);
    }

    /**
     * Removes an existing task, closes the gap in the list, and reports the new count.
     *
     * @throws KingException If the task number is invalid.
     */
    private static void deleteTask(List<Task> tasks, String indexText) throws KingException {
        Task removedTask = tasks.remove(parseTaskIndex(indexText, tasks.size()));
        System.out.println("    By your command, I've removed this task:");
        System.out.print("      ");
        printTask(0, removedTask);
        System.out.println("    Now you have " + tasks.size() + " task(s) in the list.");
    }

    /**
     * Prints an added task and the updated task count.
     */
    private static void printAddedTask(Task task, int taskCount) {
        System.out.println("    Got it. I've added this task:");
        System.out.print("      ");
        printTask(0, task);
        System.out.println("    Now you have " + taskCount + " task(s) in the list.");
    }
}
