package com.phoenix.feature.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.phoenix.feature.Application;
import com.phoenix.feature.impl.FeatureImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class FeatureController {

    @SuppressWarnings("unchecked")
    @RequestMapping("/feature/{feature_id}/{action_id}")
    public Mono<ResponseEntity<String>> call(@PathVariable("feature_id") String feature_id, @PathVariable("action_id") String action_id,
                                             @RequestBody(required = false) String requestBody) {
        FeatureImpl feature = Application.getFeatures().stream().filter(i -> i.getData().value().equals(feature_id)).findFirst().orElseThrow();

        JsonObject object = new JsonObject();
        if(requestBody != null) object = JsonParser.parseString(requestBody).getAsJsonObject();

        if(!feature.getApiSecurity().allow(object))
            return Mono.just(ResponseEntity.badRequest().body("This API is not available"));

        return (Mono<ResponseEntity<String>>) feature.invoke(action_id, object);
    }

}
