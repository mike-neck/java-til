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
package com.example.pojo.server;

import com.example.pojo.model.ServerTime;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneOffset;

@Slf4j
public class ServerTimeServerHandler extends ChannelInboundHandlerAdapter {

    private final ZoneOffset offset = ZoneOffset.UTC;

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        final ServerTime serverTime = new ServerTime(offset);
        final ChannelFuture channelFuture = ctx.writeAndFlush(serverTime);
        channelFuture.addListener((GenericFutureListener<ChannelFuture>) future -> future.channel().close());
    }

}
