package training.concurrency.ex_1;

public class FutureValueImpl<V> implements FutureValue<V> {

    private volatile boolean set;

    private V value;

    public boolean trySet(V v) {
        if (set) {
            return false;
        } else {
            synchronized (this) {
                if (set) {
                    return false;
                } else {
                    value = v;
                    set = true;
                    notifyAll();
                    return true;
                }
            }
        }
    }

    public V get() throws InterruptedException {
        if (!set) {
            synchronized (this) {
                while (!set) {
                    wait();
                }
            }
        }
        return value;
    }

}
