package training.concurrency.ex_1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static training.concurrency.utils.LogMessageTools.getNameCurrentThread;

/**
 * Class is intended to test {@link FutureValueImpl} class.
 *
 * @author Alexey Boznyakov
 */
public class TestFutureValue {

    /**
     * Logger.
     */
    private static final Logger log = LogManager.getLogger(FutureValueImpl.class);

    public static void main(String[] args) throws InterruptedException {
        FutureValue<Long> futureValue = new FutureValueImpl<>();

        // create 10 threads for retrieve value
        // expected behavior: wait until value is null
        ExecutorService consumerExecutor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            consumerExecutor.submit(() -> {
                try {
                    futureValue.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // sleep
        TimeUnit.SECONDS.sleep(5);

        // create 10 threads for fill value
        // expected behavior: only one thread set value, other thread exit from method with false
        ExecutorService fillerExecutor = Executors.newFixedThreadPool(10);
        for (long i = 0; i < 10; i++) {
            final long value = i;
            fillerExecutor.submit(() ->
                    log.debug("Thread {}. Try set value {}, result", getNameCurrentThread(), value, futureValue.trySet(value)));
        }

        // sleep
        TimeUnit.SECONDS.sleep(5);

        // check single value 10 times
        for (int i = 0; i < 10; i++) {
            log.debug("Thread {}. Get value: {}", getNameCurrentThread(), futureValue.get());
        }

        consumerExecutor.shutdown();
        fillerExecutor.shutdown();
    }
}
