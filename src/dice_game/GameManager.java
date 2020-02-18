package dice_game;

import java.util.Scanner;

public class GameManager {

    private static final Scanner scanner = new Scanner(System.in);
    private static final int MAX_PLAYERS = 6;
    private static final int MAX_DICE = 5;
    private static final int MAX_ROUNDS = 100;

    private static final int MIN_DICE_OR_PLAYERS = 2;
    private static final int MIN_ROUNDS = 1;

    public static void main(String[] args) {
        try {

            if (args.length != 3) {
                System.out.println("Invalid number of parameters. ");
                return;
            }

            Game currentGame;

            final int players = getGameParameter(MIN_DICE_OR_PLAYERS, MAX_PLAYERS, "players",
                    args[0]);
            final int dice = getGameParameter(MIN_DICE_OR_PLAYERS, MAX_DICE, "dice", args[1]);
            final int wins = getGameParameter(MIN_ROUNDS, MAX_ROUNDS, "rounds", args[2]);

            do {

                currentGame = new Game(players, dice, wins);

                System.out.println("\n\nWelcome to the Dice Game!\n");
                currentGame.start();

                currentGame.join();
                Thread.sleep(2000);

                System.out.println("\nPrint 'e' to exit, any other key to play again.");

            } while (!scanner.next().equals("e"));
        } catch (InterruptedException ex) {
            System.out.println("The game was stopped.");
        }
    }

    /**
     * Checks input parameters for correctness
     *
     * @param min     min value for the input
     * @param max     max value for the input
     * @param message message to show
     * @return parameter got from the input
     */
    static int getGameParameter(int min, int max, String message, String parameter) {

        int res = -1;
        while (res == -1) {
            try {
                res = Integer.parseInt(parameter);

                if (res < min || res > max) {
                    res = -1;
                    throw new NumberFormatException();
                }

            } catch (NumberFormatException ex) {
                System.out.println(String.format("Invalid number of %s. Enter a positive integer in [%d, %d]:",
                        message, min, max));
                parameter = scanner.nextLine();
            }
        }
        return res;
    }
}
