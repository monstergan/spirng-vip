package com.spring;


/**
 * @className: BeanPostProcessor
 * @description: TODO 仿照spring里面的BeanPostProcessor 的接口
 * @author: monster_gan
 * @date: 2022/1/4 22:47
 **/
public interface BeanPostProcessor {

    //初始化前
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * @param: bean
     * @param: beanName
     * @description: TODO 初始化之后
     * @return: java.lang.Object
     * @author: monster_gan
     * @date: 2022/1/5 22:10
     * @version 1.0
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
