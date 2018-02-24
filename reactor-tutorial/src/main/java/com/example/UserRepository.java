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

import com.example.annotations.Lesson;
import reactor.core.publisher.Flux;

@Lesson(6)
public class UserRepository {

    Flux<User> findAll() {
        return Flux.just(//
                new UserImpl("phillipe"),
                new UserImpl("charles"),
                new UserImpl("sebastian"),
                new UserImpl("katharine")
        );
    }

    private static class UserImpl implements User {

        private final String username;

        private UserImpl(final String username) {
            this.username = username;
        }

        @Override
        public Name getUsername() {
            return null;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof UserImpl)) return false;

            final UserImpl user = (UserImpl) o;

            return username.equals(user.username);
        }

        @Override
        public int hashCode() {
            return username.hashCode();
        }
    }

    private static class NameImpl implements Name {

        private final String string;

        private NameImpl(final String string) {
            this.string = string;
        }

        @Override
        public String asString() {
            return null;
        }
    }
}
