package com.god.dragon.international.exception;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @implNote start with 2024/10/8 16:34
 */

import java.text.MessageFormat;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.pandora.support.exception
 * @date 2023/6/30 10:48
 * @description TODO
 */
public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 3192231000418010424L;
    private String errorCode;
    private String errorMessage;
    private Object[] messageArgs;

    public ServiceException(String message, Object... messageArgs) {
        super(formatMessage(message,messageArgs));
        this.errorCode = message;
        this.errorMessage = message;
        this.messageArgs= messageArgs;
    }

    public ServiceException(String errorCode, String message, Object... messageArgs) {
        this(message, messageArgs);
        this.errorCode = errorCode;
    }

    public ServiceException(String message, Throwable cause, Object... messageArgs) {
        super(formatMessage(message,messageArgs),cause);
        this.errorMessage = message;
        this.errorCode = message;
        this.messageArgs= messageArgs;
    }

    public ServiceException(String errorCode, String message, Throwable cause, Object... messageArgs) {
        this(message, cause, messageArgs);
        this.errorCode = errorCode;
    }

    public ServiceException(IExceptionEnums exceptionEnums, String message, Object... messageArgs) {
        this(exceptionEnums.getCode(),message,messageArgs);
        translateExceptionEnums(exceptionEnums);
    }

    public ServiceException(IExceptionEnums exceptionEnums, Object... messageArgs) {
        this(exceptionEnums.getMessage(),messageArgs);
        translateExceptionEnums(exceptionEnums);
    }

    public ServiceException(IExceptionEnums exceptionEnums, Throwable cause, Object... messageArgs) {
        this(exceptionEnums.getMessage(),cause,messageArgs);
        translateExceptionEnums(exceptionEnums);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Object[] getMessageArgs() {
        return messageArgs;
    }

    public void setMessageArgs(Object[] messageArgs) {
        this.messageArgs = messageArgs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private void translateExceptionEnums(IExceptionEnums exceptionEnums){
        this.errorCode = exceptionEnums.getCategory() + exceptionEnums.getCode();
    }

    /**
     * 格式化异常信息, 异常信息通过{0},{1},{2}...{n}作为占位符,异常参数按照数组顺序填充进去
     * @param message
     * @param messageArgs
     * @return
     */
    private static String formatMessage(String message,Object... messageArgs) {
        return message != null && message.length() > 0 ? MessageFormat.format(message, messageArgs) : "";
    }
}

