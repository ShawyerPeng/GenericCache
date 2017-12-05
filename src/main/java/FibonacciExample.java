import java.util.concurrent.Callable;

/**
 * 实践：新的缓存方案
 * 例子一：斐波那契序列求值
 */
public class FibonacciExample {
    private final GenericCacheExample<Long, Long> cache = new GenericCacheExample<>();

    public FibonacciExample() {
        cache.setValueIfAbsent(0L, 1L);
        cache.setValueIfAbsent(1L, 1L);
    }

    public long getNumber(final long index) throws Exception {
        return cache.getValue(index, new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                System.out.println("Computing the " + index + " Fibonacci number");
                return getNumber(index - 1) + getNumber(index - 2);
            }
        });
    }

    public static void main(final String[] args) throws Exception {
        final long index = 12;
        final FibonacciExample example = new FibonacciExample();
        final long fn = example.getNumber(index);
        System.out.println("The " + index + "th Fibonacci number is: " + fn);
    }
}
