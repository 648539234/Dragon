package com.god.dragon.java.classloader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.god.dragon.java.classloader
 * @date 2022/11/10 11:11
 * @description 自定义类加载器,加载自定义的jar包文件,类似于LaunchedURLClassLoader（SpringBoot-可执行jar包读取jar包中jar包的原理）
 */
public class TestClassLoader {
    public static void main(String[] args) throws Exception {
        String jarPath = "META-INF/dragon-utils-0.0.1-SNAPSHOT.jar";
        String jarPath2 = "D:\\项目\\myjar\\com\\god\\dragon\\dragon-utils\\0.0.1-SNAPSHOT\\dragon-utils-0.0.1-SNAPSHOT.jar";
        List<String> classpath = new ArrayList<>();
        classpath.add(jarPath);
        ImplementationLoader loader = new ImplementationLoader(classpath);
        Object o = loader.load("com.god.dragon.utils.Builder.UserDao2");
    }
}
