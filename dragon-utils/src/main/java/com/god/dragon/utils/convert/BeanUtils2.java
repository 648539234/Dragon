package com.god.dragon.utils.convert;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.god.dragon.utils.convert
 * @date 2023/2/8 10:31
 */
public class BeanUtils2 {

    public static Map bean2Map(Object beanObject){
        return bean2Map(beanObject, null);
    }

    public static Map bean2Map(Object beanObject,CustomMapper mapper){
        if(Objects.isNull(beanObject)){
            return new HashMap(0);
        }
        if(beanObject instanceof Map){
            return (Map) beanObject;
        }
        BeanWrapperImpl bean = new BeanWrapperImpl(beanObject);
        PropertyDescriptor desc[] = bean.getPropertyDescriptors();
        Map dataMap = new HashMap(desc.length);
        for(int i = 0; i < desc.length; i++){
            String name = desc[i].getName();
            //PropertyDescriptor会获取一些非属性字段包括一些类描述name是class的,所以需要过滤掉
            if(!"class".equals(name) && bean.isReadableProperty(name)){
                Object object = bean.getPropertyValue(name);
                if(object != null){
                    if(object instanceof List){
                        List innerDataList = (List)object;
                        List dataList = new ArrayList();
                        for(Object inner : innerDataList){
                            Class innerClass = inner.getClass();
                            if(!isPrimitive(innerClass)){ //List对象需要递归处理进行深拷贝
                                Map innerDataMap = bean2Map(inner);
                                dataList.add(innerDataMap);
                            }else{
                                dataList.add(inner);
                            }
                        }
                        dataMap.put(name, dataList);
                    } else {
                        //此处可以通过isPrimitive(object.getClass())判断object是否是基本类
                        //根据业务需要继续做深度拷贝,此处没有做深拷贝
                        dataMap.put(name, object);
                    }
                }
            }
        }
        if(mapper!=null) { //plus扩展
            mapper.mapAtoB(beanObject, dataMap);
        }
        return dataMap;
    }

    public static <T> T map2Bean(Map map,Class<T> clazz){
        return map2Bean(map, clazz, null);
    }

    public static <T> T map2Bean(Map map,Class<T> clazz,CustomMapper customMapper){
        if(null == map || map.size() == 0){
            return null;
        }

        if(Map.class.isAssignableFrom(clazz)){
            return (T)map;
        }

        BeanWrapperImpl bw = new BeanWrapperImpl(clazz);
        PropertyDescriptor desc[] = bw.getPropertyDescriptors();
        for(int i = 0; i < desc.length; i++){
            PropertyDescriptor pd = desc[i];
            String name = pd.getName();
            if(bw.isReadableProperty(name) && bw.isReadableProperty(name)){ //这个属性具备get和set方法
                Class innerClazz = pd.getPropertyType();
                Object value = map.get(name);
                if(value == null){
                    continue; //Pojo属性名在Map中没找到就直接跳过
                }
                if(Enum.class.isAssignableFrom(innerClazz)){
                    //如果value也是枚举类型就直接赋值,如果value不是枚举类型就根据String值去对应的枚举类型中找到对应的枚举值
                    //例如 Pojo对象中属性是 xxxEnum,Map对象中属性是"HELLO",就会去xxxEnum找对应字符串的枚举值并赋值
                    if(value.getClass() == innerClazz){
                        bw.setPropertyValue(name,value);
                    }else{
                        String enumValue = String.valueOf(value);
                        if(enumValue.length()>0){
                            Enum v = Enum.valueOf(innerClazz,enumValue);
                            bw.setPropertyValue(name,v);
                        }
                    }
                } else if(List.class.isAssignableFrom(innerClazz)
                        && value instanceof List){
                    List dataList = new ArrayList();
                    if(((List) value).isEmpty()){
                        bw.setPropertyValue(name,dataList);
                    }
                    Class actualType = getActualType(clazz,name);
                    if(actualType != null){
                        for(Object inner:(List)value){
                            if(inner instanceof Map){
                                dataList.add(map2Bean((Map)inner,actualType));
                            }else{
                                dataList.add(beanCopy(inner,actualType));
                            }
                        }
                        bw.setPropertyValue(name,dataList);
                    }
                } else if (!isPrimitive(innerClazz)){
                    if(value instanceof Map){
                        bw.setPropertyValue(name,map2Bean((Map)value, innerClazz));
                    }else{
                        bw.setPropertyValue(name,beanCopy(value, innerClazz));
                    }
                } else if (isPrimitive(innerClazz)){
                    bw.setPropertyValue(name,value);
                }
            }
        }
        T result = (T) bw.getWrappedInstance();

        if(customMapper!=null){ //plus扩展
            customMapper.mapAtoB(map, result);
        }

        return result;
    }

    public static <T> T beanCopy(Object src, Class<T> target) {
        return beanCopy(src, target, null);
    }

