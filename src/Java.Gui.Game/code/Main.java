import javafx.application.Application;

/**
 * Path to the directory containing country data files for WordGame.
 * This directory must contain valid country-capital files expected by WordGame loader.
 */
private static final String COUNTRY_DATA_PATH = "Term_Project_AP/src/bcit.comp2522.termproject/code/data";


/**
 * Entry point for the term project. Displays a numeric main menu for launching available games.
 * The user can select:
 * - 1: WordGame (console-based trivia using country-capital data)
 * - 2: NumberGame (JavaFX logic/guessing game)
 * - 3: MyGame (JavaFX combat/stat-based custom game)
 * - 4: Quit the program
 * Demonstrates:
 * - File I/O for game data
 * - JavaFX launching
 * - Menu-based user interaction through the console
 * Note: Console I/O (System.out) is permitted in this context as the interface is text-based.
 *
 * @author Aleksandar Panich
 */
public static void main(final String[] args) {
    final Scanner scanner = new Scanner(System.in);
    boolean running = true;

    while (running) {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("W - Play WordGame");
        System.out.println("N - Play NumberGame");
        System.out.println("M - Play MyGame");
        System.out.println("Q - Quit");

        String input;
        while (true) {
            System.out.print("Enter your choice: ");
            input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("W") || input.equalsIgnoreCase("N") ||
                    input.equalsIgnoreCase("M") || input.equalsIgnoreCase("Q")) {
                break;
            }

            System.out.println("Invalid input. Please enter W, N, M, or Q.");
        }

        switch (input.toUpperCase()) {
            case "W":
                try {
                    final List<Country> countries = WordGame.loadCountriesFromDirectory(COUNTRY_DATA_PATH);
                    WordGame.playGame(countries);
                } catch (final IOException e) {
                    // Error occurred while reading files (e.g., file missing or unreadable)
                    System.out.println("Error loading country data: " + e.getMessage());
                }
                break;

            case "N":
                // Launches JavaFX-based number game
                Application.launch(NumberGame.class, args);
                break;

            case "M":
                // Launches the main custom game (JavaFX) and exits the menu loop
                Application.launch(MyGame.class, args);
                running = false;
                break;

            case "Q":
                // Exits the menu loop and terminates the program
                running = false;
                System.out.println("Thanks for playing! Goodbye.");
                break;

            default:
                // Should never occur due to input validation above
                System.out.println("Unexpected error: unrecognized menu selection.");
        }
    }

    scanner.close();
}