package com.nenglian.filecoin.rpc.jasonrpc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author stephen
 */
public interface CallAdapter<R, T> {

    Type responseType();

    T adapt(Call<R> call);

    abstract class Factory {
        public abstract CallAdapter<?, ?> get(
            Type returnType, Annotation[] annotations, JsonRpcRetrofit retrofit);

        protected static Class<?> getRawType(Type type) {
            return Utils.getRawType(type);
        }
    }
}
