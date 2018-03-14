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
package com.example.time2;

import com.example.client.ClientChannelInitializerConfigurer;
import io.netty.channel.ChannelHandlerAdapter;

public final class ChannelConfigurers {

    public static class DecoderConfigurer implements ClientChannelInitializerConfigurer {
        @Override
        public ChannelHandlerAdapter handlerAdapter() {
            return new TimeDecoder();
        }
    }

    public static class LoggingTimeHandlerConfigurer implements ClientChannelInitializerConfigurer {
        @Override
        public ChannelHandlerAdapter handlerAdapter() {
            return new LoggingTimeHandler();
        }
    }

    
}
