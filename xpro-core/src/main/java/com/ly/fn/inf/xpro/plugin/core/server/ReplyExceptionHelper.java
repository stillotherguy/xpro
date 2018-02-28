package com.ly.fn.inf.xpro.plugin.core.server;

import com.ly.fn.inf.xpro.plugin.core.protocol.ReplyHeader;
import com.ly.fn.inf.xpro.plugin.core.protocol.ReplyOutProtocol;
import com.ly.fn.inf.xpro.plugin.core.protocol.RequestHeader;
import com.ly.fn.inf.xpro.plugin.core.protocol.RequestInProtocol;

import java.lang.reflect.InvocationTargetException;

/**
 * 应答异常帮助类。
 */
public class ReplyExceptionHelper {
    public static void replyException(RequestInProtocol in, ReplyOutProtocol out, Throwable e) {
        if (e instanceof InvocationTargetException) {
            e = ((InvocationTargetException) e).getTargetException();
        }

        ReplyHeader replyHeader = new ReplyHeader();
        replyHeader.setServerInfo(AppInfo.getInstance().toAddress());
        RequestHeader reqHeader = in.readHeader();
        if (reqHeader.getCrossDMZFlag() != null && reqHeader.getCrossDMZFlag().booleanValue()) {
            // Cross DMZ, convert exception to simpleException.
            // 跨越安全区域，去除服务器信息
            replyHeader.setServerInfo(null);
        }

        replyHeader.setType(ReplyHeader.TYPE_EXCEPTION);
        out.writeHeader(replyHeader);
        out.writeException(e);
    }
}
