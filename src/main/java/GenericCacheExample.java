import java.util.concurrent.*;

/**
 * 线程安全的通用缓存方案
 */
public class GenericCacheExample<K, V> {
    // cache 中的保存值不是直接的 V 类型值，而是 Future，这种方式是避免重复计算的关键
    // Future 表示将计算并获得一个值，对于每一个 key，都只会有一个对应的 Future
    // 当线程 1 将 Future 添加到缓存之后，线程 2 会获取同一个 Future，然后等待 Future 的结果返回
    private final ConcurrentMap<K, Future<V>> cache = new ConcurrentHashMap<>();

    public void setValueIfAbsent(final K key, final V value) {
        // 求值算法被抽象为 Callable，由于 Callable 可以任意实现，缓存方案就可以应用到任意场景
        createFutureIfAbsent(key, new Callable<V>() {
            @Override
            public V call() throws Exception {
                return value;
            }
        });
    }

    /**
     * 从 Future 获取最终需要用到的值
     */
    public V getValue(final K key, final Callable<V> callable) throws InterruptedException, ExecutionException {
        try {
            final Future<V> future = createFutureIfAbsent(key, callable);
            // Future 的 get() 方法保证了同时只会有一个线程在计算某个序列值
            return future.get();
        } catch (InterruptedException | ExecutionException | RuntimeException e) {
            cache.remove(key);
            throw e;
        }
    }

    private Future<V> createFutureIfAbsent(final K key, final Callable<V> callable) {
        // 从 cache 中取得对应的 Future
        Future<V> future = cache.get(key);
        // 如果 future 是 null，就新建一个 future，并添加到 cache 里面
        if (future == null) {
            final FutureTask<V> futureTask = new FutureTask<V>(callable);
            // ConcurrentMap 的 putIfAbsent() 使用线程安全的方式将一个不在 map 内的新值添加到 map 内
            // putIfAbsent() 方法返回先前已经存在的 Future，
            // 如果返回的 future 是null，表明这个 key 在 cache 中没有对应的值。
            // 于是我们新建的 future 会被自动添加到 cache 里
            future = cache.putIfAbsent(key, futureTask);
            if (future == null) {
                future = futureTask;
                // 开始 futureTask，不然 future 无法开始执行
                futureTask.run();
            }
        }
        return future;
    }
}
