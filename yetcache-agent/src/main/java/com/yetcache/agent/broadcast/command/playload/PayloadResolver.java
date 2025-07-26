package com.yetcache.agent.broadcast.command.playload;


import com.yetcache.agent.broadcast.command.descriptor.CommandDescriptor;

import java.util.Map;

/**
 * 平台治理结构：结构行为的数据解析与构造器。
 *
 * @param <T> 表示结构化数据类型，例如 DynamicHashData、KvPutData 等
 */
public interface PayloadResolver<T> {

    /**
     * 当前解析器是否支持给定的指令行为（结构 + 动作）。
     * 通常可通过 CommandDescriptorKey 进行判断。
     */
    boolean supports(CommandDescriptor descriptor);

    /**
     * 正向解析：接收端使用。
     * 从通用 payload Map 结构中提取出结构化业务对象（如 key + fieldValueMap）。
     */
    T resolve(Map<String, Object> payload);

    /**
     * 反向构造：发送端使用。
     * 将结构化业务对象转为标准 payload Map（用于广播构造）。
     */
    Map<String, Object> serialize(T value);
}