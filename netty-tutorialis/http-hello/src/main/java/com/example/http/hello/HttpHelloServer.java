/*
 * Copyright 2018 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.http.hello;

import com.example.server.ServerChannelInitializationConfigurer;
import com.example.share.GracefulShutdown;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HttpHelloServer {

    private static final int DEFAULT_PORT = 8080;

    private final int port;

    private void run() throws InterruptedException {
        final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        final NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try (final GracefulShutdown ignored = GracefulShutdown.gracefulShutdown(bossGroup::shutdownGracefully, workerGroup::shutdownGracefully)) {
            final ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(ServerChannelInitializationConfigurer.channelInitializer());

            final Channel channel = serverBootstrap.bind(port).sync().channel();

            log.info("server started: http://localhost:{}/", port);

            channel.closeFuture().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new HttpHelloServer(serverPort(args)).run();
    }

    private static int serverPort(final String[] args) {
        if (args.length == 1) {
            return serverPort(args[0]);
        } else {
            return DEFAULT_PORT;
        }
    }

    private static int serverPort(final String arg) {
        if (!arg.isEmpty() && arg.matches("^\\d+$")) {
            return Integer.parseInt(arg);
        } else {
            return DEFAULT_PORT;
        }
    }
}
