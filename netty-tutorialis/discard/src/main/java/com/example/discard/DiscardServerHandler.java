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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        log.info("message coming.");
        final ByteBuf byteBuf = (ByteBuf) msg;
        final EmitterProcessor<Byte> emitter = EmitterProcessor.create();

        // ここは ByteBuf.toString(StandardCharset.UTF_8) でもよいらしい
        final Mono<String> messageMono = emitter
                .collectList()
                .map(DiscardServerHandler::unBoxing)
                .map(bs -> new String(bs, StandardCharsets.UTF_8));
        messageMono.subscribe(message -> log.info("received message: {}", message));
        messageMono.doOnTerminate(() -> ReferenceCountUtil.release(byteBuf));

        while (byteBuf.isReadable()) {
            emitter.onNext(byteBuf.readByte());
        }
        emitter.onComplete();
    }

    private static byte[] unBoxing(final List<Byte> bytes) {
        final int size = bytes.size();
        final byte[] bs = new byte[size];
        for (int i = 0; i < size; i++) {
            bs[i] = bytes.get(i);
        }
        return bs;
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        log.warn("exception caught", cause);
        ctx.close();
    }
}
