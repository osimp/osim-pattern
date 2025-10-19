package org.osimp.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If there is an EntityManager, bound to the current context and this EntityManager
 * has an active connection under the hood, closes the connection, before annotated
 * method is started.
 * <p/>
 * If there's an active transaction, does nothing.
 * <p/>
 * Suppose you have a method, which performs an http query. You want to make sure,
 * that current context does not hold any db connections, while your code waits
 * for the response.
 * <p/>
 * Put this annotation on a method, to release the connection back to the pool,
 * without closing the EnittyManager.
 * <p/>
 * Supposed to be used together with {@link Osim}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReleaseConnection {
}
