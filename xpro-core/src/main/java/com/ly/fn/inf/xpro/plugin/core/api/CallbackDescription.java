package com.ly.fn.inf.xpro.plugin.core.api;

import com.ly.fn.inf.rpc.annotaion.Lifecycle;

/**
 * 回调描述类。
 */
public class CallbackDescription {
    /**
     * 生命周期
     */
    private Lifecycle lifecycle;

    /**
     * 生存周期（秒）
     */
    private int ttl;

    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Lifecycle lifecycle) {
        if (lifecycle == Lifecycle.DEFAULT) {
            throw new RuntimeException("CallbackDescription's lifecycle can't equal DEFAULT.");
        }

        this.lifecycle = lifecycle;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
