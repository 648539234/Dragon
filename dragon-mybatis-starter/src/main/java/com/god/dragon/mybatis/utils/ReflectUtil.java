package com.god.dragon.mybatis.utils;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.god.dragon.mybatis.utils
 * @date 2021/10/18 14:01
 */
public class ReflectUtil {
    private static Log log = LogFactory.getLog(ReflectUtil.class);

    public static Field getDeclaredField(Object object,String fieldName){
        Class superClass = object.getClass();
        while (superClass!=Object.class){
            try {
                return superClass.getDeclaredField(fieldName);
            }catch (NoSuchFieldException e){
                superClass = superClass.getSuperclass();
            }
        }
        return null;
    }

    public static void makeAcessible(Field field){
        if(!Modifier.isPublic(field.getModifiers())){
            field.setAccessible(true);
        }
    }

    public static void setIfNull(Object object,String fieldName,Object value){
        Field field = getDeclaredField(object, fieldName);
        if(field!=null){
            makeAcessible(field);
            try{
                Object result = field.get(object);
                if(null == result){
                    field.set(object, value);
                }
            }catch (IllegalAccessException e){
                log.error(e.getMessage() );
            }
        }
    }
}
