package com.ly.fn.inf.xpro.plugin.demo.cli;

import com.google.common.collect.Lists;
import com.ly.fn.inf.rpc.annotaion.Rpcwired;
import com.ly.fn.inf.xpro.plugin.api.AsyncCallResult;
import com.ly.fn.inf.xpro.plugin.api.RpcClientContextAccessor;
import com.ly.fn.inf.xpro.plugin.core.client.ExpectAsyncCallAccessor;
import com.ly.fn.inf.xpro.plugin.demo.api.AsyncHelloService;
import com.ly.fn.inf.xpro.plugin.demo.api.CallbackService;
import com.ly.fn.inf.xpro.plugin.demo.api.HelloService;
import com.ly.fn.inf.xpro.plugin.demo.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Mitsui
 * @since 2018年01月10日
 */
@Component
public class Invoker {

    @Rpcwired
    private HelloService helloService;

    @Rpcwired
    private AsyncHelloService asyncHelloService;

    @Autowired
    private CallbackService1 callbackService1;

    public void invoke() {
        //System.out.println("HelloService method hello return: " + helloService.hello());
    }

    public User invokeUser() {
        User u = new User();
        u.setName("Susan");

        User user = helloService.helloUser(u, "Catelyn");
        //System.out.println("HelloService method helloUser return: " + user);
        return user;
    }

    public void invokeUsers() {
        // payload size > 128K
        List<User> users = Lists.newLinkedList();
        for (int i = 0; i < 10; i++) {
            User u = new User();
            u.setName("Susan");
            users.add(u);
        }

        users = helloService.helloUsers(users);
        //System.out.println("HelloService method helloUser return: ***");
    }

    public void invokeExp() {
        try {
            helloService.helloExp();
        } catch (Exception e) {
            //System.out.println("HelloService method helloExp throws: " + e);
        }
    }

    public void invokeAsync() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ExpectAsyncCallAccessor.expect(new AsyncCallResult() {
            @Override
            public void onSuccess(Object[] objects, Object o) {
                //System.out.println("AsyncHelloService method helloAsync return: " + o);

                latch.countDown();
            }

            @Override
            public void onException(Object[] objects, Throwable throwable) {
                //System.out.println("AsyncHelloService method helloAsync throws: " + throwable);

                latch.countDown();
            }
        }, -1);
        asyncHelloService.helloAsync();
        latch.await();
    }

    public void invokeAsyncExp() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ExpectAsyncCallAccessor.expect(new AsyncCallResult() {
            @Override
            public void onSuccess(Object[] objects, Object o) {
                //System.out.println("AsyncHelloService method voidHelloAsyncExp return: " + o);

                latch.countDown();
            }

            @Override
            public void onException(Object[] objects, Throwable throwable) {
                //System.out.println("AsyncHelloService method voidHelloAsyncExp throws: " + throwable);

                latch.countDown();
            }
        }, -1);
        asyncHelloService.voidHelloAsyncExp();
        latch.await();
    }

    public void invokeVoidAsyncCallback() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ExpectAsyncCallAccessor.expect(new AsyncCallResult() {
            @Override
            public void onSuccess(Object[] objects, Object o) {
                //System.out.println("AsyncHelloService method voidHelloAsyncCallback return: " + o);

                latch.countDown();
            }

            @Override
            public void onException(Object[] objects, Throwable throwable) {
                //System.out.println("AsyncHelloService method voidHelloAsyncCallback throws: " + throwable);

                latch.countDown();
            }
        }, -1);

        asyncHelloService.voidHelloAsyncCallback("Hello!!!", new CallbackService() {
            @Override
            public void callback() {
                //System.out.println("AsyncHelloService method voidHelloAsyncCallback callback: I'm back");
            }
        });
        latch.await();
    }

    public void invokeVoidAsyncServiceCallback1() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ExpectAsyncCallAccessor.expect(new AsyncCallResult() {
            @Override
            public void onSuccess(Object[] objects, Object o) {
                //System.out.println("AsyncHelloService method voidHelloAsyncCallback service1 callback return: " + o);

                latch.countDown();
            }

            @Override
            public void onException(Object[] objects, Throwable throwable) {
                //System.out.println("AsyncHelloService method voidHelloAsyncCallback service1 callback throws: " + throwable);

                latch.countDown();
            }
        }, -1);

        CallbackService callbackService = RpcClientContextAccessor.newCallbackForRpcService(CallbackService.class, callbackService1);
        asyncHelloService.voidHelloAsyncCallback("Hello!!!", callbackService);
        latch.await();
    }

    public void invokeVoidAsyncServiceCallback2() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ExpectAsyncCallAccessor.expect(new AsyncCallResult() {
            @Override
            public void onSuccess(Object[] objects, Object o) {
                //System.out.println("AsyncHelloService method voidHelloAsyncCallback service2 callback return: " + o);

                latch.countDown();
            }

            @Override
            public void onException(Object[] objects, Throwable throwable) {
                //System.out.println("AsyncHelloService method voidHelloAsyncCallback service2 callback throws: " + throwable);

                latch.countDown();
            }
        }, -1);

        CallbackService callbackService = RpcClientContextAccessor.newCallbackForRpcService(CallbackService.class, CallbackService2.class);
        asyncHelloService.voidHelloAsyncCallback("Hello!!!", callbackService);
        latch.await();
    }
}

