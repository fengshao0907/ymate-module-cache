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

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import com.opensymphony.oscache.web.filter.ExpiresRefreshPolicy;
import net.ymate.platform.module.cache.CacheException;
import net.ymate.platform.module.cache.ICache;
import net.ymate.platform.module.cache.ICacheEventListener;
import net.ymate.platform.module.cache.provider.ICacheProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author 刘镇 (suninformation@163.com) on 14/10/17
 * @version 1.0
 */
@Deprecated
public class OSCacheProvider implements ICacheProvider {

    private Map<String, ICache> __CACHES;

    public String getName() {
        return "oscache";
    }

    public void initialize() throws CacheException {
        __CACHES = new ConcurrentHashMap<String, ICache>();
    }

    public ICache createCache(final String name, final ICacheEventListener listener) throws CacheException {
        ICache _cache = __CACHES.get(name);
        if (_cache == null) {
            synchronized (__CACHES) {
                _cache = new ICache() {

                    private String __name = name;

                    GeneralCacheAdministrator __oscache = new GeneralCacheAdministrator();

                    List __cacheKeys = new CopyOnWriteArrayList();

                    public Object get(Object key) throws CacheException {
                        String _key = key.toString();
                        try {
                            return __oscache.getFromCache(_key);
                        } catch (NeedsRefreshException e) {
                            if (e.getCacheContent() != null) {
                                __cacheKeys.remove(_key);
                                __oscache.cancelUpdate(_key);
                                __oscache.removeEntry(_key);
                            }
                        }
                        return null;
                    }

                    public void put(Object key, Object value) throws CacheException {
                        String _key = key.toString();
                        if (!__cacheKeys.contains(_key)) {
                            __cacheKeys.add(_key);
                        }
                        __oscache.putInCache(_key, value, new ExpiresRefreshPolicy(-1));
                        //
                        if (listener != null) {
                            listener.onElementPut(__name, value);
                        }
                    }

                    public void update(Object key, Object value) throws CacheException {
                        put(key, value);
                    }

                    public List keys() throws CacheException {
                        return Collections.unmodifiableList(__cacheKeys);
                    }

                    public void remove(Object key) throws CacheException {
                        String _key = key.toString();
                        if (__cacheKeys.contains(_key)) {
                            //
                            if (listener != null) {
                                listener.onElementRemoved(__name, get(key));
                            }
                            __cacheKeys.remove(_key);
                            __oscache.cancelUpdate(_key);
                            //
                            __oscache.removeEntry(_key);
                        }
                    }

                    public void removeAll(List keys) throws CacheException {
                        String _key = null;
                        for (Object key : keys) {
                            _key.toString();
                            if (__cacheKeys.contains(key)) {
                                //
                                if (listener != null) {
                                    listener.onElementRemoved(__name, get(key));
                                }
                                __oscache.cancelUpdate(key.toString());
                                __oscache.removeEntry(key.toString());
                            }
                        }
                    }

                    public void clear() throws CacheException {
                        __cacheKeys.clear();
                        __oscache.flushAll();
                        //
                        if (listener != null) {
                            listener.onRemoveAll(__name);
                        }
                    }

                    public void destroy() throws CacheException {
                        clear();
                        __CACHES.remove(__name);
                        __oscache.destroy();
                        __name = null;
                        __oscache = null;
                        __cacheKeys = null;
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
    }
}
