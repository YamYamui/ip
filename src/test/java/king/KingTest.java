package king;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Exercises King's command loop using scripted conversations without external libraries.
 */
public class KingTest {

    /**
     * Runs regression tests and fails with an AssertionError if a check fails.
     */
    public static void main(String[] args) {
        commands_invalidInput_recoversWithoutAddingTasks();
        deadline_invalidFields_rejectsTask();
        event_invalidFields_rejectsTask();
        taskStatus_invalidIndex_preservesState();
        commands_validInput_preservesLevelFourBehavior();
        taskList_overOneHundred_growsDynamically();
        delete_validIndices_removesAndRenumbersTasks();
        delete_invalidIndices_preservesTasks();
        input_endOfStream_exitsCleanly();
        bye_validCommand_stopsReading();
        System.out.println("All 10 King regression tests passed.");
    }

    private static void commands_invalidInput_recoversWithoutAddingTasks() {
        String output = runConversation("todo\n   \nblah\nlist extra\nbye extra\ntodo read a book\nlist\nbye\n");
        assertOccurrences(output, "My subject,", 5);
        assertContains(output, "a todo needs a description");
        assertContains(output, "please enter a command");
        assertContains(output, "I do not recognize 'blah'");
        assertContains(output, "'list' takes no arguments");
        assertContains(output, "'bye' takes no arguments");
        assertContains(output, "1. [T][ ] read a book");
        assertOccurrences(output, "I've added this task", 1);
        assertContains(output, "Bye. Hope to see you again soon!");
    }

    private static void deadline_invalidFields_rejectsTask() {
        String[] commands = {
            "deadline", "deadline return book", "deadline /by Friday",
            "deadline return book /by", "deadline return book /by Friday /by Saturday"
        };
        String output = runConversation(String.join("\n", commands) + "\ntodo safe\nlist\nbye\n");
        assertOccurrences(output, "My subject,", commands.length);
        assertContains(output, "a deadline needs a description");
        assertContains(output, "a deadline needs a date/time after /by");
        assertOccurrences(output, "I've added this task", 1);
        assertContains(output, "1. [T][ ] safe");
    }

    private static void event_invalidFields_rejectsTask() {
        String[] commands = {
            "event", "event meeting", "event meeting /from Monday",
            "event /from Monday /to Tuesday", "event meeting /from /to Tuesday",
            "event meeting /from Monday /to", "event meeting /to Tuesday /from Monday",
            "event meeting /from Monday /from Tuesday /to Wednesday",
            "event meeting /from Monday /to Tuesday /to Wednesday",
            "event meeting /to Sunday /from Monday /to Tuesday"
        };
        String output = runConversation(String.join("\n", commands) + "\ntodo safe\nlist\nbye\n");
        assertOccurrences(output, "My subject,", commands.length);
        assertContains(output, "an event needs a description");
        assertContains(output, "an event needs a start time after /from");
        assertContains(output, "an event needs an end time after /to");
        assertOccurrences(output, "I've added this task", 1);
        assertContains(output, "1. [T][ ] safe");
    }

    private static void taskStatus_invalidIndex_preservesState() {
        String[] invalidIndices = {"", "abc", "1.5", "1 2", "999999999999999999999", "0", "-1", "2"};
        StringBuilder commands = new StringBuilder("mark 1\nunmark 1\ntodo safe\nmark 1\n");
        for (String index : invalidIndices) {
            commands.append("unmark ").append(index).append('\n');
        }
        commands.append("list\nunmark 1\n");
        for (String index : invalidIndices) {
            commands.append("mark ").append(index).append('\n');
        }
        commands.append("list\nbye\n");
        String output = runConversation(commands.toString());
        assertOccurrences(output, "My subject,", 2 + 2 * invalidIndices.length);
        assertOccurrences(output, "your list is empty", 2);
        assertContains(output, "1. [T][X] safe");
        assertContains(output, "1. [T][ ] safe");
    }

