package com.jasmine.core.cache.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.jasmine.core.cache.CacheService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author xieshanghan
 * @version GuavaLocalCacheServiceImpl.java, v 0.1 2023年08月05日 23:39 xieshanghan
 */
@Service
public class GuavaLocalCacheServiceImpl implements CacheService {

    private static final int DEFAULT_CONCURRENCY_LEVEL = 8;

    private LoadingCache<Object, Object> cache;

    @Override
    public void initDefaultLocalCache() {
        cache = CacheBuilder.newBuilder()
                //设置并发级别为8，并发级别是指可以同时写缓存的线程数
                .concurrencyLevel(8)
                //设置缓存容器的初始容量为10
                .initialCapacity(10)
                //设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
                .maximumSize(100)
                //是否需要统计缓存情况,该操作消耗一定的性能,生产环境应该去除
                .recordStats()
                //设置写缓存后n秒钟过期
                .expireAfterWrite(60, TimeUnit.SECONDS)
                //设置读写缓存后n秒钟过期,实际很少用到,类似于expireAfterWrite
                //.expireAfterAccess(17, TimeUnit.SECONDS)
                //只阻塞当前数据加载线程，其他线程返回旧值
                //.refreshAfterWrite(13, TimeUnit.SECONDS)
                //设置缓存的移除通知
                .removalListener(notification -> {
                    System.out.println(notification.getKey() + " " + notification.getValue() + " 被移除,原因:" + notification.getCause());
                })
                //build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
                .build(new DefaultGuavaLocalCacheLoader());
    }

    @Override
    public Object getValueFromLocalCache(Object key) {
        try {
            return cache.get(key);
        } catch (Throwable th) {
            return null;
        }
    }

    @Override
    public Boolean putValueToLocalCache(Object key, Object value) {
        try {
            cache.put(key, value);
            return Boolean.TRUE;
        } catch (Throwable th) {
            return Boolean.FALSE;
        }
    }

}