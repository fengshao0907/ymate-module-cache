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
package net.ymate.platform.module.cache.serialize;

import java.io.IOException;

/**
 * 缓存对象序列化接口
 *
 * @author 刘镇 (suninformation@163.com) on 14-12-1 上午2:12
 * @version 1.0
 */
public interface IObjectSerializer {

    /**
     * 序列化
     *
     * @param object
     * @return
     * @throws IOException
     */
    public byte[] serialize(Object object) throws IOException;

    /**
     * 返序列化
     *
     * @param bytes
     * @return
     * @throws IOException
     */
    public Object deserialize(byte[] bytes) throws IOException;

}
