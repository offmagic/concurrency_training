package training.concurrency.ex_3.cache;

import net.jcip.annotations.ThreadSafe;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Class for saving cached value.
 */
@ThreadSafe
public class LazyLoadingValue<V> {

    /**
     * Runnable task.
     */
    private final FutureTask<V> futureTask;

    /**
     * Expiration timeout.
     */
    private final long expirationTimeout;

    /**
     * Expiration date of value.
     */
    private volatile LocalDateTime expirationDate;

    /**
     * Constructor.
     *
     * @param valueCalculator   value calculator represented by class, which implements {@link Callable} interface.
     * @param expirationTimeout expiration timeout
     * @throws IllegalArgumentException if future task is null
     *                                  if expirationTimeout less or equals zero
     */
    public LazyLoadingValue(final Callable<V> valueCalculator, final long expirationTimeout) {
        if (valueCalculator == null) {
            throw new IllegalArgumentException("Value calculator is null.");
        }
        this.futureTask = new FutureTask<>(valueCalculator);

        if (expirationTimeout <= 0) {
            throw new IllegalArgumentException("Expiration timeout should be more than zero.");
        }
        this.expirationTimeout = expirationTimeout;
    }

    /**
     * Retrieve result of cached value.
     *
     * @return result of cached value
     * @throws ExecutionException   execution exception
     * @throws InterruptedException interrupted exception
     */
    public V retrieveResult() throws ExecutionException, InterruptedException {
        return futureTask.get();
    }

    /**
     * Calculate cached value.
     * This method is synchronized, because calculation should be executed only in one thread and only once.
     */
    public synchronized void calculate() {
        if (!futureTask.isDone()) {
            futureTask.run();
            this.expirationDate = LocalDateTime.now().plusSeconds(expirationTimeout);
        }
    }

    /**
     * Returns {@code true} if this cached value expired.
     *
     * @return whether cached value expired
     */
    public boolean isValueExpired() {
        return expirationDate != null && futureTask.isDone() && LocalDateTime.now().isAfter(expirationDate);
    }
}
