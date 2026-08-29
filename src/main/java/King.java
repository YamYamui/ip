import java.util.Scanner;

/**
 * Entry point for the King chatbot.
 *
 * <p>Prints the banner, greets the user, then reads commands in a loop,
 * echoing each one back to the user. Exits when the user types {@code bye}.
 */
public class King {

    /** Line of underscores used to frame the chatbot's messages. */
    private static final String LINE = "____________________________________________________________\n";

    /** The command that ends the conversation. */
    private static final String BYE_COMMAND = "bye";

    public static void main(String[] args) {
        String banner = "    __    _          \n"
                + "   / /__ (_)__  ___ _\n"
                + "  /  '_// / _ \\/ _ `/\n"
                + " /_/\\_\\/_/_//_/\\_, / \n"
                + "              /___/\n";

        // Greet the user in King's regal tone.
        System.out.println(LINE);
        System.out.print(banner);
        System.out.println("Hello, my subject. I am King, your faithful chatbot.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        // Read commands from the user, echoing each one, until they type "bye".
        Scanner console = new Scanner(System.in);
        while (true) {
            String command = console.nextLine().trim();
            System.out.println(LINE);
            if (command.equalsIgnoreCase(BYE_COMMAND)) {
                System.out.println("    Bye. Hope to see you again soon!");
                break;
            }
            // Echo the command back, showing that King received it.
            System.out.println("    " + command);
            System.out.println(LINE);
        }
        console.close();
    }
}
