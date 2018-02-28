package com.ly.fn.inf.xpro.plugin.demo.cli;

import com.ly.fn.inf.rpc.annotaion.RpcService;
import com.ly.fn.inf.xpro.plugin.demo.api.CallbackService;
import org.springframework.stereotype.Component;

/**
 * @author Mitsui
 * @since 2018年01月23日
 */
@RpcService(serviceGroup = "helloclient")
@Component
public class CallbackService2 implements CallbackService {
    @Override
    public void callback() {
        System.out.println("Annotation service callback: I'm back");
    }
}
