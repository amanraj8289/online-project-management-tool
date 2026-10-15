package com.projectmanagement.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Thread-safe Background Job Executor demonstrating Multithreading & Concurrency.
 * Uses a controlled thread pool for asynchronous tasks (e.g., audit logging, notifications).
 */
public final class BackgroundJobExecutor {

    private static final Logger logger = LoggerFactory.getLogger(BackgroundJobExecutor.class);

    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 4;
    private static final long KEEP_ALIVE_SECONDS = 60L;

    private static final ExecutorService executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(500),
            new ThreadFactory() {
                private int counter = 1;
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "ProManage-Worker-" + (counter++));
                    t.setDaemon(true);
                    return t;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    private BackgroundJobExecutor() {
    }

    /**
     * Executes a runnable task asynchronously in the background.
     *
     * @param task the background task
     */
    public static void execute(Runnable task) {
        if (task == null) return;
        try {
            executor.execute(task);
        } catch (Exception e) {
            logger.error("Failed to submit background task: {}", e.getMessage(), e);
        }
    }

    /**
     * Gracefully shuts down the background worker pool.
     */
    public static void shutdown() {
        logger.info("Shutting down background worker thread pool...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
