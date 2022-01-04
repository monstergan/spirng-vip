package com.monster.service;

import com.spring.Component;
import com.spring.Scope;

/**
 * @className: OrderService
 * @description: TODO
 * @author: monster_gan
 * @date: 2021/12/30 21:18
 **/
@Component
@Scope("singleton")
public class OrderService {

    public void test(){
        System.out.println("testsssss");
    }
}
