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
package net.ymate.platform.module.cache.provider;

import net.ymate.platform.module.cache.CacheException;
import net.ymate.platform.module.cache.ICache;
import net.ymate.platform.module.cache.ICacheEventListener;

/**
 * 缓存提供者接口
 *
 * @author 刘镇 (suninformation@163.com) on 14/10/17
 * @version 1.0
 */
public interface ICacheProvider {

    /**
     * @return 缓存提供者名称
     */
    public String getName();

    /**
     * 初始化
     *
     * @throws CacheException
     */
    public void initialize() throws CacheException;

    /**
     * 创建缓存对象，若已存在则直接返回
     *
     * @param name
     * @param listener
     * @return
     * @throws CacheException
     */
    public ICache createCache(String name, ICacheEventListener listener) throws CacheException;

    /**
     * 获取缓存对象，若不存在则返回null
     *
     * @param name
     * @return
     */
    public ICache getCache(String name);

    /**
     * 销毁
     *
     * @throws CacheException
     */
    public void destroy() throws CacheException;

}
