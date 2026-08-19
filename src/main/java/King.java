/**
 * Entry point for the King chatbot.
 *
 * <p>Prints the banner, greets the user, and prints a farewell before exiting.
 */
public class King {

    /** Line of underscores used to frame the chatbot's messages. */
    private static final String LINE = "____________________________________________________________\n";

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

        // Bid the user farewell and exit.
        System.out.println(LINE);
        System.out.println("Farewell, my friend. May your days be prosperous.");
        System.out.println("Hope to see you again soon!");
        System.out.println(LINE);
    }
}
