package com.ziker0k.voting.server.command;

import io.netty.channel.ChannelHandlerContext;

public interface ServerCommand {
    String getName();
    void execute(String[] args);
}
