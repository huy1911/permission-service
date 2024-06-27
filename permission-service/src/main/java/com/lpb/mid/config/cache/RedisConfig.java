package com.lpb.mid.config.cache;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private String redisPost;
    @Value("${spring.redis.password}")
    private String redisPassword;
    @Value("${spring.redis.poolSize:20}")
    private int poolSize;
    @Value("${spring.redis.minimumIdleSize:5}")
    private int minimumIdleSize;
    @Bean
    public RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer()
                .setPassword(redisPassword)
                .setConnectionPoolSize(poolSize)
                .setConnectionMinimumIdleSize(minimumIdleSize)
                .setAddress("redis://" + redisHost + ":" + redisPost)
                .setTimeout(5000)
                .setIdleConnectionTimeout(30000)
                .setRetryAttempts(3)
                .setRetryInterval(3000)
                .setPingConnectionInterval(60000)
                .setKeepAlive(false);

        return Redisson.create(config);
    }

}