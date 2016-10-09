package training.concurrency.ex_3.task;

import training.concurrency.ex_3.cache.LoadableCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadLocalRandom;

import static training.concurrency.ex_3.utils.LogMessageTools.getNameCurrentThread;

/**
 * Task for consuming cached values.
 *
 * @author Alexey Boznyakov
 */
public class ConsumerTask implements Runnable {

    /**
     * Logger.
     */
    private static final Logger log = LogManager.getLogger(ConsumerTask.class);

    /**
     * Cache for consuming values.
     */
    private final LoadableCache cache;


    /**
     * Constructor.
     *
     * @param cache cache for invalidating values
     * @throws IllegalArgumentException if cache is null
     */
    public ConsumerTask(final LoadableCache cache) {
        if (cache == null) {
            throw new IllegalArgumentException("Cache is null");
        }
        this.cache = cache;
    }

    /**
     * Steps:
     * 1. Generate randomly key 1(inclusive) - 11(exclusive)
     * 2. Retrieve value by key and log it.
     * 3. Sleep timeout and then go to step 1.
     */
    @Override
    public void run() {
        while (true) {
            try {
                String key = Integer.toString(ThreadLocalRandom.current().nextInt(1, 11));
                Object value = cache.get(key);
                log.info("Thread {}. Result: key: {}, value: {}.", getNameCurrentThread(), key, value);
            } catch (InterruptedException e) {
                log.warn("Thread was interrupted. Exit from infinite loop.");
                return;
            } catch (Exception ex) {
                log.error("Error during consuming value. ", ex);
            }
        }
    }
}
