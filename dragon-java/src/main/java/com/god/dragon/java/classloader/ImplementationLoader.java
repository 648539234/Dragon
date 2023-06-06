package com.god.dragon.java.classloader;

import com.god.dragon.java.classloader.urlprotocol.ResInJarURLStreamHandlerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.god.dragon.java.classloader
 * @date 2022/11/10 10:14
 * @description 加载jar包中的类,用于该项目被其他项目依赖时，其他项目能够使用这些类，否则其他项目就需要加载jar包中的jar包，普通的类加载器会报错
 */
public class ImplementationLoader {
    static {
        //安装URL协议:resinjar
        ResInJarURLStreamHandlerFactory.install();
    }

    private final FilteringDelegateClassLoader classLoader;

    /**
     * jar包的类加载路径
     * @param jarPaths
     */
    public ImplementationLoader(List<String> jarPaths){
        List<URL> jarUrls = new ArrayList<>();
        for(String jarPath:jarPaths){
            try {
                jarUrls.add(new URL(ResInJarURLStreamHandlerFactory.PROTOCOL+":"+jarPath));
            } catch (MalformedURLException e) {
                throw new RuntimeException("fail load internal jar url to FilteringDelegateClassLoader,url:"+ResInJarURLStreamHandlerFactory.PROTOCOL+":"+jarPath,e);
            }
        }

        URL[] urls = new URL[jarUrls.size()];
        jarUrls.toArray(urls);
        classLoader = new FilteringDelegateClassLoader(urls,getClass().getClassLoader()){

            @Override
            protected boolean isClassDelegatedByParent(String name) {
                //TODO 此处用于判断类是否需要父类加载器去加载，false打破双亲委派，true让父加载器去加载
                return false;
            }
        };
    }

    /**
     * 类加载
     * @param className 加载的类名
     * @param <T> 返回对应的实例对象(需要有空构造)
     * @return
     */
    public <T> T load(String className){
        try {
            return (T) classLoader.loadClass(className).newInstance();
        } catch (Throwable e) {
            throw new RuntimeException("fail load from internal jar with FilteringDelegateClassLoader,classname"+className,e);
        }
    }

    /**
     * 打印当前类加载器
     * @return
     */
    public String getInternalClassLoaderInfo(){
        return printClassLoaderParent(classLoader);
    }

    public static String printClassLoaderParent(ClassLoader classLoader){
        if(classLoader == null){
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(classLoader);

        while ((classLoader = classLoader.getParent())!=null){
            sb.append("\n").append(classLoader);
        }
        return sb.toString();
    }
}
