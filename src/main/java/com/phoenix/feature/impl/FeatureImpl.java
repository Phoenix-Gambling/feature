package com.phoenix.feature.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.phoenix.feature.annotation.*;
import com.phoenix.feature.security.APISecurity;
import com.phoenix.feature.security.DefaultAPISecurity;
import com.phoenix.feature.utils.ThrowableConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class FeatureImpl {

    private final Object instance;
    private final Feature data;

    private APISecurity apiSecurity = new DefaultAPISecurity();

    private final Map<String, Function<JsonObject, Object>> actions = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(FeatureImpl.class);

    public FeatureImpl(Object instance) {
        this.instance = instance;
        this.data = instance.getClass().getAnnotation(Feature.class);

        this.loadActions();
        this.loadConfig();

        iterateWithAnnotation(Main.class, instance.getClass().getDeclaredMethods(), method -> {
            method.invoke(instance);
        });

        iterateWithAnnotation(Security.class, instance.getClass().getDeclaredFields(), field -> {
            field.setAccessible(true);
            apiSecurity = (APISecurity) field.get(instance);
        });

        if(apiSecurity instanceof DefaultAPISecurity)
            logger.warn("Default API security is used. This feature API is available for everyone.");
    }

    private void loadActions() {
        iterateWithAnnotation(Action.class, instance.getClass().getDeclaredMethods(), method -> {
            Action action = method.getAnnotation(Action.class);

            actions.put(action.value(), (jsonObject) -> {
                try {
                    if(method.getParameterCount() == 0) return method.invoke(instance);
                    return method.invoke(instance, jsonObject);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });

            logger.info(" - @{}", action.value());
        });
    }

    private void loadConfig() {
        iterateWithAnnotation(Config.class, instance.getClass().getDeclaredFields(), field -> {
            field.setAccessible(true);

            String configFileContent = Files.readString(Path.of(System.getProperty("user.dir") + "/config.json"));
            field.set(instance, JsonParser.parseString(configFileContent).getAsJsonObject());
        });
    }

    private <T extends AccessibleObject> void iterateWithAnnotation(Class<? extends Annotation> annotationClass, T[] objects, ThrowableConsumer<T> consumer) {
        List.of(objects).forEach(obj -> {
            if(!obj.isAnnotationPresent(annotationClass)) return;
            consumer.acceptOrThrow(obj);
        });
    }

    public Object invoke(String actionId, JsonObject jsonObject) {
        if(!actions.containsKey(actionId)) throw new RuntimeException("Invalid actionId");
        return actions.get(actionId).apply(jsonObject);
    }

    public APISecurity getApiSecurity() {
        return apiSecurity;
    }

    public Feature getData() {
        return data;
    }

}
