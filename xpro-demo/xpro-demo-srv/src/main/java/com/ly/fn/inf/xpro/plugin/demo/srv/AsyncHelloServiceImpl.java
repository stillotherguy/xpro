package com.ly.fn.inf.xpro.plugin.demo.srv;

import com.ly.fn.inf.base.AppBizException;
import com.ly.fn.inf.xpro.plugin.demo.api.AsyncHelloService;
import com.ly.fn.inf.xpro.plugin.demo.api.CallbackService;
import org.springframework.stereotype.Component;

/**
 * @author Mitsui
 * @since 2018年01月10日
 */
@Component
public class AsyncHelloServiceImpl implements AsyncHelloService {

    @Override
    public void voidHelloAsync() {
        System.out.println("AsyncHelloService received voidHelloAsync invocation");
    }

    @Override
    public String helloAsync() {
        System.out.println("AsyncHelloService received helloAsync invocation");
        return "Hello";
    }

    @Override
    public void voidHelloAsyncExp() throws AppBizException {
        System.out.println("AsyncHelloService received voidHelloAsyncExp invocation");
        throw new AppBizException("EXP.001");
    }

    @Override
    public void voidHelloAsyncCallback(String str, CallbackService callback) {
        System.out.printf("AsyncHelloService received voidHelloAsyncCallback invocation: args=%s\n", str);
        callback.callback();
    }
}
