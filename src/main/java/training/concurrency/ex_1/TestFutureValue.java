package training.concurrency.ex_1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class is intended to test {@link FutureValueImpl} class.
 *
 * @author Alexey Boznyakov
 */
public class TestFutureValue {

    public static void main(String[] args) throws InterruptedException {
        FutureValue<Long> futureValue = new FutureValueImpl<>();

        // create 10 threads for retrieve value
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
        ExecutorService fillerExecutor = Executors.newFixedThreadPool(5);
        for (long i = 0; i < 10; i++) {
            final long value = i;
            fillerExecutor.submit((Runnable) () -> futureValue.trySet(value));
        }

        // sleep
        TimeUnit.SECONDS.sleep(5);

        consumerExecutor.shutdown();
        fillerExecutor.shutdown();
    }
}
