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
package com.example.client;

import com.example.share.GracefulShutdown;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@RequiredArgsConstructor
public class ClientMain {

    private final String host;
    private final int port;

    public void run() throws InterruptedException {
        final NioEventLoopGroup workerLoop = new NioEventLoopGroup();
        try (final GracefulShutdown ignored = GracefulShutdown.gracefulShutdown(workerLoop::shutdownGracefully)) {
            final Bootstrap bootstrap = new Bootstrap(); // ServerBootstrap は常時コネクションを待つようなタイプのソケットに使うらしい
            final ChannelFuture future = bootstrap.group(workerLoop) // 一つだけ イベントループを指定した場合は、 boss ループ/worker ループの双方を一つのループが担当する
                    .channel(NioSocketChannel.class) // NioServerSocketChannel の代わりにクライアントはこちらを使う
                    .option(ChannelOption.SO_KEEPALIVE, true) // サーバーとは異なり parent ループを持たないので、 childOption は使えない
                    .handler(ClientChannelInitializerConfigurer.channelInitializer())
                    .connect(host, port) // bind ではなく connect
                    .sync();
            future.channel().closeFuture().sync();
        }
    }
}
