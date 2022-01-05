package com.monster.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @className: MonsterBeanPostProcessor
 * @description: TODO
 * @author: monster_gan
 * @date: 2022/1/5 22:09
 **/
@Component
public class MonsterBeanPostProcessor implements BeanPostProcessor {

    /**
     * @param: bean
     * @param: beanName
     * @description: TODO 初始化之后
     * @return: java.lang.Object
     * @author: monster_gan
     * @date: 2022/1/5 22:10
     * @version 1.0
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {

        if (beanName.equals("userService")) {
            //代理对象
            Object proxyInstance = Proxy.newProxyInstance(MonsterBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    //切面逻辑
                    System.out.println("切面逻辑");
                    //执行普通Bean的方法
                    return method.invoke(bean,args);
                }
            });
            return proxyInstance;
        }
        return bean;
    }
}
