package training.concurrency.ex_3.cache;

import net.jcip.annotations.ThreadSafe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import static training.concurrency.ex_3.utils.LogMessageTools.getNameCurrentThread;

/**
 * Class is intended to provide lazy-cache which is able to load its value on demand.
 * For more details please see to {@link LoadableCache}.
 *
 * @author Alexey Boznyakov
 */
@ThreadSafe
public class LoadableCacheImpl<V> implements LoadableCache<V> {

    /**
     * Logger.
     */
    private static final Logger log = LogManager.getLogger(LoadableCacheImpl.class);

    /**
     * Loader.
     */
    private final LoadableCache.Loader<V> loader;

    /**
     * Expiration timeout.
     */
    private final long cachedValueExpirationTimeout;

    /**
     * Internal data structure for caching calculated values.
     */
    private final ConcurrentMap<String, LazyLoadingValue<V>> cache = new ConcurrentHashMap<>();

    /**
     * Constructor.
     *
     * @param loader                       loader for loading values
     * @param cachedValueExpirationTimeout cached values expiration timeout
     * @throws IllegalArgumentException if loader is null or
     *                                  if expirationTimeout less or equals zero
     */
    public LoadableCacheImpl(final LoadableCache.Loader<V> loader, final long cachedValueExpirationTimeout) {
        if (loader == null) {
            throw new IllegalArgumentException("Loader is null");
        }
        this.loader = loader;

        if (cachedValueExpirationTimeout <= 0) {
            throw new IllegalArgumentException("Expiration timeout should be more than zero.");
        }
        this.cachedValueExpirationTimeout = cachedValueExpirationTimeout;
    }

    @Override
    public V get(final String key) throws InterruptedException {
        if (key == null) {
            throw new NullPointerException("Key is null");
        }

        log.debug("Thread {}. Get value for key {}.", getNameCurrentThread(), key);
        LazyLoadingValue<V> existingCachedValue = cache.get(key);
        if (existingCachedValue == null) {
            log.debug("Thread {}. Cached value isn't found in cache. Create new cached value.", getNameCurrentThread());
            LazyLoadingValue<V> newValueForCache = new LazyLoadingValue<>(() -> loader.load(key),
                    cachedValueExpirationTimeout);

            existingCachedValue = cache.putIfAbsent(key, newValueForCache);
            if (existingCachedValue == null) {
                log.debug("Thread {}. Cached value isn't found in cache, during attempt to putting it to cache. " +
                        "Run this task.", getNameCurrentThread());
                existingCachedValue = newValueForCache;
                existingCachedValue.calculate();
            } else {
                log.debug("Thread {}. Cached value is found in cache, during attempt to putting it to cache.",
                        getNameCurrentThread());
            }
        }
        try {
            return existingCachedValue.retrieveResult();
        } catch (ExecutionException ex) {
            cache.remove(key); // give chance to other threads to compute value later
            throw new IllegalStateException("Thread " + getNameCurrentThread() +  "Error occurred during computation.");
        }
    }

    @Override
    public void reset(final String key) {
        if (key == null) {
            throw new NullPointerException("Key is null");
        }

        LazyLoadingValue<V> cachedValue = cache.get(key);
        if (cachedValue == null) {
            log.debug("Thread {}. Cached value isn't found in cache. Nothing to reset.", getNameCurrentThread());
            return;
        }

        if (cachedValue.isValueExpired()) {
            LazyLoadingValue<V> removedCachedValue = cache.remove(key);
            if (removedCachedValue != null) {
                log.debug("Thread {}. Cached value is removed from cache.", getNameCurrentThread());
            } else {
                log.debug("Thread {}. Cached value isn't removed from cache. It has been removed earlier in another thread.",
                        getNameCurrentThread());
            }
        }
    }

    @Override
    public void resetAllExpiredValues() {
        // copy all keys in another collection, because other threads can exec reset method, and our iterator will break.
        new HashSet<>(cache.keySet()).forEach(this::reset);
    }
}
