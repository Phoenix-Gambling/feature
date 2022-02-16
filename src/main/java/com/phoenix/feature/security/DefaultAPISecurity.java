package com.phoenix.feature.security;

import com.google.gson.JsonObject;

public class DefaultAPISecurity implements APISecurity {

    @Override public boolean allow(JsonObject requestData) {
        return true;
    }

}
