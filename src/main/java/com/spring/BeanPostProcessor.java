package com.spring;

import java.util.Object;

/**
 * @className: BeanPostProcessor
 * @description: TODO
 * @author: monster_gan
 * @date: 2022/1/4 22:47
 **/
public interface BeanPostProcessor {

    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return  bean;
    }

    default Object postProcessAfterInitialization(Object bean, String beanName){
        return bean;
    }
}
