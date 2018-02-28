package com.ly.fn.inf.xpro.plugin.benchmark.cli;

import com.google.common.collect.Lists;
import com.ly.fn.inf.util.NumberUtil;
import com.ly.fn.inf.xpro.plugin.autoconfig.annotation.XproPluginApplication;
import com.ly.fn.inf.xpro.plugin.benchmark.api.BenchmarkService;
import com.ly.fn.inf.xpro.plugin.core.api.ClientObjectFactory;
import com.ly.fn.inf.xpro.plugin.core.api.RunMode;
import org.springframework.boot.SpringApplication;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Mitsui
 * @since 2018年01月11日
 */
@XproPluginApplication(runMode = RunMode.CLIENT)
public class ClientMain {

    private static final int WARMUPTIME = 30;

    public static void main(String[] args) throws Exception {
        AbstractApplicationContext context = (AbstractApplicationContext) SpringApplication.run(ClientMain.class);
        context.registerShutdownHook();

        Environment env = context.getEnvironment();
        int concurrent = NumberUtil.parseInteger(env.getProperty("concurrent"), 0);
        int runTime = NumberUtil.parseInteger(env.getProperty("runTime"), 0);

        Assert.isTrue(concurrent > 0, "concurrent must > 0");
        Assert.isTrue(runTime > 0, "runTime must > 0");

        // empty 1K 10K
        String cmdType = env.getProperty("cmdType");
        long currentTime = System.nanoTime() / 1000L;
        long startTime = currentTime + WARMUPTIME * 1000 * 1000L;
        long endTime = currentTime + runTime * 1000 * 1000L;

        ClientObjectFactory clientObjectFactory = context.getBean(ClientObjectFactory.class);

        CommandFactoryImpl factory = new CommandFactoryImpl();
        factory.setCmdType(cmdType);
        factory.setBenchmarkService(clientObjectFactory.getClientObject(BenchmarkService.class));
        factory.setStartTime(startTime);
        factory.setEndTime(endTime);

        LinkedBlockingDeque<Statistics> queue = new LinkedBlockingDeque<>(concurrent);
        CommandInvoker commandInvoker = new MultiThreadCommandInvoker(concurrent, factory, queue);
        commandInvoker.invoke();

        List<Statistics> statisticsList = Lists.newLinkedList();
        do {
            queue.drainTo(statisticsList);
        } while (statisticsList.size() < concurrent);

        ClientStatistics clientStatistics = new ClientStatistics(statisticsList);
        clientStatistics.collectStatistics();
        clientStatistics.printStatistics();

        System.exit(0);
        /*invoker.invokeUser();
        invoker.invokeUsers();
        invoker.invoke();
        invoker.invokeExp();
        invoker.invokeAsync();
        invoker.invokeAsyncExp();
        invoker.invokeVoidAsyncCallback();
        invoker.invokeVoidAsyncServiceCallback1();
        invoker.invokeVoidAsyncServiceCallback2();*/
    }
}
