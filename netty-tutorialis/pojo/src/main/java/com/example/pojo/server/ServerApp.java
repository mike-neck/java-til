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

import com.example.server.ServerChannelInitializationConfigurer;
import com.example.server.ServerMain;
import io.netty.channel.ChannelHandler;

public class ServerApp {

    public static void main(String[] args) throws InterruptedException {
        new ServerMain(8000).run();
    }

    public static class EncoderConfigurer implements ServerChannelInitializationConfigurer {

        @Override
        public ChannelHandler channelHandler() {
            return new ServerTimeEncoder();
        }
    }

    public static class ServerHandlerConfigurer implements ServerChannelInitializationConfigurer {

        @Override
        public ChannelHandler channelHandler() {
            return new ServerTimeServerHandler();
        }
    }
}
