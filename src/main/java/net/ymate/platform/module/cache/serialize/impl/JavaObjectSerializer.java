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
package net.ymate.platform.module.cache.serialize.impl;

import net.ymate.platform.commons.util.RuntimeUtils;
import net.ymate.platform.module.cache.CacheException;
import net.ymate.platform.module.cache.serialize.IObjectSerializer;

import java.io.*;

/**
 * 基于JDK标准序列化
 *
 * @author 刘镇 (suninformation@163.com) on 14-12-1 上午2:15
 * @version 1.0
 */
public class JavaObjectSerializer implements IObjectSerializer {

    public byte[] serialize(Object object) throws IOException {
        ObjectOutputStream _objectOutput = null;
        try {
            ByteArrayOutputStream _arrayOutput = new ByteArrayOutputStream();
            _objectOutput = new ObjectOutputStream(_arrayOutput);
            _objectOutput.writeObject(object);
            return _arrayOutput.toByteArray();
        } finally {
            if (_objectOutput != null) {
                try {
                    _objectOutput.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public Object deserialize(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ObjectInputStream _objectInput = null;
        try {
            ByteArrayInputStream _arrayInput = new ByteArrayInputStream(bytes);
            _objectInput = new ObjectInputStream(_arrayInput);
            return _objectInput.readObject();
        } catch (ClassNotFoundException e) {
            throw new CacheException(RuntimeUtils.unwrapThrow(e));
        } finally {
            if (_objectInput != null) {
                try {
                    _objectInput.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
