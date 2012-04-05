/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sohu.jafka.message.compress;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.sohu.jafka.common.UnKnownCodecException;
import com.sohu.jafka.message.CompressionCodec;


/**
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-4-6
 */
public final class CompressionFactory {

    public static CompressionFacade create(CompressionCodec compressionCodec, ByteArrayOutputStream out) {
        return create(compressionCodec, null, out);
    }

    public static CompressionFacade create(CompressionCodec compressionCodec, InputStream in) {
        return create(compressionCodec, in, null);
    }

    private static CompressionFacade create(CompressionCodec compressionCodec,//
            InputStream in,//
            ByteArrayOutputStream out) {
        try {
            switch (compressionCodec) {
                case GZIPCompressionCodec:
                    return new GZIPCompression(in, out);
                default:
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        throw new UnKnownCodecException("Unknown Codec: " + compressionCodec);
    }
}
