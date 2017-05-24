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
import com.example.repository.ApplicationUserRepository;
import com.google.inject.persist.Transactional;

import javax.inject.Inject;
import java.util.Optional;

public class ApplicationUserService {

    private final ApplicationUserRepository repository;
    private final HashService hash;

    @Inject
    public ApplicationUserService(ApplicationUserRepository repository, HashService hash) {
        this.repository = repository;
        this.hash = hash;
    }

    @Transactional
    public ApplicationUser save(final String username, final String rawPassword) {
        return repository.create(username, hash.makeHash(rawPassword));
    }

    @Transactional
    public Optional<ApplicationUser> findForUpdate(final Long userId) {
        final Optional<ApplicationUser> user = repository.findByIdWithOptimisticLock(userId);
        user.map(ApplicationUser::getVersion).ifPresent(System.out::println);
        return user;
    }

    @Transactional
    public Optional<ApplicationUser> findForUpdateWithForceFlush(final Long userId) {
        final Optional<ApplicationUser> user = repository.findByIdWithOptimisticLock(userId);
        user.map(ApplicationUser::getVersion).ifPresent(System.out::println);
        repository.forceFlush();
        user.map(ApplicationUser::getVersion).ifPresent(System.out::println);
        return user;
    }
}
