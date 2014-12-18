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

import net.ymate.platform.base.AbstractModule;
import net.ymate.platform.commons.util.ClassUtils;
import net.ymate.platform.module.cache.provider.ICacheProvider;
import net.ymate.platform.module.cache.provider.impl.EhCacheProvider;
import net.ymate.platform.module.cache.provider.impl.OSCacheProvider;
import net.ymate.platform.module.cache.serialize.IObjectSerializer;
import net.ymate.platform.module.cache.serialize.impl.JavaObjectSerializer;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
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
public class Caches extends AbstractModule {

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

    @Override
    public void initialize(final Map<String, String> moduleCfgs) throws Exception {
        if (!__isInited) {
            initialize(new ICacheConfig() {
                public ICacheProvider getInternalProviderClass() {
                    return ClassUtils.impl(StringUtils.defaultIfEmpty(moduleCfgs.get("internal_provider_impl"), __INTERNAL_PROVIDER_CLASS.get("ehcache")), ICacheProvider.class, Caches.class);
                }

                public ICacheProvider getExternalProviderClass() {
                    return ClassUtils.impl(moduleCfgs.get("external_provider_impl"), ICacheProvider.class, Caches.class);
                }

                public IObjectSerializer getObjectSerializerClass() {
                    return ClassUtils.impl(StringUtils.defaultIfEmpty(moduleCfgs.get("serializer_impl"), __SERIALIZER_CLASS.get("java")), IObjectSerializer.class, Caches.class);
                }

                public ICacheEventListener getCacheEventListenerClass() {
                    return ClassUtils.impl(moduleCfgs.get("event_listener_impl"), ICacheEventListener.class, Caches.class);
                }
            });
        }
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

    @Override
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

    public static void main(String[] args) throws Exception {
//        ICacheProvider _cache = new EhCacheProvider(); // OSCacheProvider();
//        _cache.initialize();
//        ICache _c = _cache.createCache("test", null);
//        _c.put("name", "test");
//        System.out.println(_c.get("name"));
//        _c.remove("name");
//        System.out.println(_c.get("name"));
//        _c.destroy();
//        _cache.destroy();
        Caches _cache = Caches.getInstance();
        _cache.initialize(new HashMap<String, String>());
        //
        CacheTestObj _obj = new CacheTestObj();
        _obj.setAge("123");
        _obj.setName("abc");
        _cache.put("test", "name", _cache.getObjectSerializer().serialize(_obj));
        System.out.println(Caches.getInstance().getAll("test").size());
        //System.out.println(_cache.getObjectSerializer().deserialize((byte[])_cache.get("test", "name")));
        _cache.destroy();
    }

    public static class CacheTestObj implements Serializable {
        private String name;

        private String age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "CacheTestObj{" +
                    "name='" + name + '\'' +
                    ", age='" + age + '\'' +
                    '}';
        }
    }

}
