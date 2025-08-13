package com.yetcache.core.cache;

import com.yetcache.core.cache.support.CacheValueHolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/12
 */
public final class CacheValueHolderCodec implements ValueCodec {
    // 用“同一种算法”的 valueCodec；这里用 Object 保持去泛型
    private final ValueCodec delegate;

    public CacheValueHolderCodec(ValueCodec delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte[] encode(Object holderObj, Type valueType) throws Exception {
        if (holderObj == null) return null;
        CacheValueHolder holder = (CacheValueHolder) holderObj;
        // 1) 先用“同一套 codec”把内部 value 编成 bytes
        byte[] valueBytes = delegate.encode(holder.getValue(), valueType);

        // 2) 再把 holder 元数据 + valueBytes 组合为统一的二进制（或 JSON）
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeLong(holder.getCreatedTime());
            dos.writeLong(holder.getExpireTime());
            dos.writeLong(holder.getLastAccessTime());
            dos.writeInt(valueBytes == null ? -1 : valueBytes.length);
            if (valueBytes != null) dos.write(valueBytes);
            return bos.toByteArray();
        }
    }

    @Override
    public CacheValueHolder decode(byte[] bytes, Type valueType) throws Exception {
        if (bytes == null || bytes.length == 0) return null;

        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             DataInputStream dis = new DataInputStream(bis)) {
            long created = dis.readLong();
            long expire = dis.readLong();
            long access = dis.readLong();
            int len = dis.readInt();

            byte[] valueBytes = null;
            if (len >= 0) {
                valueBytes = new byte[len];
                dis.readFully(valueBytes);
            }

            // 用“同一套 codec”按调用方提供的 valueType 还原 value
            Object value = (valueBytes == null) ? null : delegate.decode(valueBytes, valueType);

            CacheValueHolder holder = new CacheValueHolder();
            holder.setCreatedTime(created);
            holder.setExpireTime(expire);
            holder.setLastAccessTime(access);
            holder.setValue(value);
            return holder;
        }
    }
}
