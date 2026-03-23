package minesweeper.domain.timer;

public enum GameTimer implements ITimer {
    INSTANCE;

    private long startTime;
    private long elapsedBeforeStop;
    private boolean running;

    @Override
    public void start() {
        if (running) {
            return;
        }

        startTime = System.currentTimeMillis();
        running = true;
    }

    @Override
    public void stop() {
        if (!running) {
            return;
        }

        elapsedBeforeStop += System.currentTimeMillis() - startTime;
        running = false;
    }

    @Override
    public void reset() {
        startTime = 0;
        elapsedBeforeStop = 0;
        running = false;
    }

    @Override
    public long getElapsed() {
        long elapsed = elapsedBeforeStop;
        if (running) {
            elapsed += System.currentTimeMillis() - startTime;
        }

        return elapsed / 1000;
    }
}
