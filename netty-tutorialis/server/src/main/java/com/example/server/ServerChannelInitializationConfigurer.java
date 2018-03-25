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

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public interface ServerChannelInitializationConfigurer {

    ChannelHandler channelHandler();

    static ChannelInitializer<SocketChannel> applyConfigurer(final Iterable<ServerChannelInitializationConfigurer> configurers) {
        final Logger log = LoggerFactory.getLogger(ServerChannelInitializationConfigurer.class);
        final ImmutableList<ServerChannelInitializationConfigurer> list = Lists.immutable.ofAll(configurers);
        log.info("configurers: {}", list);
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(final SocketChannel ch) {
                log.info("initialize: {}", list);
                list.injectInto(ch.pipeline(), (pip, config) -> pip.addLast(config.channelHandler()));
            }
        };
    }

    static ChannelInitializer<SocketChannel> channelInitializer() {
        final ServiceLoader<ServerChannelInitializationConfigurer> configurers = ServiceLoader.load(ServerChannelInitializationConfigurer.class);
        return applyConfigurer(configurers);
    }
}
