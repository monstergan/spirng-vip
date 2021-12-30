package com.spring;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @className: MonsterApplicationContext
 * @description: TODO
 * @author: monster_gan
 * @date: 2021/12/30 21:14
 **/
public class MonsterApplicationContext {

    private Class configClass;

    private Map<String,BeanDefinition> beanDefinitionMap= new HashMap<>();

    public MonsterApplicationContext(Class configClass) {
        this.configClass = configClass;

        //扫描
        scan(configClass);

    }

    public Object getBean(String beanName) {
        boolean b = beanDefinitionMap.containsKey(beanName);
        if (!b){
            throw new NullPointerException();
        }
        return null;
    }

    private void scan(Class configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {

            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value();
            path = path.replace(".", "/");

            System.out.println(path);

            ClassLoader classLoader = MonsterApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());

            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    //获取文件的绝对路径
                    String absolutePath = f.getAbsolutePath();

                    absolutePath = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));
                    absolutePath = absolutePath.replace("/", ".");

                    System.out.println(absolutePath);
                    try {
                        Class<?> clazz = classLoader.loadClass(absolutePath);
                        if (clazz.isAnnotationPresent(Component.class)) {
                            Component componentAnnotation = clazz.getAnnotation(Component.class);
                            String beanName = componentAnnotation.value();

                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setType(clazz);
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
                                String value = scopeAnnotation.value();
                                beanDefinition.setScope(value);
                            }else {
                                //单例bean
                                beanDefinition.setScope("singleton");
                            }
                            beanDefinitionMap.put(beanName,beanDefinition);
                            //Bean
                            System.out.println(clazz);
                        }

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }


                }
            }

        }
    }


}
