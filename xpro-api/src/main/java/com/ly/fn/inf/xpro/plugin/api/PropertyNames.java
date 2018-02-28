package com.ly.fn.inf.xpro.plugin.api;

/**
 * 保留的属性名称定义类。
 */
public class PropertyNames {
    
    public static final String CLIENT_SRV_INFO = "rpc.cli.info";
    /**
     * 穿越DMZ区域标志
     */
    public static final String CROSS_DMS_FLAG = "transport.cross_dmz_flag";

    /**
     * 远程地址信息
     */
    public static final String REMOTE_ADDRESS = "transport.remote_address";

    /**
     * 源端地址信息
     */
    public static final String SOURCE_ADDRESS = "transport.source_address";

    /**
     * 客户端证书CommonName
     */
    public static final String CLIENT_CERT_CN = "transport.client_cert_common_name";

    /**
     * 传输层实现名称
     */
    public static final String TRANSPORT_IMPL = "transport.impl";

    /**
     * 本地传输层版本号
     */
    public static final String LOCAL_TRANSPORT_VERSION = "transport.local_version";

    /**
     * 远程传输层版本号
     */
    public static final String REMOTE_TRANSPORT_VERSION = "transport.remote_version";

    /**
     * 本地Ti-Rpc版本号
     */
    public static final String LOCAL_TI_LNK_VERSION = "inf-rpc.local_version";

    /**
     * 远程Ti-Rpc版本号
     */
    public static final String REMOTE_TI_LNK_VERSION = "inf-rpc.remote_version";

    /**
     * 客户端时间（发出请求时点，没有进行时间差调整）。
     */
    public static final String CLIENT_TIME = "inf-rpc.cli-time";

    /**
     * 压缩标记
     */
    public static final String ZIP_FLAG = "inf-rpc.zip-flag";
}
