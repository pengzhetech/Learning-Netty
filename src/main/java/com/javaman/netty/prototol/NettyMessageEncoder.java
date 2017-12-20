package com.javaman.netty.prototol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Map;

/**
 * @author:彭哲
 * @Date:2017/12/20
 */
public class NettyMessageEncoder extends MessageToByteEncoder<NettyMessage> {

    MarshallingEncoder marshallingEncoder;

    public NettyMessageEncoder(MarshallingEncoder marshallingEncoder) {
        this.marshallingEncoder = marshallingEncoder;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage nettyMessage, ByteBuf sendBuf) throws Exception {

        if (nettyMessage == null || nettyMessage.getHeader() == null) {
            throw new Exception("The encode message is null");
        }
        sendBuf.writeInt((nettyMessage.getHeader().getCrcCode()));
        sendBuf.writeInt((nettyMessage.getHeader().getLength()));
        sendBuf.writeByte((nettyMessage.getHeader().getPriority()));
        sendBuf.writeByte((nettyMessage.getHeader().getType()));
        sendBuf.writeLong((nettyMessage.getHeader().getSessionID()));
        sendBuf.writeInt((nettyMessage.getHeader().getAttachment().size()));

        String key = null;
        byte[] keyArray = null;
        Object value = null;
        for (Map.Entry<String, Object> param : nettyMessage.getHeader().getAttachment().entrySet()) {
            key = param.getKey();
            keyArray = key.getBytes("UTF-8");
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
            value = param.getValue();
            marshallingEncoder.encode(value, sendBuf);
        }

        key = null;
        keyArray = null;
        value = null;
        if (nettyMessage.getBody() != null) {
            marshallingEncoder.encode(nettyMessage.getBody(), sendBuf);
        } else
            sendBuf.writeInt(0);
        sendBuf.setInt(4, sendBuf.readableBytes() - 8);

    }
}
