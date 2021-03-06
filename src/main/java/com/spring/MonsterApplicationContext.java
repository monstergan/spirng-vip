package com.spring;

import com.monster.service.MonsterBeanPostProcessor;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

/**
 * @className: MonsterApplicationContext
 * @description: TODO
 * @author: monster_gan
 * @date: 2021/12/30 21:14
 **/
public class MonsterApplicationContext {

    private Class configClass;

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    /**
     * 单例池
     */
    private Map<String, Object> singletonObjects = new HashMap<>();

    private final String SINGLETON = "singleton";

    public MonsterApplicationContext(Class configClass) {
        this.configClass = configClass;
        //扫描
        scan(configClass);

        //创建单例bean
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (SINGLETON.equals(beanDefinition.getScope())) {
                //单例bean
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        }

    }

    /**
     * @param: beanName
     * @param: beanDefinition
     * @description: TODO 创建bean
     * @return: java.lang.Object
     * @author: monster_gan
     * @date: 2022/1/4 21:42
     * @version 1.0
     */
    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();
        Object instance = null;
        try {
            //实例化对象
            instance = clazz.getConstructor().newInstance();
            //依赖注入
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    //开启反射
                    field.setAccessible(true);
                    field.set(instance, getBean(field.getName()));
                }
            }
            //实现初始化
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            //实现AOP原理
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * @param: beanName
     * @description: TODO 获取bean对象
     * @return: java.lang.Object
     * @author: monster_gan
     * @date: 2022/1/4 21:48
     * @version 1.0
     */
    public Object getBean(String beanName) {
        boolean b = beanDefinitionMap.containsKey(beanName);
        if (!b) {
            throw new NullPointerException();
        }
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (SINGLETON.equals(beanDefinition)) {
            //单例bean
            Object singletonBean = singletonObjects.get(beanName);
            //如果bean没有找到，则创建新的bean
            if (Objects.isNull(singletonBean)) {
                singletonBean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, singletonBean);
            }
            return singletonBean;
        } else {
            //原型bean
            Object prototypeBean = createBean(beanName, beanDefinition);
            return prototypeBean;
        }
    }

    /**
     * @param: configClass
     * @description: TODO 扫描bean的属性
     * @return: void
     * @author: monster_gan
     * @date: 2022/1/4 21:33
     * @version 1.0
     */
    private void scan(Class configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value();
            path = path.replace(".", "/");
            ClassLoader classLoader = MonsterApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    //获取文件的绝对路径
                    String absolutePath = f.getAbsolutePath();
                    absolutePath = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));
                    absolutePath = absolutePath.replace("/", ".");
                    try {
                        Class<?> clazz = classLoader.loadClass(absolutePath);
                        //判断当前类是否有Component注解
                        if (clazz.isAnnotationPresent(Component.class)) {

                            //判断类是否实现BeanPostProcessor接口
                            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                //根据类的无参构造方法来实例化对象
                                BeanPostProcessor instance = (BeanPostProcessor) clazz.getConstructor().newInstance();
                                //将实例化好的对象添加到缓存
                                beanPostProcessorList.add(instance);
                            }

                            Component componentAnnotation = clazz.getAnnotation(Component.class);
                            String beanName = componentAnnotation.value();
                            //如果没有设置名字，则生成默认bean的名称
                            if ("".equals(beanName)) {
                                beanName = Introspector.decapitalize(clazz.getSimpleName());
                            }
                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setType(clazz);
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
                                String value = scopeAnnotation.value();
                                beanDefinition.setScope(value);
                            } else {
                                //单例bean
                                beanDefinition.setScope("singleton");
                            }
                            beanDefinitionMap.put(beanName, beanDefinition);
                        }
                    } catch (ClassNotFoundException | NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }


}
