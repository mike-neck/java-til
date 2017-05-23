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
package com.example.repository;

import com.example.entity.ApplicationUser;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.Optional;

public class ApplicationUserRepository {

    private final EntityManager em;

    @Inject
    public ApplicationUserRepository(EntityManager em) {
        this.em = em;
    }

    public ApplicationUser create(final String username, final String hashedPassword) {
        final ApplicationUser user = new ApplicationUser(username, hashedPassword);
        em.persist(user);
        return user;
    }

    public Optional<ApplicationUser> findById(final long userId) {
        final ApplicationUser user = em.find(ApplicationUser.class, userId);
        return Optional.ofNullable(user);
    }

    public Optional<ApplicationUser> findByIdWithOptimisticLock(final long userId) {
        final ApplicationUser user = em.find(ApplicationUser.class, userId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        return Optional.ofNullable(user);
    }
}
