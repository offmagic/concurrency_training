package training.concurrency.ex_2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static training.concurrency.utils.LogMessageTools.getNameCurrentThread;

/**
 * * Implementation {@link BlockingStack} interface.
 *
 * @param <V> type of stack elements
 */
public class BlockingStackImpl<V> implements BlockingStack<V> {

    /**
     * Logger.
     */
    private static final Logger log = LogManager.getLogger(BlockingStackImpl.class);

    /**
     * Count elements in stack.
     */
    private int countElementsInStack;

    /**
     * Container for saving stack values.
     */
    private Object[] values;

    /**
     * Lock.
     */
    private final Lock lock;

    /**
     * Not full condition.
     */
    private final Condition notFullCondition;


    /**
     * Not empty condition.
     */
    private final Condition notEmptyCondition;


    /**
     * Constructor.
     *
     * @param size stack size
     */
    public BlockingStackImpl(int size) {
        lock = new ReentrantLock();
        notFullCondition = lock.newCondition();
        notEmptyCondition = lock.newCondition();
        this.values = new Object[size];
        countElementsInStack = 0;
    }

    @Override
    public void push(V v) throws InterruptedException {
        log.debug("Thread {}. Try to push element: {}", getNameCurrentThread(), v);
        lock.lock();
        try {
            while (countElementsInStack == values.length) {
                log.debug("Thread {}. Stack is full. Wait.", getNameCurrentThread());
                notFullCondition.await();
            }
            log.debug("Thread {}. Stack isn't full. Push new element and notify other thread, that now there is element in stack.",
                    getNameCurrentThread());
            values[countElementsInStack++] = v;
            notEmptyCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("unckecked")
    public V pop() throws InterruptedException {
        lock.lock();
        try {
            while (countElementsInStack == 0) {
                log.debug("Thread {}. Stack is empty. Wait.", getNameCurrentThread());
                notEmptyCondition.await();
            }
            log.debug("Thread {}. Stack isn't empty. Pop element and notify other thread, that now there is place for pushing",
                    getNameCurrentThread());
            V v = (V) values[--countElementsInStack];
            notFullCondition.signal();
            return v;
        } finally {
            lock.unlock();
        }
    }
}
