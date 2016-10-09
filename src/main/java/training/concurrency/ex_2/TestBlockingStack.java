package training.concurrency.ex_2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class is intended to test {@link BlockingStackImpl} class.
 *
 * @author Alexey Boznyakov
 */
public class TestBlockingStack {

    /**
     * Logger.
     */
    private static final Logger log = LogManager.getLogger(TestBlockingStack.class);

    public static void main(String[] args) throws InterruptedException {
        BlockingStack<Long> stack = new BlockingStackImpl<>(10);

        // create 1 threads for pop value
        // expected behavior: wait, because stack is empty
        ExecutorService consumerExecutor = Executors.newFixedThreadPool(2);
        consumerExecutor.submit(() -> {
            try {
                stack.pop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // sleep
        TimeUnit.SECONDS.sleep(5);

        // create 1 thread for push value
        // expected behavior: 11 will not be pushed
        ExecutorService fillerExecutor = Executors.newFixedThreadPool(1);
        fillerExecutor.submit(() -> {
            try {
                for (long i = 0; i < 12; i++) {
                    stack.push(i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // sleep
        TimeUnit.SECONDS.sleep(5);

        // expected elements: first element 10, 11(may be later, it depends of wake up time "pushed" thread) 9,8,7,6,5,4,3,2,1,0
        for (int i = 0; i < 11; i++) {
            Long popElement = stack.pop();
            log.debug("Retrieve next element from stack: {}", popElement);
        }

        consumerExecutor.shutdown();
        fillerExecutor.shutdown();
    }
}
