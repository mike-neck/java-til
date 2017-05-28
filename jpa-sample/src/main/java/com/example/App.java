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

import com.example.entity.ApplicationUser;
import com.example.service.ApplicationUserService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;

import java.util.Optional;

public class App {

    public static void main(String[] args) {
        final AppModule module = new AppModule();
        final Injector injector = Guice.createInjector(module);

        injector.getInstance(PersistService.class).start();

        final ApplicationUserService service = injector.getInstance(ApplicationUserService.class);

        final ApplicationUser user = service.save("test-user", "test-password");
        System.out.println(user);

        final Optional<ApplicationUser> fu1 = service.findForUpdate(user.getId());
        fu1.map(ApplicationUser::getVersion).ifPresent(System.out::println);

        final Optional<ApplicationUser> fu2 = service.findForUpdateWithForceFlush(user.getId());
        fu2.map(ApplicationUser::getVersion).ifPresent(System.out::println);

        injector.getInstance(PersistService.class).stop();
    }
}
