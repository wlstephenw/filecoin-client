package com.nenglian.filecoin.rpc.jasonrpc.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author stephen
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface JsonRpcService {
}
