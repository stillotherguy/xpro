package com.ly.fn.inf.xpro.plugin.demo.cli;

import com.ly.fn.inf.rpc.annotaion.RpcService;
import com.ly.fn.inf.xpro.plugin.api.ConfigurableRpcService;
import com.ly.fn.inf.xpro.plugin.demo.api.CallbackService;
import org.springframework.stereotype.Component;

/**
 * @author Mitsui
 * @since 2018年01月23日
 */
@Component
@RpcService
public class CallbackService1 implements ConfigurableRpcService, CallbackService {

    @Override
    public void callback() {
        System.out.println("Configurable service callback: I'm back");
    }

    @Override
    public String getServiceGroup() {
        return "helloclient";
    }

    @Override
    public String getServiceId() {
        return CallbackService.class.getName();
    }
}
