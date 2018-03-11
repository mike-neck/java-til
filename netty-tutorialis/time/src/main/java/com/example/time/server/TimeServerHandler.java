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
package com.example.time.server;

import com.example.server.ServerMain;
import com.example.share.Futures;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Slf4j
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    private final ZoneId zoneId = ZoneId.of("UTC");

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        log.info("channel active");
        final ByteBuf byteBuf = ctx.alloc().buffer(8);
        final LocalDateTime now = LocalDateTime.now(zoneId);
        final long epochSecond = now.toInstant(ZoneOffset.UTC).getEpochSecond();
        byteBuf.writeLong(epochSecond);
        Mono.fromFuture(Futures.toCompletableFuture(ctx.writeAndFlush(byteBuf)))
                .subscribe(v -> ctx.close());
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        log.warn("exception caught on channelActive", cause);
        ctx.close();
    }

    public static void main(String[] args) throws InterruptedException {
        new ServerMain(8000).run();
    }
}
