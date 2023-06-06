package com.god.dragon.mybatis.Interceptor;

import com.god.dragon.mybatis.utils.ReflectUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.cglib.core.ReflectUtils;

import java.sql.Timestamp;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.god.dragon.mybatis.Interceptor
 * @date 2021/10/18 12:04
 */
@Intercepts({@Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
)})
public class CommonInterceptor implements Interceptor {
    private static final String DEFAULT_CREATE_TIME_NAME = "createTime";
    private static final String DEFAULT_UPDATE_TIME_NAME = "updateTime";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement)invocation.getArgs()[0];
        Object object = invocation.getArgs()[1];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        if(SqlCommandType.INSERT.equals(sqlCommandType)) {
            ReflectUtil.setIfNull(object, DEFAULT_CREATE_TIME_NAME, currentTime);
            ReflectUtil.setIfNull(object, DEFAULT_UPDATE_TIME_NAME, currentTime);
        }else if(sqlCommandType.UPDATE.equals(sqlCommandType)){
            ReflectUtil.setIfNull(object, DEFAULT_UPDATE_TIME_NAME, currentTime);
        }
        return invocation.proceed();
    }

    //plugin方法在3.5.x已经默认实现 Plugin.wrap(target,this)
}
