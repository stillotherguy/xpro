package com.ly.fn.inf.xpro.plugin.core;

import com.ly.fn.inf.xpro.plugin.core.protocol.MessageContentTypes;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Mitsui
 * @since 2017年1月10日
 */
@Data
@ConfigurationProperties("xpro.config")
public class XproProperties {
    private String contentType = MessageContentTypes.JSON_JACKSON_SMILE;
    private int asyncCallResultHandlerConcurrent = 5;
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
