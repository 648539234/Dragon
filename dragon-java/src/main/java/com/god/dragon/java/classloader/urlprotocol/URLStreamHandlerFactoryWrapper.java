package com.god.dragon.java.classloader.urlprotocol;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * This is a wrapper to make a url stream handler factory a parent aware url stream
 *
 * @author  http://svn.apache.org/repos/asf/commons/sandbox/jnet/trunk/src/main/java/org/apache/commons/jnet
 */
public class URLStreamHandlerFactoryWrapper extends ParentAwareURLStreamHandlerFactory {

    protected final URLStreamHandlerFactory wrapper;

    public URLStreamHandlerFactoryWrapper(URLStreamHandlerFactory f) {
        this.wrapper = f;
    }

    /**
     * @see ParentAwareURLStreamHandlerFactory#create(java.lang.String)
     */
    protected URLStreamHandler create(String protocol) {
        return this.wrapper.createURLStreamHandler(protocol);
    }

}
