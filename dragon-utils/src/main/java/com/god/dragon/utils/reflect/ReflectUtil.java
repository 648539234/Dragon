package com.god.dragon.utils.reflect;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.pandora.mybatis.utils
 * @date 2023/6/12 17:12
 */
public class ReflectUtil {
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
}
