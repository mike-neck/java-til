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
package com.example;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

public interface ChannelInitializationConfigurer {

    void configure(final SocketChannel socketChannel) throws Exception;

    default ChannelInitializer<SocketChannel> asInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(final SocketChannel ch) throws Exception {
                configure(ch);
            }
        };
    }

    static ChannelInitializer<SocketChannel> channelInitializer() {
        final Iterator<ChannelInitializationConfigurer> iterator = ServiceLoader.load(ChannelInitializationConfigurer.class).iterator();
        if (iterator.hasNext()) {
            return iterator.next().asInitializer();
        }
        throw new NoSuchElementException("no ChannelInitializationConfigurer found.");
    }
}
