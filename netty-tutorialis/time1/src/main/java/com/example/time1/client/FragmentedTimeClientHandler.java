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
package com.example.time1.client;

import com.example.client.ClientMain;
import com.example.share.TimeServerPort;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
public class FragmentedTimeClientHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf byteBuf;

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        this.byteBuf = ctx.alloc().buffer(4);
    }

    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        byteBuf.release();
        byteBuf = null;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final ByteBuf buf = (ByteBuf) msg;
        byteBuf.writeBytes(buf);
        buf.release();
        if (byteBuf.readableBytes() >= 8) {
            final long epochSeconds = byteBuf.readLong();
            final OffsetDateTime serverTime = Instant.ofEpochSecond(epochSeconds).atOffset(ZoneOffset.UTC);
            log.info("server time: {}", serverTime);
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        log.warn("exception occurred in channelRead", cause);
        ctx.close();
    }

    public static void main(String[] args) throws InterruptedException {
        new ClientMain("localhost", TimeServerPort.port()).run();
    }
}
