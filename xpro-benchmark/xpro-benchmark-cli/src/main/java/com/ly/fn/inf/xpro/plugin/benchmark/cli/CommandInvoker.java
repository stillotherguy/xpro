package com.ly.fn.inf.xpro.plugin.benchmark.cli;

import java.util.concurrent.BlockingQueue;

/**
 * @author Mitsui
 * @since 2018年01月31日
 */
public interface CommandInvoker {

    void invoke();

    BlockingQueue<Statistics> getStatisticsQueue();
}
