package com.god.dragon.utils.reflect;


import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.pandora.mybatis.utils
 * @date 2023/6/12 17:12
 */
public class ReflectUtil {

    /**
     * 构造对象缓存
     */
    private static final WeakConcurrentMap<Class<?>, Constructor<?>[]> CONSTRUCTORS_CACHE = new WeakConcurrentMap<>();


    private static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class);

    private static Object operate(Object obj, String fieldName,Object fieldVal,String type){
        Object ret = null;
        try{
            Class<? extends Object> classType = obj.getClass();
            Field[] fields = classType.getDeclaredFields();
            for(int i = 0; i < fields.length; i++){
                Field field = fields[i];
                if(field.getName().equals(fieldName)){
                    String firstLetter = fieldName.substring(0,1).toUpperCase();
                    String getMethodName;
                    Method getMethod;
                    if("set".equals(type)){
                        getMethodName = "set" + firstLetter + fieldName.substring(1);
                        getMethod = classType.getMethod(getMethodName, field.getType());
                        ret = getMethod.invoke(obj,fieldVal);
                    }
                    if("get".equals(type)){
                        getMethodName = "get" + firstLetter + fieldName.substring(1);
                        getMethod = classType.getMethod(getMethodName);
                        ret = getMethod.invoke(obj);
                    }
                    return ret;
                }
            }
        }catch (Exception e){
            logger.error("reflect error:"+fieldName,e);
        }
        return ret;
    }

    public static Object getVal(Object obj,String fieldName){
        return operate(obj, fieldName, null,"get");
    }

    public static Object setVal(Object obj,String fieldName,Object fieldVal){
        return operate(obj, fieldName, fieldVal,"set");
    }

    public static Field getDeclaredField(Object object,String filedName){
        Class superClass = object.getClass();
        while(superClass != Object.class){
            try{
                return superClass.getDeclaredField(filedName);
            }catch (NoSuchFieldException e){
                superClass = superClass.getSuperclass();
            }
        }
        return null;
    }

    public static Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes){
        Class superClass = object.getClass();
        while(superClass != Object.class){
            try{
                return superClass.getDeclaredMethod(methodName,parameterTypes);
            }catch (NoSuchMethodException e){
                superClass = superClass.getSuperclass();
            }
        }
        return null;
    }

    private static void makeAccessible(Field field){
        if(!Modifier.isPublic(field.getModifiers())){
            field.setAccessible(true);
        }
    }

    public static Object invokeMethod(Object object,String methodName,Class<?>[] parameterTypes,Object[] parameters) throws InvocationTargetException {
        Method method = getDeclaredMethod(object, methodName, parameterTypes);
        if(method == null){
            throw new IllegalArgumentException("Could not find method ["+methodName+"] on target ["+object+"]");
        }else{
            method.setAccessible(true);
            try{
                return method.invoke(object,parameters);
            }catch (IllegalAccessException e){
                return null;
            }
        }
    }

    public static void setFieldValue(Object object,String fieldName,Object value){
        Field field = getDeclaredField(object,fieldName);
        if(field == null){
            throw new IllegalArgumentException("Could not find field ["+fieldName+"] on target ["+object+"]");
        }else{
            makeAccessible(field);
            try{
                field.set(object,value);
            }catch (IllegalAccessException e){}
        }
    }

    public static Object getFieldValue(Object object,String fieldName){
        Field field = getDeclaredField(object,fieldName);
        if(field == null){
            throw new IllegalArgumentException("Could not find field ["+fieldName+"] on target ["+object+"]");
        }else{
            makeAccessible(field);
            Object result = null;
            try{
                result = field.get(object);
            }catch (IllegalAccessException e){}
            return result;
        }
    }

    public static void setIfNull(Object object,String fieldName,Object value){
        Field field = getDeclaredField(object,fieldName);
        if(field == null){
            throw new IllegalArgumentException("Could not find field ["+fieldName+"] on target ["+object+"]");
        }else{
            makeAccessible(field);
            try{
                Object result = field.get(object);
                if(null == result){
                    field.set(object,value);
                }
            }catch (IllegalAccessException e){}
        }
    }

    /** 查询一个类上带有某个注解的全部属性，包括父类 */
    public static List<Field> findFieldAnnotation(Class clazz, Class<? extends Annotation> annotationType){
        List<Field> fields = new ArrayList();
        while (clazz != null && clazz != Object.class){
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields.stream().filter(item->item.isAnnotationPresent(annotationType)).collect(Collectors.toList());
    }

    /**
     * 将类转成对象取自hutool
     * @param type
     * @return
     * @param <T>
     */
    public static <T> T newInstanceIfPossible(Class<T> type) {
        Assert.notNull(type);

        // 原始类型
        if (type.isPrimitive()) {
            return (T) ClassUtil.getPrimitiveDefaultValue(type);
        }

        // 某些特殊接口的实例化按照默认实现进行
        if (type.isAssignableFrom(AbstractMap.class)) {
            type = (Class<T>) HashMap.class;
        } else if (type.isAssignableFrom(List.class)) {
            type = (Class<T>) ArrayList.class;
        } else if (type.isAssignableFrom(Set.class)) {
            type = (Class<T>) HashSet.class;
        }

        try {
            return newInstance(type);
        } catch (Exception e) {
            // ignore
            // 默认构造不存在的情况下查找其它构造
        }

        // 枚举
        if (type.isEnum()) {
            return type.getEnumConstants()[0];
        }

        // 数组
        if (type.isArray()) {
            return (T) Array.newInstance(type.getComponentType(), 0);
        }

        final Constructor<T>[] constructors = getConstructors(type);
        Class<?>[] parameterTypes;
        for (Constructor<T> constructor : constructors) {
            parameterTypes = constructor.getParameterTypes();
            if (0 == parameterTypes.length) {
                continue;
            }
            setAccessible(constructor);
            try {
                return constructor.newInstance(ClassUtil.getDefaultValues(parameterTypes));
            } catch (Exception ignore) {
                // 构造出错时继续尝试下一种构造方式
            }
        }
        return null;
    }

    public static <T extends AccessibleObject> T setAccessible(T accessibleObject) {
        if (null != accessibleObject && false == accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
    }

    public static <T> Constructor<T>[] getConstructors(Class<T> beanClass) throws SecurityException {
        Assert.notNull(beanClass);
        return (Constructor<T>[]) CONSTRUCTORS_CACHE.computeIfAbsent(beanClass, () -> getConstructorsDirectly(beanClass));
    }

    /**
     * 获得一个类中所有构造列表，直接反射获取，无缓存
     *
     * @param beanClass 类
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    public static Constructor<?>[] getConstructorsDirectly(Class<?> beanClass) throws SecurityException {
        return beanClass.getDeclaredConstructors();
    }

    /**
     * 实例化对象
     *
     * @param <T>    对象类型
     * @param clazz  类
     * @param params 构造函数参数
     * @return 对象
     * @throws UtilException 包装各类异常
     */
    public static <T> T newInstance(Class<T> clazz, Object... params) throws UtilException {
        if (ArrayUtil.isEmpty(params)) {
            final Constructor<T> constructor = getConstructor(clazz);
            if (null == constructor) {
                throw new UtilException("No constructor for [{}]", clazz);
            }
            try {
                return constructor.newInstance();
            } catch (Exception e) {
                throw new UtilException(e, "Instance class [{}] error!", clazz);
            }
        }

        final Class<?>[] paramTypes = ClassUtil.getClasses(params);
        final Constructor<T> constructor = getConstructor(clazz, paramTypes);
        if (null == constructor) {
            throw new UtilException("No Constructor matched for parameter types: [{}]", new Object[]{paramTypes});
        }
        try {
            return constructor.newInstance(params);
        } catch (Exception e) {
            throw new UtilException(e, "Instance class [{}] error!", clazz);
        }
    }

    /**
     * 查找类中的指定参数的构造方法，如果找到构造方法，会自动设置可访问为true
     *
     * @param <T>            对象类型
     * @param clazz          类
     * @param parameterTypes 参数类型，只要任何一个参数是指定参数的父类或接口或相等即可，此参数可以不传
     * @return 构造方法，如果未找到返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        if (null == clazz) {
            return null;
        }

        final Constructor<?>[] constructors = getConstructors(clazz);
        Class<?>[] pts;
        for (Constructor<?> constructor : constructors) {
            pts = constructor.getParameterTypes();
            if (ClassUtil.isAllAssignableFrom(pts, parameterTypes)) {
                // 构造可访问
                setAccessible(constructor);
                return (Constructor<T>) constructor;
            }
        }
        return null;
    }
}
