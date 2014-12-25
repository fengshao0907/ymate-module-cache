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
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存模块加载器接口实现类；
 *
 * @author 刘镇 (suninformation@163.com) on 14/12/25 下午5:58
 * @version 1.0
 */
public class CachesModule extends AbstractModule {

    @Override
    public void initialize(final Map<String, String> moduleCfgs) throws Exception {
        if (!Caches.getInstance().isInited()) {
            Caches.getInstance().initialize(new ICacheConfig() {
                public ICacheProvider getInternalProviderClass() {
                    String _providerImpl = StringUtils.defaultIfEmpty(moduleCfgs.get("internal_provider_impl"), "ehcache");
                    return ClassUtils.impl(StringUtils.defaultIfEmpty(Caches.__INTERNAL_PROVIDER_CLASS.get(_providerImpl), _providerImpl), ICacheProvider.class, Caches.class);
                }

                public ICacheProvider getExternalProviderClass() {
                    return ClassUtils.impl(moduleCfgs.get("external_provider_impl"), ICacheProvider.class, Caches.class);
                }

                public IObjectSerializer getObjectSerializerClass() {
                    return ClassUtils.impl(StringUtils.defaultIfEmpty(moduleCfgs.get("serializer_impl"), Caches.__SERIALIZER_CLASS.get("java")), IObjectSerializer.class, Caches.class);
                }

                public ICacheEventListener getCacheEventListenerClass() {
                    return ClassUtils.impl(moduleCfgs.get("event_listener_impl"), ICacheEventListener.class, Caches.class);
                }
            });
        }
    }

    @Override
    public void destroy() throws Exception {
        if (Caches.getInstance().isInited()) {
            Caches.getInstance().destroy();
        }
    }

    /**
     * DEMO
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        CachesModule _module = new CachesModule();
        _module.initialize(new HashMap<String, String>());
        //
        CacheTestObj _obj = new CacheTestObj();
        _obj.setAge("123");
        _obj.setName("abc");
        Caches.getInstance().put("test", "name", Caches.getInstance().getObjectSerializer().serialize(_obj));
        System.out.println(Caches.getInstance().getAll("test").size());
        System.out.println(Caches.getInstance().getObjectSerializer().deserialize((byte[]) Caches.getInstance().get("test", "name")));
        //
        Caches _customCache = new Caches();
        _customCache.initialize(new ICacheConfig() {
            public ICacheProvider getInternalProviderClass() {
                return new OSCacheProvider();
            }

            public ICacheProvider getExternalProviderClass() {
                return null;
            }

            public IObjectSerializer getObjectSerializerClass() {
                return new JavaObjectSerializer();
            }

            public ICacheEventListener getCacheEventListenerClass() {
                return new ICacheEventListener() {
                    public void onElementExpired(String cacheName, Object element) {
                        System.out.println(":::onExpired::" + cacheName + "---" + element);
                    }

                    public void onElementPut(String cacheName, Object element) {
                        System.out.println(":::onPut::" + cacheName + "---" + element);
                    }

                    public void onElementRemoved(String cacheName, Object element) {
                        System.out.println(":::onRemoved::" + cacheName + "---" + element);
                    }

                    public void onElementUpdated(String cacheName, Object element) {
                        System.out.println(":::onUpdated::" + cacheName + "---" + element);
                    }

                    public void onRemoveAll(String cacheName) {
                        System.out.println(":::onRemoveAll::" + cacheName);
                    }
                };
            }
        });
        _customCache.put("fuck", "you", "are you sure?");
        System.out.println(_customCache.get("fuck", "you"));
        _customCache.remove("fuck", "you");
        _customCache.destroy();
        //
        _module.destroy();
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
