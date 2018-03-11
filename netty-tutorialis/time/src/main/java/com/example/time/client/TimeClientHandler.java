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
package com.example.time.client;

import com.example.share.GracefulShutdown;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        final ByteBuf byteBuf = (ByteBuf) msg;
        try (final GracefulShutdown ignored = GracefulShutdown.gracefulShutdown(byteBuf::release)) {
            final long epochSeconds = byteBuf.readLong();
            final OffsetDateTime serverTime = Instant.ofEpochSecond(epochSeconds).atOffset(ZoneOffset.UTC);
            log.info("server time: {}", serverTime);
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        log.warn("exception occurred in channelRead", cause);
        ctx.close();
    }
}
