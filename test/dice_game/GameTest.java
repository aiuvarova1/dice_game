package dice_game;

import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sorry, I don't know what to test here
 **/
class GameTest {

    private Game game;

    @Test
    void gameTest1() throws InterruptedException {
        game = new Game(2, 2, 2);
        game.start();

        waitFor(Game::isGameInProcess);
        waitFor(Game::isRoundStarted);

        Thread.sleep(10000);
        waitFor(game -> !game.isGameInProcess());
    }

    @Test
    void gameTest2() throws InterruptedException {
        game = new Game(2, 1, 1);

        assertFalse(game.isGameInProcess());
        game.start();

        waitFor(Game::isGameInProcess);
        waitFor(Game::isRoundStarted);

        waitFor(game -> !game.isGameInProcess());
    }

    @Test
    void playerTest() {
        Player player = new Player(1, game);
        assertEquals(0, player.getNumOfWins());

        player.increaseWins();
        player.increaseWins();
        assertEquals(2, player.getNumOfWins());
        assertEquals("Player1", player.toString());
    }

    @Test
    void gameManagerTest() {

        assertEquals(2, GameManager.getGameParameter(0, 2, "", "2"));
        assertEquals(1, GameManager.getGameParameter(-3, 12, "", "1"));
    }


    private void waitFor(Predicate<Game> predicate) throws InterruptedException {

        for (int i = 0; i < 1000; i++) {
            if (predicate.test(game))
                return;
            Thread.sleep(10);
        }
        throw new AssertionError("Fail");
    }
}