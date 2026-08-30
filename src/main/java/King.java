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

    /** Maximum number of tasks King can store (the spec caps it at 100). */
    private static final int MAX_TASKS = 100;

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
            } else {
                // Treat any other text as a new task to store.
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println("    added: " + command);
            }
            System.out.println(LINE);
        }
        console.close();
    }

    /**
     * Prints all stored tasks as a numbered list, indented to match the chat frame.
     * Each task is shown with a done-state icon: {@code [X]} if done, {@code [ ]} if not.
     *
     * @param tasks     the array holding the stored tasks
     * @param taskCount how many of the tasks in the array are in use
     */
    private static void printTasks(Task[] tasks, int taskCount) {
        System.out.println("    Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.printf("    %d. [%s] %s%n", i + 1,
                    tasks[i].getStatusIcon(), tasks[i].getDescription());
        }
    }

    /**
     * Marks the task named in the command as done or not done, and prints a
     * confirmation. The command is expected to look like {@code "mark 2"} or
     * {@code "unmark 2"}: anything after the first word is the 1-based index.
     *
     * @param tasks      the array holding the stored tasks
     * @param taskCount  how many of the tasks in the array are in use
     * @param command    the full command typed by the user, e.g. "mark 2"
     * @param markAsDone true to mark the task done, false to mark it not done
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
        Task task = tasks[index - 1];
        if (markAsDone) {
            task.markAsDone();
            System.out.println("    Nice! I've marked this task as done:");
        } else {
            task.markAsNotDone();
            System.out.println("    OK, I've marked this task as not done yet:");
        }
        System.out.printf("      [%s] %s%n", task.getStatusIcon(), task.getDescription());
    }

    /**
     * Checks whether the given text is a non-empty string of digits,
     * i.e. a usable task index.
     *
     * @param text the text to check
     * @return true if the text consists solely of digits
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
