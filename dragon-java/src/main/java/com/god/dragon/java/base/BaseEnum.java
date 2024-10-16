package com.god.dragon.java.base;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 两个元素的枚举基类,label字符串对应枚举的标签和value是对应枚举的值,
 * 枚举只要实现该接口即可,同时定义2个属性分别是String的label和对应V类型的value
 * @author wuyuxiang
 * @version 1.0.0
 * @implNote start with 2024/7/9 9:48
 */
public interface BaseEnum<V> {
    String getLabel();
    V getValue();

    /**
     * 静态方法,获取某个枚举类下面所有的枚举内容
     * @param enumClass 枚举类
     * @return c用二元组返回
     * @param <V> 枚举对应的Value类型
     * @param <T> 枚举类型
     */
    default <V,T extends BaseEnum<V>> List<ImmutablePair<String,V>> getEnumList(Class<T> enumClass){
        return Arrays.stream(enumClass.getEnumConstants())
                .map(item -> new ImmutablePair<>(item.getLabel(),item.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * 静态方法,获取某个枚举类下面所有的枚举标签
     * @param enumClass 枚举类
     * @return 枚举Label集合
     * @param <V> 枚举对应的Value类型
     * @param <T> 枚举类型
     */
    static <V,T extends BaseEnum<V>> List<String> getEnumLabelList(Class<T> enumClass){
        return Arrays.stream(enumClass.getEnumConstants())
                .map(BaseEnum::getLabel)
                .collect(Collectors.toList());
    }

    /**
     * 静态方法,获取某个枚举类下面所有的枚举值
     * @param enumClass 枚举类
     * @return 枚举Value集合
     * @param <V> 枚举对应的Value类型
     * @param <T> 枚举类型
     */
    static <V,T extends BaseEnum<V>> List<V> getEnumValueList(Class<T> enumClass){
        return Arrays.stream(enumClass.getEnumConstants())
                .map(BaseEnum::getValue)
                .collect(Collectors.toList());
    }

    /**
     * 静态方法,获取某个枚举类下指定label的枚举,因为是静态方法无法获取实际调用的类型,需要传枚举类型
     * 如果放到子类枚举实现则不需要传enumClass
     * @param enumClass 枚举类型
     * @param label 枚举对应的label
     * @return 返回枚举对象
     * @param <V> 枚举对应的Value类型
     * @param <T> 枚举类型
     */
    static <V,T extends BaseEnum<V>> T getEnumByLabel(Class<T> enumClass,String label){
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(item->item.getLabel().equals(label))
                .findFirst()
                .orElseThrow(()->new IllegalArgumentException("不合法的参数"));
    }

    /**
     * 静态方法,获取某个枚举类下指定value的枚举,因为是静态方法无法获取实际调用的类型,需要传枚举类型
     * 如果放到子类枚举实现则不需要传enumClass
     * @param enumClass 枚举类型
     * @param value 枚举对应的value
     * @return 返回枚举对象
     * @param <V> 枚举对应的Value类型
     * @param <T> 枚举类型
     */
    static <V,T extends BaseEnum<V>> T getEnumByValue(Class<T> enumClass,V value){
        if(value == null){
            return Arrays.stream(enumClass.getEnumConstants())
                    .filter(item-> Objects.isNull(item.getValue()))
                    .findFirst()
                    .orElseThrow(()->new IllegalArgumentException("不合法的参数"));
        }else{
            return Arrays.stream(enumClass.getEnumConstants())
                    .filter(item->value.equals(item.getValue()))
                    .findFirst()
                    .orElseThrow(()->new IllegalArgumentException("不合法的参数"));
        }
    }

    /**
     * 静态方法,获取某个枚举类下指定label的枚举value,因为是静态方法无法获取实际调用的类型,需要传枚举类型
     * 如果放到子类枚举实现则不需要传enumClass
     * @param enumClass 枚举类型
     * @param label 枚举对应的label
     * @return 返回枚举对象
     * @param <V> 枚举对应的Value类型
     * @param <T> 枚举类型
     */
    static <V,T extends BaseEnum<V>> V getValueByLabel(Class<T> enumClass,String label){
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(item->item.getLabel().equals(label))
                .findFirst()
                .orElseThrow(()->new IllegalArgumentException("不合法的参数"))
                .getValue();
    }

    /**
     * 静态方法,获取某个枚举类下指定value的枚举label,因为是静态方法无法获取实际调用的类型,需要传枚举类型
     * 如果放到子类枚举实现则不需要传enumClass
     * @param enumClass 枚举类型
     * @param value 枚举对应的value
     * @return 返回枚举对象
     * @param <V> 枚举对应的Value类型
     * @param <T> 枚举类型
     */
    static <V,T extends BaseEnum<V>> String getLabelByValue(Class<T> enumClass,V value){
        if(value == null){
            return Arrays.stream(enumClass.getEnumConstants())
                    .filter(item-> Objects.isNull(item.getValue()))
                    .findFirst()
                    .orElseThrow(()->new IllegalArgumentException("不合法的参数"))
                    .getLabel();
        }else{
            return Arrays.stream(enumClass.getEnumConstants())
                    .filter(item->value.equals(item.getValue()))
                    .findFirst()
                    .orElseThrow(()->new IllegalArgumentException("不合法的参数"))
                    .getLabel();
        }
    }


}
