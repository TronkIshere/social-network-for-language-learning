package com.private_project.charitable_money_management.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {
    void save(String key, String value);
    void save(String key, String value, long duration, TimeUnit timeUnit);
    String get(String key);
    void delete(String key);
}

