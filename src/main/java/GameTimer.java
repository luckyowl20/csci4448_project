public enum GameTimer implements ITimer {
    INSTANCE;

    private long startTime;
    private boolean running;

    @Override
    public void start() {
        startTime = System.currentTimeMillis();
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void reset() {
        startTime = 0;
        running = false;
    }

    @Override
    public long getElapsed() {
        return running ? (System.currentTimeMillis() - startTime) / 1000 : 0;
    }
}
