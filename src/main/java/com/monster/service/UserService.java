package com.monster.service;

import com.spring.Autowired;
import com.spring.Component;
import com.spring.InitializingBean;
import com.spring.Scope;

/**
 * @className: UserService
 * @description: TODO
 * @author: monster_gan
 * @date: 2021/12/30 21:18
 **/
@Component("userService")
@Scope("prototype")
public class UserService implements InitializingBean {

    @Autowired
     private OrderService orderService;

    public void test(){
        System.out.println(orderService);
    }

    @Override
    public void afterPropertiesSet() {

    }
}
