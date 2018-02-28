package com.ly.fn.inf.xpro.plugin.autoconfig;

import com.ly.fn.inf.rpc.annotaion.RpcAbstractService;
import com.ly.fn.inf.rpc.annotaion.RpcService;
import com.ly.fn.inf.util.SleepUtil;
import com.ly.fn.inf.util.TargetClassAware;
import com.ly.fn.inf.xpro.plugin.api.RemoteObjectFactoryAware;
import com.ly.fn.inf.xpro.plugin.core.client.InternalClientObjectFactory;
import com.ly.fn.inf.xpro.plugin.core.server.InternalServerObjectRegistry;
import com.ly.fn.inf.xpro.plugin.core.util.AttentionDebugLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Mitsui
 * @since 2018年01月10日
 */
@Slf4j
public class XproServerLifecycle implements InitializingBean, ApplicationContextAware, PriorityOrdered, SmartLifecycle {
    private ApplicationContext applicationContext;

    protected final Object lifecycleMonitor = new Object();

    private AtomicBoolean init = new AtomicBoolean(false);

    private AtomicBoolean running = new AtomicBoolean(false);

    protected boolean debugMode = false;

    @Autowired
    private InternalServerObjectRegistry serverObjectRegistry;

    @Autowired
    private InternalClientObjectFactory clientObjectFactory;

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        log.info("stop inf-rpc.");
        stopRpc();
        log.info("stop inf-rpc finished.");
        SleepUtil.sleep(100);
        synchronized (this.lifecycleMonitor) {
            running.compareAndSet(true, false);
            init.compareAndSet(true, false);
        }
        callback.run();
    }

    public void initRpc() {
        AttentionDebugLogger.DEBUGMODE = debugMode;
    }

    public void stopRpc() {
        log.info("stop inf-rpc server transport finished");
        serverObjectRegistry.prepareStop();
        clientObjectFactory.stop();
        serverObjectRegistry.stop();
    }

    public void startRpc() {
        serverObjectRegistry.start();
    }

    protected void registerBeans(Map<String, Object> beans) {
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            String name = entry.getKey();
            Object bean = entry.getValue();
            Class<?> targetClass = null;
            if (bean instanceof TargetClassAware) {
                targetClass = ((TargetClassAware) bean).getTargetClass();
            } else {
                targetClass = AopUtils.getTargetClass(bean);
            }
            if (serverObjectRegistry.registerServerObject(bean, targetClass)) {
                log.info("register the RpcService = [{}].", name);
            }
        }
    }

    @Override
    public void start() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RpcService.class);
        registerBeans(beans);
        beans = applicationContext.getBeansWithAnnotation(RpcAbstractService.class);
        registerBeans(beans);
        Map<String, RemoteObjectFactoryAware> beans2 = applicationContext.getBeansOfType(RemoteObjectFactoryAware.class);
        beans.clear();
        for (Map.Entry<String, RemoteObjectFactoryAware> entry : beans2.entrySet()) {
            beans.put(entry.getKey(), entry.getValue());
        }
        registerBeans(beans);
        log.info("start inf-rpc.");
        startRpc();
        SleepUtil.sleep(100);
        synchronized (this.lifecycleMonitor) {
            running.compareAndSet(false, true);
        }
        log.info("register term signal handler.");
        Signal.handle(new Signal("TERM"), new SignalHandler() {
            public void handle(Signal sn) {
                log.info(sn.getName() + " signal is recevied.");
                System.exit(0);
            }
        });
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        synchronized (this.lifecycleMonitor) {
            return running.get();
        }
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.init.compareAndSet(false, true)) {
            this.initRpc();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
