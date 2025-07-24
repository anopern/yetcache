package com.yetcache.example.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import io.netty.buffer.*;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

import java.io.IOException;

/**
 * @author walter.yan
 * @since 2025/7/6
 */
public class Fastjson2Codec extends StringCodec {

    private final Encoder encoder = in -> {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        try (ByteBufOutputStream os = new ByteBufOutputStream(out)) {
            // ✅ 启用 @type 输出，用于自动类型还原
            JSON.writeTo(os, in, JSONWriter.Feature.WriteClassName);
            return os.buffer();
        } catch (Exception e) {
            out.release();
            throw new IOException(e);
        }
    };

    private final Decoder<Object> decoder = (buf, state) -> {
        // ✅ 启用 autoType 支持，Fastjson2 会根据 @type 自动还原对象
        return JSON.parseObject(new ByteBufInputStream(buf), Object.class, JSONReader.Feature.SupportAutoType);
    };

    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }
}

