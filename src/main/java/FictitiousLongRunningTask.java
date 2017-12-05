import java.util.concurrent.Callable;

/**
 * 实践：新的缓存方案
 * 例子二：虚拟的耗时任务
 */
public class FictitiousLongRunningTask {
    private final GenericCacheExample<String, Long> cache = new GenericCacheExample<>();

    public long computeLongTask(final String key) throws Exception {
        return cache.getValue(key, new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                System.out.println("Computing Fictitious Long Running Task: " + key);
                Thread.sleep(10000); // 10 seconds
                return System.currentTimeMillis();
            }
        });
    }

    public static void main(final String[] args) throws Exception {
        final FictitiousLongRunningTask task = new FictitiousLongRunningTask();

        final StopWatch stopWatch = new StopWatch("Fictitious Long Running Task");
        stopWatch.start("First Run");
        task.computeLongTask("a");
        stopWatch.stop();

        stopWatch.start("Other Runs");
        for (int i = 0; i < 100; i++) {
            task.computeLongTask("a");
        }
        stopWatch.stop();

        System.out.println(stopWatch);
    }
}
