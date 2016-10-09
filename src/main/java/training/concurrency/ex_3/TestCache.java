package training.concurrency.ex_3;


import training.concurrency.ex_3.cache.LoadableCache;
import training.concurrency.ex_3.cache.LoadableCacheImpl;
import training.concurrency.ex_3.loader.SquareNumberLoader;
import training.concurrency.ex_3.task.ConsumerTask;
import training.concurrency.ex_3.task.InvalidateTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class is intended to test cache.
 *
 * @author Alexey Boznyakov
 */
public class TestCache {

    public static void main(String[] args) {
        long cachedValueExpirationTimeout = 30;
        long calculationTime = 3; // emulate long running computation
        LoadableCache<Integer> cache = new LoadableCacheImpl<>(new SquareNumberLoader(calculationTime), cachedValueExpirationTimeout);

        // start 10 consumer tasks
        ExecutorService consumerExecutor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            consumerExecutor.submit(new ConsumerTask(cache));
        }

        // start 1 invalidate task
        ExecutorService invalidateCacheExecutor = Executors.newFixedThreadPool(1);
        int invalidateTimeout = 10; // check and invalidate values if it needed
        invalidateCacheExecutor.submit(new InvalidateTask(cache, invalidateTimeout));
    }
}
