package minesweeper.domain.timer;

public interface ITimer {
    void start();
    void stop();
    void reset();
    long getElapsed();
}
