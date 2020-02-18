package dice_game;

/**
 * Represents a player of the game
 */
public class Player extends Thread {

    private final int number;
    private final Game curGame;

    private int wins;
    private int points;

    public Player(int number, Game game) {
        this.number = number;
        this.curGame = game;
        wins = 0;
        points = 0;
    }

    public void increaseWins() {
        wins++;
    }

    public int getPoints() {
        return points;
    }

    public int getNumOfWins() {
        return wins;
    }

    @Override
    public void run() {

        synchronized (curGame) {
            try {

                while (curGame.isGameInProcess()) {
                    curGame.markReady();
                    curGame.notifyAll();
                    System.out.println("wait " + number);
                    while (!curGame.canPlayerMove())
                        curGame.wait();

                    points = curGame.makeAMove(this);
                    while (curGame.isRoundStarted()) {
                        curGame.wait();
                        System.out.println("waked "+number);
                    }
                }
            } catch (InterruptedException ex) {
                System.out.println("Interrupted");
            }
            System.out.println("leave");
        }
    }

    @Override
    public String toString() {
        return String.format("Player%d", number);
    }
}
