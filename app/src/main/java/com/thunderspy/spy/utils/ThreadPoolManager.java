package com.thunderspy.spy.utils;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by ariyan on 2/15/17.
 */

public final class ThreadPoolManager {
    private static ThreadPoolExecutor threadPoolExecutor = null;
    private static final Object THREAD_POOL_MUTEX = new Object();


    public static void setupThreadPool() {
        try {
            synchronized (THREAD_POOL_MUTEX) {
                if(threadPoolExecutor == null || threadPoolExecutor.isShutdown() || threadPoolExecutor.isTerminated()) {
                    List<Runnable> queuedCommands = null;
                    try {
                        if(threadPoolExecutor != null) {
                            queuedCommands = threadPoolExecutor.shutdownNow();
                        }
                    } catch (Exception exp) {}
                    threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Constants.THREAD_POOL_CORE_SIZE);
                    try {
                        if(queuedCommands != null) {
                            for(Runnable command : queuedCommands) {
                                threadPoolExecutor.execute(command);
                            }
                        }
                    } catch (Exception exp) {}
                } else {
                    Utils.log("ThreadPool is already running");
                }
            }
        } catch (Exception exp) {
            Utils.log("ThreadPool Error: %s", exp.getMessage());
        }
    }

    public static void execute(Runnable command) {
        try {
            threadPoolExecutor.execute(command);
        } catch (Exception exp) {
            Utils.log("ThreadPool Error on submitting task: %s", exp.getMessage());
            try {
                setupThreadPool();
                threadPoolExecutor.execute(command);
            } catch (Exception exp1) {
                Utils.log("ThreadPool Error on resubmitting task: %s", exp.getMessage());
            }
        }
    }

}
