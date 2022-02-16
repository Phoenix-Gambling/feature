package com.phoenix.feature.security;

import com.google.gson.JsonObject;

public interface APISecurity {

    boolean allow(JsonObject requestData);

}
