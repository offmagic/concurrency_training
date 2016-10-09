package training.concurrency.ex_3.loader;

import training.concurrency.ex_3.cache.LoadableCache;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Square number loader.
 * Loader parses string and performs squaring.
 * Key "5" is error prone case.
 *
 * @author Alexey Boznyakov
 */
public class SquareNumberLoader implements LoadableCache.Loader<Integer> {

    /**
     * Counter for emulating computation error.
     */
    private static final AtomicInteger counterForLoadValueByKeyFive = new AtomicInteger(0);

    @Override
    public Integer load(final String key) throws InterruptedException {
        if (key == null) {
            throw new NullPointerException("key is null");
        }

        Integer number = Integer.valueOf(key);
        if (number == 5 && counterForLoadValueByKeyFive.getAndIncrement() % 2 == 0) {
            throw new IllegalStateException("Emulate computation error.");
        }

        TimeUnit.SECONDS.sleep(10); // emulate long running task
        return number * number;
    }
}
