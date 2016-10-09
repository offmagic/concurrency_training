package training.concurrency.ex_2;

public class BlockingStackImpl<V> implements BlockingStack<V> {

    private int cur;
    private Object[] values;

    private final Object notFull = new Object();
    private final Object notEmpty = new Object();

    public BlockingStackImpl(int size) {
        this.values = new Object[size];
    }

    public void push(V v) throws InterruptedException {
        synchronized (notFull) {
            synchronized (notEmpty) {
                while (cur == values.length) {
                    notFull.wait();
                }
                values[cur++] = v;
                notEmpty.notify();
            }
        }
    }

    @SuppressWarnings("unckecked")
    public V pop() throws InterruptedException {
        synchronized (notFull) {
            synchronized (notEmpty) {
                while (cur == 0) {
                    notEmpty.wait();
                }
                V v = (V) values[cur--];
                notFull.notify();
                return v;
            }
        }
    }

}
