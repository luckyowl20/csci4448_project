public interface ITimer {
    void start();
    void stop();
    void reset();
    long getElapsed();
}
