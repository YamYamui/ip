import java.util.Scanner;

/**
 * Entry point for the King chatbot.
 *
 * <p>Prints the banner, greets the user, then reads commands in a loop.
 * Any free-form text is stored as an item and confirmed with "added: ...";
 * the command {@code list} prints all stored items numbered in order.
 * Exits when the user types {@code bye}.
 */
public class King {

    /** Line of underscores used to frame the chatbot's messages. */
    private static final String LINE = "____________________________________________________________\n";

    /** The command that ends the conversation. */
    private static final String BYE_COMMAND = "bye";

    /** The command that prints all stored items. */
    private static final String LIST_COMMAND = "list";

    /** Maximum number of items King can store (the spec caps it at 100). */
    private static final int MAX_ITEMS = 100;

    public static void main(String[] args) {
        String banner = "    __    _          \n"
                + "   / /__ (_)__  ___ _\n"
                + "  /  '_// / _ \\/ _ `/\n"
                + " /_/\\_\\/_/_//_/\\_, / \n"
                + "              /___/\n";

        // Storage for the items the user adds. The spec guarantees there will
        // never be more than 100, so a fixed-size array is enough.
        // itemCount tracks how many slots of the array are actually in use.
        String[] items = new String[MAX_ITEMS];
        int itemCount = 0;

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
            if (command.equalsIgnoreCase(BYE_COMMAND)) {
                System.out.println("    Bye. Hope to see you again soon!");
                break;
            } else if (command.equalsIgnoreCase(LIST_COMMAND)) {
                // Show everything stored so far.
                printItems(items, itemCount);
            } else {
                // Treat any other text as a new item to store.
                items[itemCount] = command;
                itemCount++;
                System.out.println("    added: " + command);
            }
            System.out.println(LINE);
        }
        console.close();
    }

    /**
     * Prints all stored items as a numbered list, indented to match the chat frame.
     *
     * @param items     the array holding the stored items
     * @param itemCount how many of the items in the array are in use
     */
    private static void printItems(String[] items, int itemCount) {
        for (int i = 0; i < itemCount; i++) {
            System.out.printf("    %d. %s%n", i + 1, items[i]);
        }
    }
}
