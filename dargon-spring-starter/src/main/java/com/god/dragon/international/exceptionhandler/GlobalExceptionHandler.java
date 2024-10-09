package com.god.dragon.international.exceptionhandler;

import com.god.dragon.international.exception.ServiceException;
import com.god.dragon.international.res.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @implNote start with 2024/10/8 16:06
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public static final String DEFAULT_ERROR_CODE = "999999";
    public static final String DEFAULT_ERROR_MESSAGE = "系统异常,请联系管理员";

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(ServiceException.class)
    public Response<Void> handleServiceException(final ServiceException e, final HttpServletRequest request) {
        return Response.failed(e.getErrorCode(),globalExceptionMessage(e.getErrorCode(), e.getErrorMessage(),e.getMessageArgs()));
    }

    @ExceptionHandler(Exception.class)
    public Response<Void> handleException(final ServiceException e, final HttpServletRequest request) {
        return Response.failed(DEFAULT_ERROR_CODE,globalExceptionMessage(DEFAULT_ERROR_CODE, DEFAULT_ERROR_MESSAGE,null));
    }

    //@Validation 3兄弟
    @ExceptionHandler(BindException.class)
    public Response<Void> handleBindException(final BindException e, final HttpServletRequest request) {
        String message = e.getMessage();
        if(!CollectionUtils.isEmpty(e.getAllErrors())){
            message = e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).filter(Objects::nonNull).collect(Collectors.joining(","));
        }
        log.error(message,e);
        return Response.failed(DEFAULT_ERROR_CODE,message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Response<Void> handleConstraintViolationException(final ConstraintViolationException e, final HttpServletRequest request) {
        String message = e.getMessage();
        if(!CollectionUtils.isEmpty(e.getConstraintViolations())){
            message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).filter(Objects::nonNull).collect(Collectors.joining(","));
        }
        log.error(message,e);
        return Response.failed(DEFAULT_ERROR_CODE,message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Void> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e, final HttpServletRequest request) {
        String message = e.getMessage();
        if(!CollectionUtils.isEmpty(e.getFieldErrors())){
            message = e.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).filter(Objects::nonNull).collect(Collectors.joining(","));
        }
        log.error(message,e);
        return Response.failed(DEFAULT_ERROR_CODE,message);
    }

    private String globalExceptionMessage(String errorCode,String errorMessage,Object[] messageArgs) {
        //1.先根据错误信息获取国际化报错信息
        //2.再根据错误码获取国际化报错信息
        try{
            return messageSource.getMessage(errorMessage, messageArgs, LocaleContextHolder.getLocale());
        }catch (NoSuchMessageException ignore){}

        try{
            return messageSource.getMessage(errorCode, messageArgs, LocaleContextHolder.getLocale());
        }catch (NoSuchMessageException ignore){}

        return errorMessage;
    }
}
