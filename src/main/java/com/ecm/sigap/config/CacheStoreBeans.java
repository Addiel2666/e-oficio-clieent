package com.ecm.sigap.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ecm.sigap.data.cache.CacheStore;


@Configuration
public class CacheStoreBeans {

    @Bean(name = "connectedUsersCache")
    public CacheStore<String> connectedUsersCache() {
        return new CacheStore<String>(10, TimeUnit.MINUTES);
    }
    
  

}