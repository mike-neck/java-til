/*
 * Copyright 2017 Shinya Mochida
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

import com.lambdaworks.crypto.SCryptUtil;

public class HashService {

    public String makeHash(final String password) {
        return SCryptUtil.scrypt(password, 1024, 7, 3);
    }

    public boolean isValidPassword(final String hash, final String raw) {
        return SCryptUtil.check(raw, hash);
    }

    public static void main(String[] args) {
        final HashService hash = new HashService();
        final String password = "foo-bar-password";
        final String hashed = hash.makeHash(password);
        System.out.println(hashed);
        final boolean notValid = hash.isValidPassword(hashed, "this is not valid.");
        System.out.println(notValid);
        final boolean valid = hash.isValidPassword(hashed, password);
        System.out.println(valid);
    }
}
