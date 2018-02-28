package com.ly.fn.inf.xpro.plugin.core;

import com.ly.fn.inf.xpro.plugin.api.RpcServiceCallbackProxy;
import com.ly.fn.inf.xpro.plugin.core.api.ClientObjectFactory;

/**
 * 内部Rpc服务回调代理类。
 * 
 * @author
 */
public interface InternalRpcServiceCallbackProxy extends RpcServiceCallbackProxy {
    void setClientObjectFactory(ClientObjectFactory cliObjFactory);
}
