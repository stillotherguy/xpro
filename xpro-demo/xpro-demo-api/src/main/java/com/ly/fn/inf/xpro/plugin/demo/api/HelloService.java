package com.ly.fn.inf.xpro.plugin.demo.api;

import com.ly.fn.inf.base.AppBizException;
import com.ly.fn.inf.rpc.annotaion.RpcService;

import java.util.List;

/**
 * @author Mitsui
 * @since 2018年01月10日
 */
@RpcService(serviceGroup = "hello")
public interface HelloService {

    String hello();

    User helloUser(User user, String name);

    List<User> helloUsers(List<User> users);

    void helloExp() throws AppBizException;
}
