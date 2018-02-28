package com.ly.fn.inf.xpro.plugin.benchmark.cli;


import com.ly.fn.inf.xpro.plugin.benchmark.api.BenchmarkService;

/**
 * @author Mitsui
 * @since 2018年01月31日
 */
public class EmptyServiceCommand extends AbstractCommand {

    private final BenchmarkService benchmarkService;

    public EmptyServiceCommand(BenchmarkService benchmarkService, long startTime, long endTime) {
        super(startTime, endTime);
        this.benchmarkService = benchmarkService;
    }

    @Override
    protected Object executeInternal() {
        benchmarkService.emptyService();
        return "empty";
    }
}
