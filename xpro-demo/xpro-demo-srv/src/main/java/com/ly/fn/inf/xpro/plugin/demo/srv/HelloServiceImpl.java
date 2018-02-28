package com.ly.fn.inf.xpro.plugin.demo.srv;

import com.ly.fn.inf.base.AppBizException;
import com.ly.fn.inf.xpro.plugin.demo.api.HelloService;
import com.ly.fn.inf.xpro.plugin.demo.api.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author Mitsui
 * @since 2018年01月10日
 */
@Component
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello() {
        return "Hello World";
    }

    @Override
    public User helloUser(User user, String name) {
        System.out.printf("HelloService received helloUser invocation: args=%s\n", Arrays.toString(new Object[] {user, name}));
        user.setName(name);
        return user;
    }

    @Override
    public List<User> helloUsers(List<User> users) {
        System.out.printf("HelloService received helloUsers invocation");
        return users;
    }

    @Override
    public void helloExp() throws AppBizException {
        System.out.println("HelloService received helloExp invocation");
        throw new AppBizException("EXP.001");
    }
}
