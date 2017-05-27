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
package com.example.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "user_email", schema = "jpa_sample")
@IdClass(UserEmailPK.class)
public class UserEmail {

    private Long userId;
    private Long emailId;
    private Timestamp createdAt;
    private EmailAddress emailAddressByEmailId;

    @Id
    @Column(name = "user_id", nullable = false)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Id
    @Column(name = "email_id", nullable = false)
    public Long getEmailId() {
        return emailId;
    }

    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    @Basic
    @Column(name = "created_at", nullable = false)
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEmail userEmail = (UserEmail) o;
        return Objects.equals(userId, userEmail.userId) &&
                Objects.equals(emailId, userEmail.emailId) &&
                Objects.equals(createdAt, userEmail.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, emailId, createdAt);
    }

    @OneToOne
    @JoinColumn(name = "email_id", referencedColumnName = "id", nullable = false)
    public EmailAddress getEmailAddressByEmailId() {
        return emailAddressByEmailId;
    }

    public void setEmailAddressByEmailId(EmailAddress emailAddressByEmailId) {
        this.emailAddressByEmailId = emailAddressByEmailId;
    }
}
