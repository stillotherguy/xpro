package com.ly.fn.inf.xpro.plugin.demo.api;

import com.ly.fn.inf.base.AppBizException;
import com.ly.fn.inf.rpc.annotaion.Callback;
import com.ly.fn.inf.rpc.annotaion.RpcMethod;
import com.ly.fn.inf.rpc.annotaion.RpcService;

/**
 * @author Mitsui
 * @since 2018年01月10日
 */
@RpcService(serviceGroup = "hello")
public interface AsyncHelloService {

    @RpcMethod(async = true)
    void voidHelloAsync();

    @RpcMethod(async = true)
    String helloAsync();

    @RpcMethod(async = true)
    void voidHelloAsyncExp() throws AppBizException;

    @RpcMethod(async = true)
    void voidHelloAsyncCallback(String str, @Callback CallbackService callback);
}
