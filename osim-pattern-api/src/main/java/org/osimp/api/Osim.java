package org.osimp.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Open Session in Method
 * <p/>
 * Creates an EntityManager, before annotated method is started and binds it
 * to the current context.
 * <p/>
 * After the method finished working, closes the EntityManager and unbinds it
 * from the context.
 * <p/>
 * Helps, if you want to perform several transactions within the same
 * session, so that they could share session cache.
 * <p/>
 * If EntityManager is already bound to the context, in case of active
 * transaction fom example, this annotation does nothing
 * <p/>
 * Supposed to be used together with {@link ReleaseConnection}, so that
 * db connections would be released back to the connection pool, when you
 * know, that your code is not going to send db requests for a couple
 * of seconds.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Osim {
}
