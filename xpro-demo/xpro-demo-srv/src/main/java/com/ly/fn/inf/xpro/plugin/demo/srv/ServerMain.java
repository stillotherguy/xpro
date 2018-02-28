package com.ly.fn.inf.xpro.plugin.demo.srv;

import com.ly.fn.inf.xpro.plugin.autoconfig.annotation.XproPluginApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

/**
 * @author Mitsui
 * @since 2018年01月10日
 */
@XproPluginApplication
public class ServerMain {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(ServerMain.class);
    }
}
