package com.ziker0k.voting.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

public class VotingClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8060;

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new VotingClientInitializer());

            Channel channel = bootstrap.connect(HOST, PORT).sync().channel();

            System.out.println("Connected to server!");

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                if ("exit".equalsIgnoreCase(command)) {
                    break;
                }
                channel.writeAndFlush(command + "\n");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
