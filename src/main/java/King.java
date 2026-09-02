import java.util.Scanner;

/**
 * Entry point for the King chatbot.
 *
 * <p>Prints the banner, greets the user, then reads commands in a loop.
 * Any free-form text is stored as a task and confirmed with "added: ...";
 * the command {@code list} prints all stored tasks numbered in order,
 * with a checkbox-style icon showing which are done.
 * The commands {@code mark <index>} and {@code unmark <index>} toggle a
 * task's done state. Exits when the user types {@code bye}.
 */
public class King {

    /** Line of underscores used to frame the chatbot's messages. */
    private static final String LINE = "____________________________________________________________\n";

    /** The command that ends the conversation. */
    private static final String BYE_COMMAND = "bye";

    /** The command that prints all stored tasks. */
    private static final String LIST_COMMAND = "list";

    /** The command that marks a task as done, followed by the task's 1-based index. */
    private static final String MARK_COMMAND = "mark";

    /** The command that marks a task as not done, followed by the task's 1-based index. */
    private static final String UNMARK_COMMAND = "unmark";

    /** The command to add a ToDo task, followed by the task description. */
    private static final String TODO_COMMAND = "todo";

    /** The command to add a Deadline task, followed by the task description and /by clause. */
    private static final String DEADLINE_COMMAND = "deadline";

    /** The command to add an Event task, followed by the task description and /from and /to clauses. */
    private static final String EVENT_COMMAND = "event";

    /** Maximum number of tasks King can store (the spec caps it at 100). */
    private static final int MAX_TASKS = 100;

    /**
     * Runs the King chatbot, reading commands until {@code bye} is typed.
     */
    public static void main(String[] args) {
        String banner = "    __    _          \n"
                + "   / /__ (_)__  ___ _\n"
                + "  /  '_// / _ \\/ _ `/\n"
                + " /_/\\_\\/_/_//_/\\_, / \n"
                + "              /___/\n";

        // Storage for the tasks the user adds. The spec guarantees there will
        // never be more than 100, so a fixed-size array is enough.
        // taskCount tracks how many slots of the array are actually in use.
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        // Greet the user in King's regal tone.
        System.out.println(LINE);
        System.out.print(banner);
        System.out.println("Hello, my subject. I am King, your faithful chatbot.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        // Read commands from the user until they type "bye".
        Scanner console = new Scanner(System.in);
        while (true) {
            String command = console.nextLine().trim();
            System.out.println(LINE);
            // The first word of the command decides how the rest is interpreted
            // (e.g. "mark 2" -> word "mark", index "2").
            String firstWord = command.split(" ", 2)[0];
            if (command.equalsIgnoreCase(BYE_COMMAND)) {
                System.out.println("    Bye. Hope to see you again soon!");
                break;
            } else if (command.equalsIgnoreCase(LIST_COMMAND)) {
                // Show everything stored so far.
                printTasks(tasks, taskCount);
            } else if (firstWord.equalsIgnoreCase(MARK_COMMAND)
                    || firstWord.equalsIgnoreCase(UNMARK_COMMAND)) {
                // Toggle the done state of the task at the given index.
                updateTaskStatus(tasks, taskCount, command,
                        firstWord.equalsIgnoreCase(MARK_COMMAND));
            } else if (firstWord.equalsIgnoreCase(TODO_COMMAND)) {
                // Add a ToDo task.
                String description = command.substring(TODO_COMMAND.length()).trim();
                if (description.isEmpty()) {
                    System.out.println("    Oops, the description of a todo cannot be empty.");
                } else {
                    tasks[taskCount] = new ToDo(description);
                    printAddedTask(tasks[taskCount]);
                    taskCount++;
                    System.out.println("    Now you have " + taskCount + " task(s) in the list.");
                }
            } else if (firstWord.equalsIgnoreCase(DEADLINE_COMMAND)) {
                // Add a Deadline task.
                addDeadline(tasks, taskCount, command);
                if (taskCount < MAX_TASKS && tasks[taskCount] != null) {
                    taskCount++;
                }
            } else if (firstWord.equalsIgnoreCase(EVENT_COMMAND)) {
                // Add an Event task.
                addEvent(tasks, taskCount, command);
                if (taskCount < MAX_TASKS && tasks[taskCount] != null) {
                    taskCount++;
                }
            } else {
                // Treat any other text as a new task to store.
                tasks[taskCount] = new ToDo(command);
                printAddedTask(tasks[taskCount]);
                taskCount++;
                System.out.println("    Now you have " + taskCount + " task(s) in the list.");
            }
            System.out.println(LINE);
        }
        console.close();
    }

    /**
     * Prints all stored tasks as a numbered list, indented to match the chat frame.
     * Each task is shown with a task-type icon ([T]/[D]/[E]), a done-state icon
     * ({@code [X]} if done, {@code [ ]} if not), and its description.
     *
     * @param tasks     the array holding the stored tasks.
     * @param taskCount how many of the tasks in the array are in use.
     */
    private static void printTasks(Task[] tasks, int taskCount) {
        System.out.println("    Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
                printTask(i + 1, tasks[i]);
        }
    }

    /**
     * Prints a task with its type, done state, and description.
     * The type, icon, and description are all resolved through the task's
     * own methods, so subclasses of {@link Task} control their own display.
     */
    private static void printTask(int index, Task task) {
        if (index > 0) {
            System.out.printf("    %d. ", index);
        }
        System.out.printf("[%s][%s] %s%n", task.getTaskType(), task.getStatusIcon(),
                task.getDescription());
    }

    /**
     * Marks the task named in the command as done or not done, and prints a
     * confirmation. The command is expected to look like {@code "mark 2"} or
     * {@code "unmark 2"}: anything after the first word is the 1-based index.
     *
     * @param tasks      the array holding the stored tasks.
     * @param taskCount  how many of the tasks in the array are in use.
     * @param command    the full command typed by the user, e.g. "mark 2".
     * @param markAsDone true to mark the task done, false to mark it not done.
     */
    private static void updateTaskStatus(Task[] tasks, int taskCount, String command,
            boolean markAsDone) {
        String[] parts = command.split(" ", 2);
        String indexText = parts.length > 1 ? parts[1].trim() : "";
        if (!isNumber(indexText)) {
            System.out.println("    Oops, I could not understand that command. Try e.g. \"mark 1\".");
            return;
        }
        int index = Integer.parseInt(indexText);
        if (index < 1 || index > taskCount) {
            System.out.println("    Oops! There is no task with that number in your list.");
            return;
        }
        if (markAsDone) {
            tasks[index - 1].markAsDone();
            System.out.println("    Nice! I've marked this task as done:");
        } else {
            tasks[index - 1].markAsNotDone();
            System.out.println("    OK, I've marked this task as not done yet:");
        }
        System.out.print("      ");
        printTask(0, tasks[index - 1]);
    }

    /**
     * Prints a task in the format: {@code [type][status] description}.
     *
     * @param task the task to print.
     */
    private static void printAddedTask(Task task) {
        System.out.println("    Got it. I've added this task:");
        System.out.print("      ");
        printTask(0, task);
    }

    /**
     * Parses and adds a Deadline task to the task list.
     * Expected format: {@code deadline <description> /by <deadline>}
     *
     * @param tasks     the array holding the stored tasks.
     * @param taskCount how many of the tasks in the array are in use.
     * @param command   the full command typed by the user.
     */
    private static void addDeadline(Task[] tasks, int taskCount, String command) {
        String content = command.substring(DEADLINE_COMMAND.length()).trim();
        String[] parts = content.split(" /by ");
        if (parts.length < 2) {
            System.out.println("    Oops, the format should be: deadline <description> /by <date/time>");
            return;
        }
        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty() || by.isEmpty()) {
            System.out.println("    Oops, the description or deadline cannot be empty.");
            return;
        }
        if (taskCount >= MAX_TASKS) {
            System.out.println("    Oops, the task list is full.");
            return;
        }
        tasks[taskCount] = new Deadline(description, by);
        printAddedTask(tasks[taskCount]);
        System.out.println("    Now you have " + (taskCount + 1) + " task(s) in the list.");
    }

