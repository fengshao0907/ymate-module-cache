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

import java.util.List;

/**
 * 缓存接口定义
 *
 * @author 刘镇 (suninformation@163.com) on 14/10/17
 * @version 1.0
 */
public interface ICache {

    /**
     * 从缓存中获取对象
     *
     * @param key
     * @return 返回缓存的对象，若不存在则返回null
     * @throws CacheException
     */
    public Object get(Object key) throws CacheException;

    /**
     * 添加对象到缓存
     *
     * @param key
     * @param value
     * @throws CacheException
     */
    public void put(Object key, Object value) throws CacheException;

    /**
     * 更新对象到缓存
     *
     * @param key
     * @param value
     * @throws CacheException
     */
    public void update(Object key, Object value) throws CacheException;

    @SuppressWarnings("rawtypes")
    public List keys() throws CacheException;

    /**
     * 从缓存中移除对象
     */
    public void remove(Object key) throws CacheException;

    /**
     * 批量从缓存中移除对象
     *
     * @param keys
     * @throws CacheException
     */
    @SuppressWarnings("rawtypes")
    public void removeAll(List keys) throws CacheException;

    /**
     * 清理缓存
     */
    public void clear() throws CacheException;

    /**
     * 销毁缓存
     */
    public void destroy() throws CacheException;

}
