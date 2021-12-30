package com.monster;

import com.monster.service.UserService;
import com.spring.MonsterApplicationContext;

/**
 * @className: Test
 * @description: TODO
 * @author: monster_gan
 * @date: 2021/12/30 21:14
 **/
public class Test {

    public static void main(String[] args) {
        MonsterApplicationContext applicationContext = new MonsterApplicationContext(Appconfig.class);


        UserService userService = (UserService) applicationContext.getBean("userService");
        userService.test();

    }
}
