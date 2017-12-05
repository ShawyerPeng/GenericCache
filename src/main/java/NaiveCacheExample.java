import java.util.HashMap;
import java.util.Map;

/**
 * 简单缓存方案实现斐波那契序列求值。
 */
public class NaiveCacheExample {
    private Map<Long, Long> cache = new HashMap<>();

    public NaiveCacheExample() {
        // The base case for the Fibonacci Sequence
        cache.put(0L, 1L);
        cache.put(1L, 1L);
    }

    public Long getNumber(long index) {
        // Check if value is in cache
        if (cache.containsKey(index)) {
            return cache.get(index);
        }

        // Compute value and save it in cache
        long value = getNumber(index - 1) + getNumber(index - 2);
        cache.put(index, value);
        return value;
    }

    public static void main(String[] args) {
        NaiveCacheExample obj = new NaiveCacheExample();
        System.out.println(obj.getNumber(5));
        System.out.println(obj.getNumber(5));
        System.out.println(obj.getNumber(6));
    }
}
