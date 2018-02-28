package com.ly.fn.inf.xpro.plugin.benchmark.cli;


import com.ly.fn.inf.xpro.plugin.benchmark.api.BenchmarkService;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Mitsui
 * @since 2018年01月31日
 */
public class String10KCommand extends AbstractCommand {

    private final BenchmarkService benchmarkService;

    public String10KCommand(BenchmarkService benchmarkService, long startTime, long endTime) {
        super(startTime, endTime);
        this.benchmarkService = benchmarkService;
    }

    @Override
    public Object executeInternal() {
        int length = 1024 * 10;
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append((char) (ThreadLocalRandom.current().nextInt(33, 128)));
        }

        benchmarkService.echoService(builder.toString());
        return "string10k";
    }
}
