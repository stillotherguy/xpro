package com.ly.fn.inf.xpro.plugin.benchmark.srv;

import com.codahale.metrics.annotation.Timed;
import com.ly.fn.inf.xpro.plugin.benchmark.api.BenchmarkService;
import org.springframework.stereotype.Component;

/**
 * @author Mitsui
 * @since 2018年01月31日
 */
@Component
public class BenchmarkServiceImpl implements BenchmarkService {
    @Override
    @Timed(name = "server statisrics", absolute = true)
    public Object echoService(Object request) {
        return request;
    }

    @Override
    @Timed(name = "server statisrics", absolute = true)
    public void emptyService() {
    }
}
