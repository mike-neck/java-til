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
import com.example.share.TimeServerPort;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Slf4j
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    private final ZoneId zoneId = ZoneId.of("UTC");

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        final OffsetDateTime now = OffsetDateTime.now(zoneId);
        final long epochSecond = now.toEpochSecond();

        log.info("channel active: {}", now);

        final ByteBuf byteBuf = ctx.alloc().buffer(8);
        byteBuf.writeLong(epochSecond);
        Futures.toMono(ctx.writeAndFlush(byteBuf))
                .subscribe(v -> ctx.close());
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        log.warn("exception caught on channelActive", cause);
        ctx.close();
    }

    public static void main(String[] args) throws InterruptedException {
        new ServerMain(TimeServerPort.port()).run();
    }
}
