package com.ly.fn.inf.xpro.plugin.core.protocol;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

/**
 * @author Mitsui
 * @since 2018年01月04日
 */
@Data
public class XproResponseProtocol {

    /**
     * 消息属性集
     */
    private Map<String, String> messageProps = Maps.newHashMap();

    private byte[] data;
}
