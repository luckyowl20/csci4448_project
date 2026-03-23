package minesweeper;

import minesweeper.domain.timer.GameTimer;
import minesweeper.domain.timer.ITimer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameTimerTest {

    @BeforeEach
    void reset() {
        GameTimer.INSTANCE.reset();
    }

    @Test
    void testSingletonSameInstance() {
        ITimer a = GameTimer.INSTANCE;
        ITimer b = GameTimer.INSTANCE;
        assertSame(a, b);
    }

    @Test
    void testElapsedZeroBeforeStart() {
        assertEquals(0, GameTimer.INSTANCE.getElapsed());
    }

    @Test
    void testTimerRunsAfterStart() throws InterruptedException {
        GameTimer.INSTANCE.start();
        Thread.sleep(1100);
        assertTrue(GameTimer.INSTANCE.getElapsed() > 0);
    }

    @Test
    void testTimerStopsAfterStop() throws InterruptedException {
        GameTimer.INSTANCE.start();
        Thread.sleep(1100);
        GameTimer.INSTANCE.stop();
        long elapsed = GameTimer.INSTANCE.getElapsed();
        Thread.sleep(1100);
        assertEquals(elapsed, GameTimer.INSTANCE.getElapsed());
    }

    @Test
    void testResetSetsElapsedToZero() throws InterruptedException {
        GameTimer.INSTANCE.start();
        Thread.sleep(1100);
        GameTimer.INSTANCE.reset();
        assertEquals(0, GameTimer.INSTANCE.getElapsed());
    }
}
