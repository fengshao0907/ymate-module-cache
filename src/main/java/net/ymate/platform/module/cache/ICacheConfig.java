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
/*
 * Copyright (c) 2007-2107, the original author or authors. All rights reserved.
 *
 * This program licensed under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package net.ymate.platform.module.cache;

import net.ymate.platform.module.cache.provider.ICacheProvider;
import net.ymate.platform.module.cache.serialize.IObjectSerializer;

/**
 * 缓存配置接口
 *
 * @author 刘镇 (suninformation@163.com) on 14-12-1 上午2:52
 * @version 1.0
 */
public interface ICacheConfig {

    /**
     * @return JVM缓存(一级)提供者接口实现类
     */
    public ICacheProvider getInternalProviderClass();

    /**
     * @return 外部缓存(二级)提供者接口实现类
     */
    public ICacheProvider getExternalProviderClass();

    public IObjectSerializer getObjectSerializerClass();

    /**
     * @return 缓存数据监听器接口实现类
     */
    public ICacheEventListener getCacheEventListenerClass();

}