    private static void commands_validInput_preservesLevelFourBehavior() {
        String output = runConversation("  TODO\tread a book  \n"
                + "DeAdLiNe\treturn book\t/by\tFriday evening\n"
                + "EVENT meeting /from Monday 2pm /to Monday 4pm\n"
                + "todo visit /bypass\nmark\t2\nlist\nunmark 2\nlist\nBYE\n");
        assertOccurrences(output, "My subject,", 0);
        assertContains(output, "1. [T][ ] read a book");
        assertContains(output, "2. [D][X] return book (by: Friday evening)");
        assertContains(output, "2. [D][ ] return book (by: Friday evening)");
        assertContains(output, "3. [E][ ] meeting (from: Monday 2pm to: Monday 4pm)");
        assertContains(output, "4. [T][ ] visit /bypass");
        assertOccurrences(output, "I've added this task", 4);
    }

    private static void taskList_overOneHundred_growsDynamically() {
        StringBuilder commands = new StringBuilder();
        for (int i = 1; i <= 100; i++) {
            commands.append("todo task ").append(i).append('\n');
        }
        commands.append("todo overflow\ndeadline overflow /by Friday\n"
                + "event overflow /from Monday /to Tuesday\nblah\nmark 100\nlist\nbye\n");
        String output = runConversation(commands.toString());
        assertOccurrences(output, "I've added this task", 103);
        assertOccurrences(output, "My subject,", 1);
        assertContains(output, "100. [T][X] task 100");
        assertContains(output, "103. [E][ ] overflow (from: Monday to: Tuesday)");
        assertContains(output, "Bye. Hope to see you again soon!");
    }

    private static void delete_validIndices_removesAndRenumbersTasks() {
        String output = runConversation("todo first\ndeadline middle /by Friday\n"
                + "event last /from noon /to night\nmark 2\ndelete 2\nlist\n"
                + "delete 2\nDELETE\t1\nlist\ntodo new\nlist\nbye\n");
        assertOccurrences(output, "My subject,", 0);
        assertOccurrences(output, "I've removed this task", 3);
        assertContains(output, "[D][X] middle (by: Friday)");
        assertContains(output, "2. [E][ ] last (from: noon to: night)");
        assertContains(output, "Now you have 0 task(s)");
        assertContains(output, "1. [T][ ] new");
    }

    private static void delete_invalidIndices_preservesTasks() {
        String output = runConversation("delete 1\ntodo safe\ndelete\ndelete abc\ndelete 1.5\n"
                + "delete 0\ndelete -1\ndelete 2\ndelete 999999999999999999\ndelete 1 2\nlist\nbye\n");
        assertOccurrences(output, "My subject,", 9);
        assertOccurrences(output, "I've removed this task", 0);
        assertContains(output, "1. [T][ ] safe");
    }

    private static void input_endOfStream_exitsCleanly() {
        assertContains(runConversation(""), "Hello, my subject.");
        assertContains(runConversation("todo unfinished input"), "Now you have 1 task(s)");
        assertContains(runConversation("blah"), "I do not recognize 'blah'");
    }

    private static void bye_validCommand_stopsReading() {
        String output = runConversation("bye\ntodo ignored\n");
        assertContains(output, "Bye. Hope to see you again soon!");
        assertOccurrences(output, "I've added this task", 0);
    }

    /**
     * Runs a fresh chatbot session and restores the process streams afterward.
     */
    private static String runConversation(String input) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(capturedOutput);
            King.main(new String[0]);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    private static void assertContains(String output, String expected) {
        if (!output.contains(expected)) {
            throw new AssertionError("Missing output: " + expected + "\nActual output:\n" + output);
        }
    }

    private static void assertOccurrences(String output, String expected, int expectedCount) {
        int count = 0;
        int offset = 0;
        while ((offset = output.indexOf(expected, offset)) >= 0) {
            count++;
            offset += expected.length();
        }
        if (count != expectedCount) {
            throw new AssertionError("Expected " + expectedCount + " occurrences of '" + expected
                    + "' but found " + count + "\nActual output:\n" + output);
        }
    }
}
