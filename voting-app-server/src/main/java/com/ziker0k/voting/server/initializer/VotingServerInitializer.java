package com.ziker0k.voting.server.initializer;

import com.ziker0k.voting.server.handler.VotingServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


public class VotingServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast(
                new StringDecoder(),
                new StringEncoder(),
                new VotingServerHandler());
    }
}