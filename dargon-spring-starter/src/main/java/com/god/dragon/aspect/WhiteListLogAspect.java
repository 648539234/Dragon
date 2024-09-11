package com.god.dragon.aspect;

import com.alibaba.fastjson.JSONObject;
import com.god.dragon.property.LoggingProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

/**
 * 支持控制层接口日志打印
 *
 * @author wuyuxiang
 * @version 1.0.0
 * @implNote start with 2024/9/11 13:09
 */
@Slf4j
@Aspect // 需要开启AOP
@Component
public class WhiteListLogAspect {

    @Autowired
    private LoggingProperties loggingProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Around("execution(* com..controller..*(..))") // 自定义拦截的Request请求范围,或者拦截所有的@RequestMapping等
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Object result = null;
        Throwable throwable = null;
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return joinPoint.proceed(args);
        }

        HttpServletRequest request = requestAttributes.getRequest();
        if (!shouldLog(request.getRequestURI())) {
            return joinPoint.proceed(args);
        }

        // ContentCachingRequestWrapper 可以在Filter中将HttpServletRequest包一层ContentCachingRequestWrapper
        // 当解析请求体流时会将请求体内容保存在ContentCachingRequestWrapper中的cachedContent中
        if (request instanceof ContentCachingRequestWrapper) {
            logRequestDetails((ContentCachingRequestWrapper) request);
        } else {
            logRequestDetails(request, args);
        }
        try {
            result = joinPoint.proceed(args);
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            logResponseDetails(result, throwable);
        }
        return result;

    }

    private boolean shouldLog(String requestURI) {
        return loggingProperties.isEnabled() &&
                loggingProperties.getIncludePaths().stream().anyMatch(pattern -> antPathMatcher.match(pattern, requestURI));
    }

    private void logRequestDetails(ContentCachingRequestWrapper request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String headers = getHeaders(request);
        String body;
        if (HttpMethod.GET.name().equals(method)) {
            body = buildRequestParam(request.getParameterMap());
        } else {
            body = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        }
        log.info("=============收到外部[{}]HTTP请求,请求URI:{},请求头:{},请求报文:{}=============", method, requestURI, headers, body);
    }

    private void logRequestDetails(HttpServletRequest request, Object[] args) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String headers = getHeaders(request);
        String body = JSONObject.toJSONString(args);
        log.info("=============收到外部[{}]HTTP请求,请求URI:{},请求头:{},请求报文:{}=============", method, requestURI, headers, body);
    }

    private void logResponseDetails(Object result, Throwable throwable) {
        if (throwable != null) {
            log.info("=============响应外部HTTP请求,出现异常,异常信息:{}=============",throwable.getMessage());
        } else {
            log.info("=============响应外部HTTP请求,响应报文:{}=============", JSONObject.toJSONString(result));
        }
    }

    private String getHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.append(headerName).append(":").append(request.getHeader(headerName)).append(";");
        }
        return headers.toString();
    }

    private String buildRequestParam(Map<String, String[]> parameterMap) {
        StringBuilder params = new StringBuilder();
        parameterMap.forEach((k, v) -> params.append(k).append("=").append(Arrays.toString(v)).append(";"));
        if (params.length() > 0) {
            params.deleteCharAt(params.length() - 1);
        }
        return params.toString();
    }
}
