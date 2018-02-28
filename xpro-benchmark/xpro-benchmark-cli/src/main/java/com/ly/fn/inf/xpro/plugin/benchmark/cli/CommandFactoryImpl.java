package com.ly.fn.inf.xpro.plugin.benchmark.cli;

import com.ly.fn.inf.xpro.plugin.benchmark.api.BenchmarkService;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * @author Mitsui
 * @since 2018年01月31日
 */
@Component
@Setter
public class CommandFactoryImpl implements CommandFactory {

    private String cmdType;
    private BenchmarkService benchmarkService;
    private long startTime;
    private long endTime;

    @Override
    public Command newCommand() {
        switch (cmdType) {
            case "empty":
                return new EmptyServiceCommand(benchmarkService, startTime, endTime);
            case "1K":
                return new String1KCommand(benchmarkService, startTime, endTime);
            case "10K":
                return new String10KCommand(benchmarkService, startTime, endTime);
            default:
                throw new RuntimeException("illegal cmd type");
        }
    }
}
