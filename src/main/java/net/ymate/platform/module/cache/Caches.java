/*
 * Copyright 2007-2107 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ymate.platform.module.cache;

import net.ymate.platform.module.cache.provider.ICacheProvider;
import net.ymate.platform.module.cache.provider.impl.EhCacheProvider;
import net.ymate.platform.module.cache.provider.impl.OSCacheProvider;
import net.ymate.platform.module.cache.serialize.IObjectSerializer;
import net.ymate.platform.module.cache.serialize.impl.JavaObjectSerializer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存管理器
 *
 * @author 刘镇 (suninformation@163.com) on 14-10-16
 * @version 1.0
 */
public class Caches {

    protected static Map<String, String> __SERIALIZER_CLASS = new HashMap<String, String>();
    protected static Map<String, String> __INTERNAL_PROVIDER_CLASS = new HashMap<String, String>();

    static {
        __SERIALIZER_CLASS.put("java", JavaObjectSerializer.class.getName());
        //
        __INTERNAL_PROVIDER_CLASS.put("ehcache", EhCacheProvider.class.getName());
        __INTERNAL_PROVIDER_CLASS.put("oscache", OSCacheProvider.class.getName());
    }

    private static Caches __current = new Caches();

    private ICacheProvider __internalCacheProvider;

    private ICacheProvider __externalCacheProvider;

    private IObjectSerializer __objectSerializer;

    private ICacheEventListener __cacheEventListener;

    private boolean __isInited;

    /**
     * @return 返回缓存管理器实体对象
     */
    public static Caches getInstance() {
        return __current;
    }

    public void initialize(ICacheConfig config) throws Exception {
        if (!__isInited) {
            __internalCacheProvider = config.getInternalProviderClass();
            __internalCacheProvider.initialize();
            __externalCacheProvider = config.getExternalProviderClass();
            if (__externalCacheProvider != null) {
                __externalCacheProvider.initialize();
            }
            __objectSerializer = config.getObjectSerializerClass();
            __cacheEventListener = config.getCacheEventListenerClass();
            //
            __isInited = true;
        }
    }

    public IObjectSerializer getObjectSerializer() {
        return __objectSerializer;
    }

    public boolean isInited() {
        return __isInited;
    }

    public void destroy() throws Exception {
        if (__isInited) {
            __isInited = false;

            __internalCacheProvider.destroy();
            if (__externalCacheProvider != null) {
                __externalCacheProvider.destroy();
            }
            __cacheEventListener = null;
            __objectSerializer = null;
        }
    }

    /**
     * 从缓存中获取对象
     *
     * @param cacheName
     * @param key
     * @return 返回缓存的对象，若不存在则返回null
     * @throws CacheException
     */
    public Object get(String cacheName, Object key) throws CacheException {
        ICache _cache = __internalCacheProvider.getCache(cacheName);
        if (_cache != null) {
            return _cache.get(key);
        }
        return null;
    }

    /**
     * 从缓存中获取所有对象
     *
     * @param cacheName
     * @return 返回缓存内对象映射
     * @throws CacheException
     */
    public Map<Object, Object> getAll(String cacheName) throws CacheException {
        Map<Object, Object> _returnValue = new HashMap();
        for (Object key : keys(cacheName)) {
            _returnValue.put(key, get(cacheName, key));
        }
        return _returnValue;
    }

    /**
     * 添加对象到缓存
     *
     * @param cacheName
     * @param key
     * @param value
     * @throws CacheException
     */
    public void put(String cacheName, Object key, Object value) throws CacheException {
        ICache _cache = __internalCacheProvider.getCache(cacheName);
        if (_cache == null) {
            _cache = __internalCacheProvider.createCache(cacheName, __cacheEventListener);
        }
        _cache.put(key, value);
    }

    /**
     * 更新对象到缓存
     *
     * @param cacheName
     * @param key
     * @param value
     * @throws CacheException
     */
    public void update(String cacheName, Object key, Object value) throws CacheException {
        put(cacheName, key, value);
    }

    @SuppressWarnings("rawtypes")
    public List keys(String cacheName) throws CacheException {
        ICache _cache = __internalCacheProvider.getCache(cacheName);
        if (_cache != null) {
            return _cache.keys();
        }
        return Collections.emptyList();
    }

    /**
     * 从缓存中移除对象
     *
     * @param cacheName
     * @param key
     * @throws CacheException
     */
    public void remove(String cacheName, Object key) throws CacheException {
        ICache _cache = __internalCacheProvider.getCache(cacheName);
        if (_cache != null) {
            _cache.remove(key);
        }
    }

    /**
     * 批量从缓存中移除对象
     *
     * @param cacheName
     * @param keys
     * @throws CacheException
     */
    @SuppressWarnings("rawtypes")
    public void removeAll(String cacheName, List keys) throws CacheException {
        ICache _cache = __internalCacheProvider.getCache(cacheName);
        if (_cache != null) {
            _cache.removeAll(keys);
        }
    }

    /**
     * 清理缓存
     *
     * @param cacheName
     * @throws CacheException
     */
    public void clear(String cacheName) throws CacheException {
        ICache _cache = __internalCacheProvider.getCache(cacheName);
        if (_cache != null) {
            _cache.clear();
        }
    }

}
