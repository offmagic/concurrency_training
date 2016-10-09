package training.concurrency.ex_3.utils;

import net.jcip.annotations.ThreadSafe;

/**
 * Class is intended to provide utility methods for logging.
 *
 * @author Alexey Boznyakov
 */
@ThreadSafe
public class LogMessageTools {

    /**
     * Get name of current thread.
     *
     * @return name of current thread
     */
    public static String getNameCurrentThread() {
        Thread currentThread = Thread.currentThread();
        String name = currentThread.getName();
        String groupName = currentThread.getThreadGroup().getName();
        return "name: [" + name + "], group: [" + groupName + "]";
    }
}
