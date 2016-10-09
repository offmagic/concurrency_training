package training.concurrency.ex_3.task;

import training.concurrency.ex_3.cache.LoadableCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Task for invalidating expired cached values.
 *
 * @author Alexey Boznyakov
 */
public class InvalidateTask implements Runnable {

    /**
     * Logger.
     */
    private static final Logger log = LogManager.getLogger(InvalidateTask.class);

    /**
     * Cache for invalidating values.
     */
    private final LoadableCache cache;

    /**
     * Timeout for invalidating.
     */
    private final long invalidateTimeout;

    /**
     * Constructor.
     *
     * @param cache cache for invalidating values
     * @throws IllegalArgumentException if cache is null
     *                                  if timeout for invalidating less or equals zero
     */
    public InvalidateTask(final LoadableCache cache, final long invalidateTimeout) {
        if (cache == null) {
            throw new IllegalArgumentException("Cache is null");
        }
        this.cache = cache;

        if (invalidateTimeout <= 0) {
            throw new IllegalArgumentException("Timeout for invalidating should be more than zero.");
        }
        this.invalidateTimeout = invalidateTimeout;
    }

    /**
     * Steps:
     * 1. Invalidate all expired values
     * 2. Sleep timeout and then go to step 1.
     */
    @Override
    public void run() {
        while (true) {
            try {
                cache.resetAllExpiredValues();
                TimeUnit.SECONDS.sleep(invalidateTimeout);
            } catch (InterruptedException e) {
                log.warn("Invalidate thread was interrupted. Exit from infinite loop.");
                return;
            } catch (Exception ex) {
                log.warn("Error during resetting expired values.");
            }
        }
    }
}
