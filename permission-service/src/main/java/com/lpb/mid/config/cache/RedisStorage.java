package com.lpb.mid.config.cache;

import com.dslplatform.json.JsonReader;
import com.lpb.mid.config.DslJsonCodec;
import com.lpb.mid.config.DslJsonUtils;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RedisStorage<T> {
    private final RedissonClient redissonClient;
//    private final Codec codec;

    public RedisStorage(RedissonClient template) {
//        JsonReader.ReadObject<T> reader = DslJsonUtils.findReader(type);
        this.redissonClient = template;
//        codec = new DslJsonCodec<>(reader);
    }

    public T put(String mapKey, String key, T value) {
        RMap<String, T> map = redissonClient.getMap(mapKey);
        return map.put(key, value);
    }
    public T putMapWithDuration(String mapKey, String key, T value, Duration duration) {
        RMap<String, T> map = redissonClient.getMap(mapKey);
        if (map.isEmpty())
            map.expire(duration);
        return map.put(key, value);
    }

    public T getValue(String mapKey, String key) {
        RMap<String, T> map = redissonClient.getMap(mapKey);
        return Objects.isNull(map) ? null : map.get(key);
    }
    public boolean contains(String mapKey, String key) {
        RMap<String, T> map = redissonClient.getMap(mapKey);
        return !Objects.isNull(map) && map.containsKey(key);
    }

    public Map<String, T> getAll(String mapKey, Set<String> keys) {
        RMap<String, T> map = redissonClient.getMap(mapKey);
        return map.getAll(keys);
    }

    public Map<String, T> getAll(String mapKey) {
        return redissonClient.getMap(mapKey);
    }

    public List<T> values(String mapKey) {
        RMap<String, T> map = redissonClient.getMap(mapKey);
        return map.values().stream().collect(Collectors.toList());
    }

    public List<T> values(String mapKey, String pattern) {
        RMap<String, T> map = redissonClient.getMap(mapKey);
        return new ArrayList<>(map.values(pattern));
    }

    public Set<String> keys(String mapKey) {
        RMap<String, T> map = redissonClient.getMap(mapKey);
        return map.keySet();
    }

    public Set<String> keys(String mapKey, String pattern) {
        RMap<String, T> map = redissonClient.getMap(mapKey);
        return map.keySet(pattern);
    }

    public void putAll(String mapKey, Map<String, T> tMap) {
        RMap<String, T> map = redissonClient.getMap(mapKey);
        map.putAll(tMap);
    }

    public void putWithLock(String mapKey, String key, T value) {
        RMap<String, T> map = redissonClient.getMap(mapKey);
        // Todo khóa entry trước khi put
        RLock lock = map.getLock(key);
        lock.lock();
        try {
            map.put(key, value);
        } finally {
            // Todo mở khóa entry sau khi put
            lock.unlock();
        }
    }

    public void putAllWithLock(String mapKey, Map<String, T> tMap) {
        RMap<String, T> map = redissonClient.getMap(mapKey);
        RLock lock = map.getLock(mapKey);
        try {
            // Todo khóa trong 10s
            lock.lock(10, TimeUnit.SECONDS);
            map.putAll(tMap);
        } finally {
            lock.unlock();
        }
    }
}
