package com.god.dragon.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.SimpleCache;
import cn.hutool.core.util.ReflectUtil;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.cglib.core.Converter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于Cglib生成转换的类和方法,第一次调用很慢后面就很快了
 *
 * @author wuyuxiang
 * @version 1.0.0
 * @implNote start with 2024/9/11 14:31
 */
public class BeanUtils3 {
    private BeanUtils3() {
    }

    public static <T, V> V bean2Bean(T source, V desc) {
        if (isNull(source)) {
            return null;
        } else if (isNull(desc)) {
            return null;
        } else {
            BeanCopier beanCopier = BeanCopierCache.INSTANCE.get(source.getClass(), desc.getClass(), null);
            beanCopier.copy(source, desc, null);
            return desc;
        }
    }

    public static <T, V> V bean2Bean(T source, Class<V> desc) {
        if (isNull(source)) {
            return null;
        } else if (isNull(desc)) {
            return null;
        } else {
            V target = ReflectUtil.newInstanceIfPossible(desc);
            return bean2Bean(source, target);
        }
    }

    public static <T, V> List<V> ListBean2List(Collection<T> sourceList, Class<V> desc) {
        if(isNull(sourceList)){
            return null;
        }else{
            return CollUtil.isEmpty(sourceList) ? new ArrayList<V>() : sourceList.stream().map((source)->{
                V target = ReflectUtil.newInstanceIfPossible(desc);
                bean2Bean(source, target);
                return target;
            }).filter(Objects::isNull).collect(Collectors.toList());
        }
    }

    public static <T> Map<String,Object> bean2Map(T source) {
        return isNull(source) ? null : BeanMap.create(source);
    }

    public static <T> Map<String,Object> bean2MapIgnoreNull(T source) {
        if(isNull(source)){
            return null;
        }else{
            Map<String,Object> map = BeanMap.create(source);
            Map<String,Object> rsMap = new HashMap<>();
            if(map != null && !map.isEmpty()){
                map.keySet().forEach((key)->{
                    if(map.get(key) != null){
                        rsMap.put(key, map.get(key));
                    }
                });
            }
            return rsMap;
        }
    }

    public static <T> T map2Bean(Map<String,Object> source, T bean) {
        if(source == null || source.isEmpty()){
            return null;
        } else if (isNull(bean)) {
            return null;
        } else {
            BeanMap.create(bean).putAll(source);
            return bean;
        }
    }

    public static <T> T map2Bean(Map<String,Object> source, Class<T> beanClass) {
        if(source == null || source.isEmpty()){
            return null;
        } else if (isNull(beanClass)) {
            return null;
        } else {
            T bean = ReflectUtil.newInstanceIfPossible(beanClass);
            return map2Bean(source, bean);
        }
    }


    private static boolean isNull(Object obj) {
        return obj == null || obj.equals(null);
    }

    /**
     * 单例模式
     */
    private static enum BeanCopierCache {
        INSTANCE;
        /**
         * 弱引用的HashMap
         */
        private final SimpleCache<String, BeanCopier> cache = new SimpleCache();

        private BeanCopierCache() {
        }

        public BeanCopier get(Class<?> srcClass, Class<?> targetClass, Converter converter) {
            String key = genKey(srcClass, targetClass, converter);
            return this.cache.get(key, () -> BeanCopier.create(srcClass, targetClass, converter != null));
        }

        private String genKey(Class<?> srcClass, Class<?> targetClass, Converter converter) {
            StringBuilder sb = new StringBuilder(srcClass.getName()).append("#").append(targetClass.getName());
            if (converter != null) {
                sb.append("#").append(converter.getClass().getName());
            }
            return sb.toString();
        }
    }
}
