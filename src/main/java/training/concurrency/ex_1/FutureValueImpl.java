package training.concurrency.ex_1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static training.concurrency.utils.LogMessageTools.getNameCurrentThread;

/**
 * Implementation {@link FutureValue} interface.
 *
 * @param <V> type of future value
 */
public class FutureValueImpl<V> implements FutureValue<V> {

    /**
     * Logger.
     */
    private static final Logger log = LogManager.getLogger(FutureValueImpl.class);

    /**
     * Lock.
     */
    private final Lock lock;

    /**
     * Condition for check, wait whether value already set.
     */
    private final Condition setValueCondition;

    /**
     * Whether value already filled
     */
    private volatile boolean set = false;

    /**
     * Value.
     */
    private V value = null;

    /**
     * Constructor.
     */
    public FutureValueImpl() {
        this.lock = new ReentrantLock();
        this.setValueCondition = lock.newCondition();
    }

    @Override
    public boolean trySet(V v) {
        if (set) {
            log.debug("Thread {}. Value already filled", getNameCurrentThread());
            return false;
        } else {
            lock.lock();
            try {
                if (set) {
                    log.debug("Thread {}. Value already filled (lock section)", getNameCurrentThread());
                    return false;
                } else {
                    value = v;
                    set = true;
                    setValueCondition.signalAll();
                    log.debug("Thread {}. Fill value.", getNameCurrentThread());
                    return true;
                }
            } finally {
                log.debug("Thread {}. Unlock in trySet method.", getNameCurrentThread());
                lock.unlock();
            }
        }
    }

    @Override
    public V get() throws InterruptedException {
        if (!set) {
            lock.lock();
            try {
                while (!set) {
                    log.debug("Thread {}. Wait until value has been filled.", getNameCurrentThread());
                    setValueCondition.await();
                }
            } finally {
                log.debug("Thread {}. Unlock in get method.", getNameCurrentThread());
                lock.unlock();
            }
        }
        log.debug("Thread {}. Return value: {}", getNameCurrentThread(), value);
        return value;
    }
}