    public static <T> T beanCopy(Object src, Class<T> target,CustomMapper mapper) {
        BeanWrapper srcBw = new BeanWrapperImpl(src);
        PropertyDescriptor srcProps[] = srcBw.getPropertyDescriptors();
        BeanWrapper targetBw = new BeanWrapperImpl(target);

        for(PropertyDescriptor pd : srcProps){
            String name = pd.getName();
            if(targetBw.isReadableProperty(name) && targetBw.isWritableProperty(name)){
                Class srcClazz = pd.getPropertyType();
                Class targetClazz = null;
                try{
                    PropertyDescriptor targetPd = targetBw.getPropertyDescriptor(name);
                    targetClazz = targetPd.getPropertyType();
                }catch (Exception e){} //目标没有对应的属性,忽略即可
                Object value = srcBw.getPropertyValue(name);
                if(targetClazz == null || value == null){ continue;}
                if(srcClazz.isAssignableFrom(targetClazz)){
                    targetBw.setPropertyValue(name, value);
                } else {
                    //如果属性对象类型也不一致就再转成对应的类型
                    Object fieldValue = beanCopy(value, targetClazz);
                    targetBw.setPropertyValue(name, fieldValue);
                }
            }
        }
        T result = (T) targetBw.getWrappedInstance();
        if(mapper!=null){
            mapper.mapAtoB(src,result);
        }
        return result;
    }

    public static List listBean2ListMap(List list){
        if(list == null || list.isEmpty()){
            return null;
        }
        List dataList = new ArrayList();
        for(Object data : list){
            Map innerDataMap = bean2Map(data);
            dataList.add(innerDataMap);
        }
        return dataList;
    }

    public static <T> List<T> listMap2ListBean(List<Map> list,Class<T> target){
        if(list == null || list.isEmpty()){
            return null;
        }
        List dataList = new ArrayList();
        for(Map data : list){
            T value = map2Bean(data, target);
            dataList.add(value);
        }
        return dataList;
    }

    /**
     * 校验是否是基本类型的类(这些类型就不用转换成Map)
     * @param clazz
     * @return
     */
    private static boolean isPrimitive(Class clazz) {
        //isPrimitive方法表示clazz是否是基本类型的,包括8大基本类型和void类型的
        //是否是基本类型的包装类可以用如下方式一个个判断也可以直接((Class) clz.getField("TYPE").get(null)).isPrimitive();
        //因为基本类型的包装类有个TYPE属性,通过get(null)可以获取起对应的基本类型
        //Object类型是可以由BeanWrapper读取的,但由于Object类型没有指定明确的类型,避免出现是基本类型所以就排除掉
        return clazz.isPrimitive() ||
                clazz.isArray()  ||
                clazz.isEnum() ||
                Object.class.equals(clazz) ||
                String.class.equals(clazz) ||
                Boolean.class.isAssignableFrom(clazz) ||
                Character.class.isAssignableFrom(clazz) ||
                Byte.class.isAssignableFrom(clazz) ||
                Short.class.isAssignableFrom(clazz) ||
                Integer.class.isAssignableFrom(clazz) ||
                Long.class.isAssignableFrom(clazz) ||
                Float.class.isAssignableFrom(clazz) ||
                Double.class.isAssignableFrom(clazz) ||
                BigDecimal.class.isAssignableFrom(clazz) ||
                BigInteger.class.isAssignableFrom(clazz) ||
                Date.class.isAssignableFrom(clazz);
    }

    /**
     * 获取集合元素泛型的具体类型,例如List<String> 返回的就是String的Class类,没有就返回null
     * @param clazz
     * @param fieldName
     * @return
     */
    private static Class getActualType(Class clazz,String fieldName){
        Field field = getFieldRecursion(clazz,fieldName);
        if(field != null){
            Type fc = field.getGenericType(); //可以获取Pojo中属性的泛型
            if(fc instanceof ParameterizedType){ //如果有泛型fc就是ParameterizedType实现类
                ParameterizedType pt = (ParameterizedType) fc;
                Object object = pt.getActualTypeArguments()[0]; //获取第一个泛型的类型
                if(object instanceof Class){
                    return (Class)object; //如果是List<普通对象>,List<Map>时返回对应的类型
                }
                if(object instanceof ParameterizedType){
                    //如果是List<Map<xxx,xxx>>情况下会进入这里,这里不需要解析Map里面的泛型,只需要解析属性是Map就返回Map类型
                    String innerClazz = ((ParameterizedType) object).getRawType().getTypeName();
                    if("java.utils.Map".equals(innerClazz)){
                        return Map.class;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据属性名称获取这个类中对应的属性Field,包括父类中的Field
     * @param clazz
     * @param fieldName
     * @return
     */
    private static Field getFieldRecursion(Class clazz, String fieldName) {
        Field field = null;
        try{
            field = clazz.getDeclaredField(fieldName);
        }catch (Exception e){}

        Class superClass = clazz.getSuperclass();
        if(field == null && superClass != null){
            field = getFieldRecursion(superClass, fieldName);
        }
        return field;
    }

    /**
     * 自定义转换器,只支持浅拷贝里面的扩展
     * @param <T>
     * @param <U>
     */
    interface CustomMapper<T,U>{
        /**
         * 自定义转换
         * @param src 源数据
         * @param target 目标数据
         */
        void mapAtoB(T src,U target);
    }
}
