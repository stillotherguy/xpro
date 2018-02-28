package com.ly.fn.inf.xpro.plugin.benchmark.cli;


/**
 * @author Mitsui
 * @since 2018年02月02日
 */
public abstract class AbstractCommand implements Command {

    private final Statistics statistics;
    private final long startTime;
    private final long endTime;
    private final int statisticTime;

    public AbstractCommand(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.statisticTime = (int) ((endTime - startTime) / 1000000);
        this.statistics = new Statistics(statisticTime);
    }
    
    @Override
    public void execute() {
        long beginTime = System.nanoTime() / 1000L;
        while (beginTime <= startTime) {
            // warm up
            beginTime = System.nanoTime() / 1000L;
            try {
                Object result = executeInternal();
            } catch (Exception e) {}
        }
        while (beginTime <= endTime) {
            beginTime = System.nanoTime() / 1000L;
            Object result = null;
            try {
                result = executeInternal();
            } catch (Exception e) {}

            long responseTime = System.nanoTime() / 1000L - beginTime;
            collectResponseTimeDistribution(responseTime);
            int currTime = (int) ((beginTime - startTime) / 1000000L);
            if (currTime >= statisticTime) {
                continue;
            }
            if (result != null) {
                statistics.TPS[currTime]++;
                statistics.RT[currTime] += responseTime;
            } else {
                statistics.errTPS[currTime]++;
                statistics.errRT[currTime] += responseTime;
            }
        }
    }

    private void collectResponseTimeDistribution(long time) {
        double responseTime = (double) (time / 1000L);
        if (responseTime >= 0 && responseTime <= 1) {
            statistics.above0sum++;
        } else if (responseTime > 1 && responseTime <= 5) {
            statistics.above1sum++;
        } else if (responseTime > 5 && responseTime <= 10) {
            statistics.above5sum++;
        } else if (responseTime > 10 && responseTime <= 50) {
            statistics.above10sum++;
        } else if (responseTime > 50 && responseTime <= 100) {
            statistics.above50sum++;
        } else if (responseTime > 100 && responseTime <= 500) {
            statistics.above100sum++;
        } else if (responseTime > 500 && responseTime <= 1000) {
            statistics.above500sum++;
        } else if (responseTime > 1000) {
            statistics.above1000sum++;
        }
    }

    protected abstract Object executeInternal();

    @Override
    public Statistics getStatistics() {
        return statistics;
    }
}
