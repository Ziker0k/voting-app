package com.ziker0k.voting.server;

import com.ziker0k.voting.server.initializer.VotingServerInitializer;
import com.ziker0k.voting.server.service.ServerConsole;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VotingServer {
    private static final Logger log = LoggerFactory.getLogger(VotingServer.class);
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final int port;


    public VotingServer(int port) {
        this.port = port;
    }

    public void run() {
        try {
            startServer();
        } catch (InterruptedException e) {
            log.error("Server interrupted: {}", e.getMessage(), e);
            Thread.currentThread().interrupt(); // Восстановление флага прерывания
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
        } finally {
            shutdown();
        }
    }

    private void startServer() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new VotingServerInitializer());

        ChannelFuture future = bootstrap.bind(port).sync();
        log.info("Voting server started on port {}", port);

        new ServerConsole().startListening();

//        future.channel().closeFuture().sync();
    }

    private void shutdown() {
        log.info("Shutting down server...");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        log.info("Server shut down.");
    }


//    public static void main(String[] args) {
//        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
//        new VotingServer(port).run();
//    }
}
