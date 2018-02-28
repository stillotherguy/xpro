package com.ly.fn.inf.xpro.plugin.benchmark.cli;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * @author Mitsui
 * @since 2018年01月31日
 */
public class MultiThreadCommandInvoker implements CommandInvoker {

    private final int concurrent;
    private final CommandFactory factory;
    private final BlockingQueue<Statistics> queue;

    private CyclicBarrier barrier;
    private CountDownLatch latch;

    public MultiThreadCommandInvoker(int concurrent, CommandFactory factory, BlockingQueue<Statistics> queue) {
        this.concurrent = concurrent;
        this.factory = factory;
        this.queue = queue;
        if (concurrent > 0) {
            barrier = new CyclicBarrier(concurrent);
            latch = new CountDownLatch(concurrent);
        } else
            throw new RuntimeException("concurrent must > 0");
    }

    @Override
    public void invoke() {
        for (int i = 0; i < concurrent; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                        Command command = factory.newCommand();
                        command.execute();

                        queue.put(command.getStatistics());

                        latch.countDown();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            }, "cli-benchmark-" + i).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BlockingQueue<Statistics> getStatisticsQueue() {
        return queue;
    }
}
