package com.nenglian.filecoin.rpc.jasonrpc.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


import com.nenglian.filecoin.rpc.jasonrpc.JsonRpcParamsMode;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author stephen
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface JsonRpcMethod {

    String value();

    JsonRpcParamsMode paramsPassMode() default JsonRpcParamsMode.AUTO;
}
