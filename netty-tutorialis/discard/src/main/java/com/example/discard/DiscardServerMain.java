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
package com.example.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DiscardServerMain {

    private final int port;

    public DiscardServerMain(final int port) {
        this.port = port;
    }

    public void run() throws Exception {
        final NioEventLoopGroup bossGroup = new NioEventLoopGroup();//1
        final NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try (AutoCloseable ignore = new ShutDownGracefully(bossGroup, workerGroup)) {
            final ServerBootstrap bootstrap = new ServerBootstrap();//2
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)//3
                    .childHandler(new ChannelInitializer<SocketChannel>() {//4
                        @Override
                        protected void initChannel(final SocketChannel ch) {
                            ch.pipeline().addLast(new DiscardServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)//5
                    .childOption(ChannelOption.SO_KEEPALIVE, true); //6
            final ChannelFuture channelFuture = bootstrap.bind(port).sync();//7
            channelFuture.channel().closeFuture().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        new DiscardServerMain(8000).run();
    }

    private static class ShutDownGracefully implements AutoCloseable {
        private final NioEventLoopGroup bossGroup;
        private final NioEventLoopGroup workerGroup;

        private ShutDownGracefully(final NioEventLoopGroup bossGroup, final NioEventLoopGroup workerGroup) {
            this.bossGroup = bossGroup;
            this.workerGroup = workerGroup;
        }

        @Override
        public void close() throws Exception {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
