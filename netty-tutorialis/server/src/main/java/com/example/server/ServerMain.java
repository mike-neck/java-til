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
package com.example.server;

import com.example.share.GracefulShutdown;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ServerMain {

    private final int port;

    public void run() throws InterruptedException {
        final NioEventLoopGroup parentGroup = new NioEventLoopGroup();
        final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try (final GracefulShutdown ignored =
                     GracefulShutdown.gracefulShutdown(parentGroup::shutdownGracefully, workerGroup::shutdownGracefully)) {
            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(ServerChannelInitializationConfigurer.channelInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            log.info("server start");
            final ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        }
    }
}
