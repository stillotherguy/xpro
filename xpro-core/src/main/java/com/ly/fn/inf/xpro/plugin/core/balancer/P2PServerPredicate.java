package com.ly.fn.inf.xpro.plugin.core.balancer;

import com.ly.fn.inf.xpro.plugin.core.server.AppInfo;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.loadbalancer.Server;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2017年12月7日 下午2:01:24
 */
public class P2PServerPredicate extends AbstractServerPredicate {

    @Override
    public boolean apply(PredicateKey input) {
        Server server = input.getServer();
        AppInfo appInfo = AppInfo.getInstance();
        if (appInfo == null) {
            return true;
        }
        return StringUtils.equals(server.getHost(), appInfo.getHostName()) && (server.getPort() == appInfo.getPort());
    }
}
