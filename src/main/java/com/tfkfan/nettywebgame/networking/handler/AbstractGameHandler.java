package com.tfkfan.nettywebgame.networking.handler;


import com.tfkfan.nettywebgame.event.Event;
import com.tfkfan.nettywebgame.event.dispatcher.EventDispatcher;
import com.tfkfan.nettywebgame.event.listener.EventListener;
import com.tfkfan.nettywebgame.networking.message.Message;
import com.tfkfan.nettywebgame.networking.message.MessageType;
import com.tfkfan.nettywebgame.networking.message.impl.outcoming.OutcomingMessage;
import com.tfkfan.nettywebgame.networking.pack.shared.ExceptionPack;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGameHandler<T extends Message> extends SimpleChannelInboundHandler<T> {
    private final EventDispatcher<T> eventDispatcher;

    protected AbstractGameHandler(EventDispatcher<T> eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public <A extends Event> void addEventListener(int messageType, Class<A> eventType,
                                                   EventListener<A> eventListener) {
        eventDispatcher.addEventListener(messageType, eventType, eventListener);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, T msg) {
        eventDispatcher.fireEvent(ctx, msg);
    }

    protected void send(Channel channel, Message message) {
        if (channel != null)
            channel.writeAndFlush(message);
    }

    protected void closeChannelWithFailure(ChannelHandlerContext ctx, String message) {
        Channel channel = ctx.channel();
        channel.writeAndFlush(new OutcomingMessage(MessageType.FAILURE, new ExceptionPack(message)))
                .addListener(ChannelFutureListener.CLOSE);
    }

    protected void closeChannelWithFailure(Channel channel, String message) {
        channel.writeAndFlush(new OutcomingMessage(MessageType.FAILURE, new ExceptionPack(message)))
                .addListener(ChannelFutureListener.CLOSE);
    }

    protected void sendFailure(ChannelHandlerContext ctx, String message) {
        send(ctx.channel(), new OutcomingMessage(MessageType.FAILURE, new ExceptionPack(message)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        sendFailure(ctx, cause.getMessage());
    }
}

