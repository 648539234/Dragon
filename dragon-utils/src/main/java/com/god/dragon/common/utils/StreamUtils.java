package com.god.dragon.common.utils;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 增强peek,map,foreach等方法的功能，使其可以获取到当前元素的下标,不支持并行流
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.god.dragon.common.utils
 * @date 2023/9/26 16:26
 * @description TODO
 */
public class StreamUtils {

    /**
     *  增强peek,foreach等方法的功能，使其可以获取到当前元素的下标,不支持并行流
     *  <pre>
     *        List.newArrayList("1","2","3").stream().forEach(StreamUtils.consumeWithIndex((index,element)->{
     *            System.out.println("index:"+index+",element:"+element);
     *        }));
     *  </pre>
     * @param consumer
     * @param <T>
     * @return
     */
    public static <T> Consumer<T> consumeWithIndex(BiConsumer<Integer, T> consumer){
        class Index{
            int index = 0;
        }
        Index i = new Index();
        return t -> consumer.accept(i.index++,t);
    }

    /**
     * 增强map等方法的功能，使其可以获取到当前元素的下标,不支持并行流
     * <pre>
     *     List.newArrayList("1","2","3").stream().map(StreamUtils.functionWithIndex((index,element)->{
     *         System.out.println("index:"+index+",element:"+element);
     *         return element;
     *     }));
     * </pre>
     * @param function
     * @param <U>
     * @param <R>
     * @return
     */
    public static <U,R> Function<U,R> functionWithIndex(BiFunction<Integer, U, R> function){
        class Index{
            int index = 0;
        }
        Index i = new Index();
        return t -> function.apply(i.index++,t);
    }
}
