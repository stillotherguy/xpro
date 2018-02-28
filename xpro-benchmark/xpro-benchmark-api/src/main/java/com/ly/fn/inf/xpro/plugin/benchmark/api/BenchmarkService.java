package com.ly.fn.inf.xpro.plugin.benchmark.api;

import com.ly.fn.inf.rpc.annotaion.RpcService;

/**
 * @author Mitsui
 * @since 2018年01月31日
 */
@RpcService(serviceGroup = "benchmark")
public interface BenchmarkService {

    Object echoService(Object request);

    void emptyService();
}
