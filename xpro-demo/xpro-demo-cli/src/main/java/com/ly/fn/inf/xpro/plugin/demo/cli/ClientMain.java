package com.ly.fn.inf.xpro.plugin.demo.cli;

import com.ly.fn.inf.xpro.plugin.autoconfig.annotation.XproPluginApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * @author Mitsui
 * @since 2018年01月11日
 */
@XproPluginApplication
public class ClientMain {

    public static void main(String[] args) throws Exception {
        AbstractApplicationContext context = (AbstractApplicationContext) SpringApplication.run(ClientMain.class);
        Invoker invoker = context.getBean(Invoker.class);
        while (true) {
            invoker.invokeUser();
            invoker.invokeUsers();
            invoker.invoke();
            invoker.invokeExp();
            invoker.invokeAsync();
            invoker.invokeAsyncExp();
            invoker.invokeVoidAsyncCallback();
            invoker.invokeVoidAsyncServiceCallback1();
            invoker.invokeVoidAsyncServiceCallback2();
        }
    }
}
