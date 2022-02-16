package com.phoenix.feature.utils;

public interface ThrowableConsumer<T> {

    void accept(T t) throws Exception;

    default void acceptOrThrow(T value) {
        try {
            this.accept(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
