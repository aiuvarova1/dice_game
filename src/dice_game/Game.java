package dice_game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

/**
 * Represents an instance of a game with specified number of players, dice, wins
 * Also plays the role of a commentator
 **/
public class Game extends Thread {

    private final int numOfPlayers;
    private final int numOfDice;
    private final int numOfRounds;
    private final ArrayList<Player> players;
    private static final Random rand = new Random();

    private boolean roundStarted = false;

    private Player curPlayer;
    private Player gameLeader;
    private Player leader = null;

    private boolean canMakeAMove = false;
    private boolean gameInProcess = false;

    private int readyPlayers = 0;

    public Game(int numOfPlayers, int dice, int rounds) {
        this.numOfPlayers = numOfPlayers;
        this.numOfDice = dice;
        this.numOfRounds = rounds;

        players = new ArrayList<>(numOfPlayers);
        for (int i = 0; i < numOfPlayers; i++)
            players.add(new Player(i + 1, this));
    }

    public boolean canPlayerMove() {
        return canMakeAMove;
    }

    public boolean isGameInProcess() {
        return gameInProcess;
    }

    public boolean isRoundStarted() {
        return roundStarted;
    }

    /**
     * Counts players which are ready for a game
     */
    public void markReady() {
        readyPlayers++;
    }

    /**
     * Whether one of the players rolled maximum points
     *
     * @return is there a maximum or not
     */
    public boolean isSenseToRoll() {
        return !(leader != null && leader.getPoints() == numOfDice * 6);
    }

    /**
     * Starts the game
     */
    @Override
    public void run() {

        System.out.println(String.format("The number of players - %d, the number of dice - %d, " +
                "the number of wins - %d",numOfPlayers,numOfDice,numOfRounds));
        System.out.println("The game starts now!");
        gameInProcess = true;

        for (Player p : players)
            p.start();

        try {
            for (int i = 1; gameInProcess; i++) {
                startRound(i);
                System.out.println("left");
            }
        } catch (InterruptedException ex) {
            System.out.println("The game was interrupted.");
        }
//        try {
//            synchronized (this) {
//                System.out.println("i wait");
//                //wait();
//            }
//        }catch (InterruptedException ex)
//        {
//
//        }
    }

    /**
     * With this method players make moves
     *
     * @param player player that makes a move
     * @return points rolled
     */
    int makeAMove(Player player) {
        int points = 0;
        if (isSenseToRoll()) {
            for (int i = 0; i < numOfDice; i++)
                points += 1 + rand.nextInt(6);
            curPlayer = player;
        }

        canMakeAMove = false;
        notifyAll();

        return points;
    }

    /**
     * Represents one game round
     *
     * @param round number of round
     * @throws InterruptedException if sth went wrong
     */
    synchronized private void startRound(int round) throws InterruptedException {
        waitForPlayers();
        leader = null;
        //Thread.sleep(1000);
        roundStarted = true;

        System.out.println("\n========================================");
        System.out.println(String.format("Round %d!", round));

        for (int i = 0; i < numOfPlayers; i++) {
            canMakeAMove = true;
            notifyAll();
            wait();

            if (!isSenseToRoll())
                continue;

          //  Thread.sleep(1500);

            if (leader == null || leader.getPoints() < curPlayer.getPoints())
                leader = curPlayer;

            if (curPlayer.getPoints() == numOfDice * 6) {
                System.out.println(String.format("%s rolled %s points! It is maximum, the round is over.",
                        curPlayer, curPlayer.getPoints()));
                continue;
            }

            System.out.println(String.format("%s rolled %d points.", curPlayer, curPlayer.getPoints()));
            if (i != numOfPlayers - 1)
                System.out.println(String.format("Current leader is %s, he has %d points.", leader, leader.getPoints()));

        }

        Objects.requireNonNull(leader);

        leader.increaseWins();
        if (gameLeader == null || leader.getNumOfWins() > gameLeader.getNumOfWins())
            gameLeader = leader;

        Thread.sleep(1500);
        if (gameLeader.getNumOfWins() == numOfRounds) {
            gameInProcess = false;
            System.out.println(String.format("\nThe winner is %s. Congratulations!", gameLeader));
            printFinalTable();
        } else {
            System.out.println(String.format("\nThe winner of %d round is %s. He has %d wins now.",
                    round, leader, leader.getNumOfWins()));
            System.out.println(String.format("The leader of the game is %s with %d wins.",
                    gameLeader, gameLeader.getNumOfWins()));
        }

        roundStarted = false;
        readyPlayers = 0;
    }

    /**
     * The game instance wait till all the players are ready for the round
     *
     * @throws InterruptedException if sth went wrong
     */
    private void waitForPlayers() throws InterruptedException {
        notifyAll();
        while (readyPlayers != numOfPlayers) {
            System.out.println("have to wait");
            this.wait();
        }

    }

    /**
     * Prints a table with final results of the game
     */
    private void printFinalTable() {
        players.sort(Comparator.comparingInt(Player::getNumOfWins).reversed());
        System.out.println("Final scores:");
        for (Player p : players)
            System.out.println(p + "   " + p.getNumOfWins());
    }

}
