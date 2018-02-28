package com.ly.fn.inf.xpro.plugin.core.endpoint;

import com.ly.fn.inf.util.JSON;
import com.ly.fn.inf.util.ZipUtil;
import com.ly.fn.inf.xpro.plugin.api.PropertyNames;
import com.ly.fn.inf.xpro.plugin.core.NotFoundServiceException;
import com.ly.fn.inf.xpro.plugin.core.RpcCallTracker;
import com.ly.fn.inf.xpro.plugin.core.api.AmbiguousMethodException;
import com.ly.fn.inf.xpro.plugin.core.api.ServiceClass;
import com.ly.fn.inf.xpro.plugin.core.api.ServiceMethod;
import com.ly.fn.inf.xpro.plugin.core.protocol.*;
import com.ly.fn.inf.xpro.plugin.core.server.InternalServerObjectRegistry;
import com.ly.fn.inf.xpro.plugin.core.server.ReplyExceptionHelper;
import com.ly.fn.inf.xpro.plugin.core.server.ServiceObject;
import com.ly.fn.inf.xpro.plugin.core.util.AttentionDebugLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;

/**
 * @author Mitsui
 * @since 2018年01月08日
 */
@RestController
@Slf4j
public class ServerController {

    @Autowired
    private ProtocolFactorySelector protocolFactorySelector;

    @Autowired
    private InternalServerObjectRegistry serverObjectRegistry;

    @RequestMapping(path = "/{serviceGroup}/{serviceId}/{method}", method = RequestMethod.POST)
    public Message entrance(@RequestBody Message requestMsg) {
        if (requestMsg.getProperty(PropertyNames.ZIP_FLAG) != null)
            requestMsg.setData(ZipUtil.unzipBytes(requestMsg.getData()));

        RequestInProtocol inProtocol;
        RequestHeader reqHeader;
        ReplyOutProtocol outProtocol;
        ProtocolFactory protocolFactory;
        try {
            protocolFactory = protocolFactorySelector.select(requestMsg.getContentType());
            inProtocol = protocolFactory.newRequestInProtocol(requestMsg);
            reqHeader = inProtocol.readHeader();
            outProtocol = protocolFactory.newReplyOutProtocol(requestMsg.getContentType());
        } catch (Throwable e) {
            log.error("ServerWorker read request msg meet error.", e);
            throw new RuntimeException("ServerWorker read request msg meet error.");
        }

        String trackingCode = reqHeader.getProp(RpcHeaderPropNames.TRACKING_CODE);
        if (trackingCode == null) {
            trackingCode = RpcCallTracker.newTrackingCode();
        }

        RpcCallTracker.setTrackingCode(trackingCode);
        if (log.isDebugEnabled()) {
            log.debug("Work for serviceId=[" + reqHeader.getServiceId() + "], method=[" + reqHeader.getMethod() + "].");
        }

        AttentionDebugLogger.log(log, "receive msg,  serviceId=[" + reqHeader.getServiceId() + "], method=[" + reqHeader.getMethod() + "].");
        try {
            ServiceObject srvObj = serverObjectRegistry.getServiceObjectFinder().getServiceObject(reqHeader.getServiceId(), reqHeader.getMethod());
            ServiceClass srvClass = srvObj.getServiceClass();

            ServiceMethod srvMethod = srvClass.getMethod(reqHeader.getMethod(), reqHeader.getArgsCount(), reqHeader.getArgsSignature());

            // TODO 过载返回
            /*if (System.currentTimeMillis() > (requestMsg.getCreateTime() + srvMethod.getDescription().getTimeout())) {
                // 消息已经超时，服务端出现过载
                AttentionServiceOverloadLogItem logItem = new AttentionServiceOverloadLogItem();
                // logItem.setArgsSignature(srvMethod.getArgsSignature());
                logItem.setCallTime(new java.util.Date(requestMsg.getCreateTime()));
                logItem.setClassName(srvClass.getInfClazz().getName());
                logItem.setDropTime(new java.util.Date());
                logItem.setMethod(srvMethod.getMethod().getName());
                logItem.setServerInfo(ServerInfo.getServerInfo());
                logItem.setServiceId(srvObj.getServiceId());
                logItem.setTrackingCode(trackingCode);

                AttentionServiceLogger.log(logItem);

                log.error("The request to service=[" + srvClass.getDescription().getServiceId() + "].[" + srvMethod.getMethod().getName() + "] is expired.");
                return null;
            }*/

            Method method = srvMethod.getMethod();

            srvObj.invoke(method, inProtocol, outProtocol);
        } catch (Throwable e) {
            if (!ServiceClass.isCallbackObject(reqHeader.getServiceId())) {
                if (e instanceof NotFoundServiceException || e instanceof NoSuchMethodException || e instanceof AmbiguousMethodException) {
                    // 配置出现严重问题，找不到服务
                    log.error("ServerWorker=[" + getId() + "] processing meet error.", e);
                } else {
                    log.error("ServerWorker=[" + getId() + "] processing meet error.", e);
                }
            } else {
                log.error("ServerWorker=[" + getId() + "] processing meet error.", e);
            }

            outProtocol = protocolFactory.newReplyOutProtocol(requestMsg.getContentType());
            ReplyExceptionHelper.replyException(inProtocol, outProtocol, e);
        } finally {
            RpcCallTracker.cleanTrackingCode();
        }

        try {
            Message replyMsg = outProtocol.getMessage();
            return replyMsg;
        } catch (Throwable e) {
            log.error("ServerWorker write reply msg meet error.", e);
        }

        throw new RuntimeException("should not reach here");
    }

    public long getId() {
        return Thread.currentThread().getId();
    }

    public static void main(String[] args) {
        String[] strs = new String[] {"110", "111"};

        byte[] bytes = new byte[] {110, 111};

    }
}
