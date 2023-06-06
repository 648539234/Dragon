package com.god.dragon.mybatis;

import com.god.dragon.mybatis.Interceptor.CommonInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.god.dragon.mybatis
 * @date 2021/10/18 11:49
 */
@Configuration
@ConditionalOnProperty(
        name = {"dragon.mybatis.enabled"},
        havingValue = "true",
        matchIfMissing = true
)
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan({"com.dragon.**.mapper"}) //**可以中间过滤多个 *只能过滤一个
public class MybatisConfiguration {

    @Bean("commonInterceptor")
    @ConditionalOnMissingBean(name={"commonInterceptor"})
    public Interceptor commonInterceptor(){
        CommonInterceptor interceptor = new CommonInterceptor();
        return interceptor;
    }

}
