package com.ly.fn.inf.xpro.plugin.core.server;

import com.ly.fn.inf.xpro.plugin.core.util.NetUtils;
import com.ly.fn.inf.xpro.plugin.core.util.XproEnvironmentConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2017年12月8日 下午9:22:46
 */
@Slf4j
public class ServerPortAllocator {
    private static class ServerPortAllocatorHolder {
        private static final ServerPortAllocator SERVER_PORT_ALLOCATOR = new ServerPortAllocator();
    }
    
    public static ServerPortAllocator getInstance() {
        return ServerPortAllocatorHolder.SERVER_PORT_ALLOCATOR;
    }

    public synchronized int selectPort(int expectListenPort, String appName) {
        String instance = getInstanceNum();
        log.info("application : {} instance : {} select port, expectListenPort : {}.", appName, instance, expectListenPort);
        if (StringUtils.isBlank(instance)) {
            if (NetUtils.isAvailable(expectListenPort)) {
                return expectListenPort;
            }
            return NetUtils.getAvailablePort(10000);
        }
        String portKey = appName + "." + instance + ".port";
        String allocPortHome = System.getProperty(XproEnvironmentConstants.ALLOC_PORT_HOME);
        if (StringUtils.isBlank(allocPortHome)) {
            allocPortHome = System.getProperty("user.home") + "/.xpro_alloc_port";
        }
        File appPortFile = new File(allocPortHome + "/" + appName + ".properties");
        if (appPortFile.exists()) {
            try {
                Properties portProps = new Properties();
                portProps.load(new FileReader(appPortFile));
                String portString = portProps.getProperty(portKey);
                if (StringUtils.isBlank(portString)) {
                    int port = -1;
                    if (NetUtils.isAvailable(expectListenPort)) {
                        port =  expectListenPort;
                    } else {
                        port = NetUtils.getAvailablePort(10024);
                    }
                    portProps.setProperty(portKey, port + "");
                    portProps.store(new FileWriter(appPortFile), appName + " port alloc.");
                    return port;
                }
                int port = -1;
                if (NetUtils.isAvailable(expectListenPort)) {
                    port = expectListenPort;
                } else {
                    port = NumberUtils.toInt(portString, -1);
                }
                if (NetUtils.isAvailable(port) == false) {
                    port = NetUtils.getAvailablePort(10029);
                }
                portProps.setProperty(portKey, port + "");
                portProps.store(new FileWriter(appPortFile), appName + " port alloc.");
                return port;
            } catch (Throwable e) {
                log.error("selectPort loading alloc port file Error.", e);
                throw new IllegalStateException("selectPort loading alloc port file Error.", e);
            }
        }
        try {
            int port = -1;
            appPortFile.getParentFile().mkdirs();
            boolean createPortFile = appPortFile.createNewFile();
            if (createPortFile == false) {
                appPortFile.delete();
                createPortFile = appPortFile.createNewFile();
                if (createPortFile == false) {
                    if (NetUtils.isAvailable(expectListenPort)) {
                        port = expectListenPort;
                    } else {
                        port = NetUtils.getAvailablePort(10034);
                    }
                    // TODO 无法创建文件是什么鬼
                    log.warn("can't create alloc port file : {}.", appPortFile);
                    return port;
                }
            }
            Properties portProps = new Properties();
            if (NetUtils.isAvailable(expectListenPort)) {
                port = expectListenPort;
            } else {
                port = NetUtils.getAvailablePort(10039);
            }
            portProps.setProperty(portKey, port + "");
            portProps.store(new FileWriter(appPortFile), appName + " port alloc.");
            return port;
        } catch (Throwable e) {
            log.error("selectPort create alloc port file Error.", e);
            throw new IllegalStateException("selectPort create alloc port file Error.", e);
        }
    }
    
    private String getInstanceNum() {
        String instance = System.getProperty(XproEnvironmentConstants.APP_INSTANCE);
        if (StringUtils.isBlank(instance)) {
            instance = System.getenv(XproEnvironmentConstants.APP_INSTANCE);
        }
        return instance;
    }
    
    private ServerPortAllocator() {}
}
