package training.concurrency.ex_3.cache;

/**
 * This interface is intended to represent lazy-cache which
 * is able to load its value on demand using Loader<V>.
 * <p>
 * If multiple threads requests a value only single load should
 * be triggered and the other getters should wait till it is done.
 * <p>
 * There should be periodical task, which checks the cache and
 * resets/invalidates the values which are too old (parametrised
 * via constructor or whatever).
 */
public interface LoadableCache<V> {

    /**
     * Gets a value if present, otherwise - loads it or waits till it is loaded.
     */
    V get(String key) throws InterruptedException;

    /**
     * Invalidates the key mapped value so that next get will retrigger load.
     */
    void reset(String key);

    /**
     * Invalidate all expired cached values.
     */
    void resetAllExpiredValues();

    /**
     * This is the interface for the loader per se.
     */
    interface Loader<V> {
        V load(String key) throws InterruptedException;
    }
}