    /**
     * Parses and adds an Event task to the task list.
     * Expected format: {@code event <description> /from <start> /to <end>}
     *
     * @param tasks     the array holding the stored tasks.
     * @param taskCount how many of the tasks in the array are in use.
     * @param command   the full command typed by the user.
     */
    private static void addEvent(Task[] tasks, int taskCount, String command) {
        String content = command.substring(EVENT_COMMAND.length()).trim();
        String[] parts = content.split(" /from ");
        if (parts.length < 2) {
            System.out.println("    Oops, the format should be: event <description> /from <start> /to <end>");
            return;
        }
        String description = parts[0].trim();
        String[] timeParts = parts[1].split(" /to ");
        if (timeParts.length < 2) {
            System.out.println("    Oops, the format should be: event <description> /from <start> /to <end>");
            return;
        }
        String from = timeParts[0].trim();
        String to = timeParts[1].trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            System.out.println("    Oops, the description, start time, or end time cannot be empty.");
            return;
        }
        if (taskCount >= MAX_TASKS) {
            System.out.println("    Oops, the task list is full.");
            return;
        }
        tasks[taskCount] = new Event(description, from, to);
        printAddedTask(tasks[taskCount]);
        System.out.println("    Now you have " + (taskCount + 1) + " task(s) in the list.");
    }

    /**
     * Checks whether the given text is a non-empty string of digits,
     * i.e. a usable task index.
     *
     * @param text the text to check.
     * @return true if the text consists solely of digits.
     */
    private static boolean isNumber(String text) {
        if (text.isEmpty()) {
            return false;
        }
        for (char c : text.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
