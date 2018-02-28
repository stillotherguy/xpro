package com.ly.fn.inf.xpro.plugin.core.balancer;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2017年12月7日 下午2:06:12
 */
@Slf4j
public class P2PFilteringRule extends AvailabilityFilteringRule {
    private AbstractServerPredicate childPredicate;
    private RoundRobinRule childRoundRobinRule;
     
    public P2PFilteringRule() {
        super();
        childRoundRobinRule = new RoundRobinRule();
        childPredicate = CompositePredicate.withPredicates(super.getPredicate(), new P2PServerPredicate()).build();
    }
    
    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        childRoundRobinRule = new RoundRobinRule();
    }
    
    @Override
    public void setLoadBalancer(ILoadBalancer lb) {
        super.setLoadBalancer(lb);
        childRoundRobinRule.setLoadBalancer(lb);
    }
    
    @Override
    public Server choose(Object key) {
        try {
            int count = 0;
            Server server = childRoundRobinRule.choose(key);
            while (count++ <= 10) {
                if (getPredicate().apply(new PredicateKey(server))) {
                    return server;
                }
                server = childRoundRobinRule.choose(key);
            }
        } catch (Throwable e) {
            log.warn("choose Server using key : {} Error.", key);
        }
        return super.choose(key);
    }

    @Override
    public AbstractServerPredicate getPredicate() {
        if (null == childPredicate) {
            return super.getPredicate();
        }
        return childPredicate;
    }
}
