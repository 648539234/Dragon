package com.god.dragon.java.classloader.urlprotocol;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.god.dragon.java.urlprotocol
 * @date 2022/11/10 10:12
 */

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * A parent aware url stream handler factory delegates to a parent
 * url stream handler factory,
 *
 * @author http://svn.apache.org/repos/asf/commons/sandbox/jnet/trunk/src/main/java/org/apache/commons/jnet
 */
public abstract class ParentAwareURLStreamHandlerFactory implements URLStreamHandlerFactory {

    protected URLStreamHandlerFactory parentFactory;

    /**
     * Set the parent factory.
     * @param factory factory
     */
    public void setParentFactory(URLStreamHandlerFactory factory) {
        this.parentFactory = factory;
    }

    /**
     * Return the parent factory.
     * @return The parent factory.
     */
    public URLStreamHandlerFactory getParent() {
        return this.parentFactory;
    }

    /**
     * @see java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
     */
    public URLStreamHandler createURLStreamHandler(String protocol) {
        URLStreamHandler handler = this.create(protocol);
        if ( handler == null && this.parentFactory != null ) {
            handler = this.parentFactory.createURLStreamHandler(protocol);
        }
        return handler;
    }

    /**
     * This method can be overwritten by subclasses to instantiate url stream
     * handlers for the given protocol.
     * @param protocol The protocol.
     * @return A url stream handler for the protocol or null.
     */
    protected abstract URLStreamHandler create(String protocol);
}
