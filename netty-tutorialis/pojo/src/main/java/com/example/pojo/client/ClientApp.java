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
package com.example.pojo.client;

import com.example.client.ClientChannelInitializerConfigurer;
import com.example.client.ClientMain;
import io.netty.channel.ChannelHandlerAdapter;

public class ClientApp {

    public static void main(String[] args) throws InterruptedException {
        new ClientMain("localhost", 8000).run();
    }
    
    public static class ClientHandlerConfigurer implements ClientChannelInitializerConfigurer {
        @Override
        public ChannelHandlerAdapter handlerAdapter() {
            return new ServerTimeClientHandler();
        }
    }

    public static class ClientDecoder implements ClientChannelInitializerConfigurer {
        @Override
        public ChannelHandlerAdapter handlerAdapter() {
            return new ServerTimeDecoder();
        }
    }
}
