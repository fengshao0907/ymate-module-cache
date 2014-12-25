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
package net.ymate.platform.module.cache.provider.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.ymate.platform.commons.util.RuntimeUtils;
import net.ymate.platform.module.cache.CacheException;
import net.ymate.platform.module.cache.ICache;
import net.ymate.platform.module.cache.ICacheEventListener;
import net.ymate.platform.module.cache.provider.ICacheProvider;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 刘镇 (suninformation@163.com) on 14/10/17
 * @version 1.0
 */
public class EhCacheProvider implements ICacheProvider, CacheEventListener {

    private CacheManager __CACHE_M = new CacheManager();

    private ICacheEventListener __LISTENER;

    private Map<String, ICache> __CACHES;

    public String getName() {
        return "ehcache";
    }

    public void initialize() throws CacheException {
        __CACHE_M = CacheManager.create();
        __CACHES = new ConcurrentHashMap<String, ICache>();
    }

    public ICache createCache(final String name, ICacheEventListener listener) throws CacheException {
        ICache _cache = __CACHES.get(name);
        if (_cache == null) {
            synchronized (__CACHES) {
                Cache __cache = __CACHE_M.getCache(name);
                //
                if (__cache == null) {
                    __LISTENER = listener;
                    __CACHE_M.addCache(name);
                    __cache = __CACHE_M.getCache(name);
                    __cache.getCacheEventNotificationService().registerListener(this);
                }
                final Cache __ehcache = __cache;
                //
                _cache = new ICache() {

                    public Object get(Object key) throws CacheException {
                        if (key != null) {
                            try {
                                Element _element = __ehcache.get(key);
                                if (_element != null) {
                                    return _element.getObjectValue();
                                }
                            } catch (net.sf.ehcache.CacheException e) {
                                throw new CacheException(RuntimeUtils.unwrapThrow(e));
                            }
                        }
                        return null;
                    }

                    public void put(Object key, Object value) throws CacheException {
                        try {
                            __ehcache.put(new Element(key, value));
                        } catch (Exception e) {
                            throw new CacheException(RuntimeUtils.unwrapThrow(e));
                        }
                    }

                    public void update(Object key, Object value) throws CacheException {
                        put(key, value);
                    }

                    public List keys() throws CacheException {
                        return __ehcache.getKeys();
                    }

                    public void remove(Object key) throws CacheException {
                        try {
                            __ehcache.remove(key);
                        } catch (Exception e) {
                            throw new CacheException(RuntimeUtils.unwrapThrow(e));
                        }
                    }

                    public void removeAll(List keys) throws CacheException {
                        __ehcache.removeAll(keys);
                    }

                    public void clear() throws CacheException {
                        __ehcache.removeAll();
                    }

                    public void destroy() throws CacheException {
                        try {
                            __CACHE_M.removeCache(__ehcache.getName());
                            __CACHES.remove(__ehcache.getName());
                        } catch (Exception e) {
                            throw new CacheException(RuntimeUtils.unwrapThrow(e));
                        }
                    }
                };
                __CACHES.put(name, _cache);
            }
        }
        return _cache;
    }

    public ICache getCache(String name) {
        return __CACHES.get(name);
    }

    public void destroy() throws CacheException {
        for (ICache _cache : __CACHES.values()) {
            _cache.destroy();
        }
        __CACHES.clear();
        __CACHES = null;
        //
        __CACHE_M.shutdown();
        __CACHE_M = null;
    }

    public void notifyElementRemoved(Ehcache ehcache, Element element) throws net.sf.ehcache.CacheException {
        if (__LISTENER != null) {
            __LISTENER.onElementRemoved(ehcache.getName(), element.getObjectKey());
        }
    }

    public void notifyElementPut(Ehcache ehcache, Element element) throws net.sf.ehcache.CacheException {
        if (__LISTENER != null) {
            __LISTENER.onElementPut(ehcache.getName(), element.getObjectKey());
        }
    }

    public void notifyElementUpdated(Ehcache ehcache, Element element) throws net.sf.ehcache.CacheException {
        if (__LISTENER != null) {
            __LISTENER.onElementUpdated(ehcache.getName(), element.getObjectKey());
        }
    }

    public void notifyElementExpired(Ehcache ehcache, Element element) {
        if (__LISTENER != null) {
            __LISTENER.onElementExpired(ehcache.getName(), element.getObjectKey());
        }
    }

    public void notifyElementEvicted(Ehcache ehcache, Element element) {
    }

    public void notifyRemoveAll(Ehcache ehcache) {
        if (__LISTENER != null) {
            __LISTENER.onRemoveAll(ehcache.getName());
        }
    }

    public void dispose() {
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
