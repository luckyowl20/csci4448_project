package minesweeper;

import minesweeper.game.GameStatsObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameStatsObserverTest {
    private GameStatsObserver observer;

    @BeforeEach
    void setUp() {
        observer = new GameStatsObserver();
    }

    @Test
    void gameLostIsInitiallyFalse() {
        assertFalse(observer.isGameLost());
    }

    @Test
    void onGameLostSetsGameLostTrue() {
        observer.onGameLost();
        assertTrue(observer.isGameLost());
    }

    @Test
    void onGameStartedResetsGameLost() {
        observer.onGameLost();
        observer.onGameStarted();
        assertFalse(observer.isGameLost());
    }

    @Test
    void onGameStartedResetsAllStats() {
        observer.onCellRevealed(0, 0);
        observer.onFlagPlaced(0, 0);
        observer.onGameLost();
        observer.onGameStarted();
        assertEquals(0, observer.getRevealedCells());
        assertEquals(0, observer.getFlagsPlaced());
        assertFalse(observer.isGameLost());
    }
}