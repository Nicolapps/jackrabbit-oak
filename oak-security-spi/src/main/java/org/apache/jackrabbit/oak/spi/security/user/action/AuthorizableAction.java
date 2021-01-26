/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.oak.spi.security.user.action;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.oak.api.Root;
import org.apache.jackrabbit.oak.namepath.NamePathMapper;
import org.apache.jackrabbit.oak.spi.security.ConfigurationParameters;
import org.apache.jackrabbit.oak.spi.security.SecurityProvider;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code AuthorizableAction} interface provide an implementation
 * specific way to execute additional validation or write tasks upon
 *
 * <ul>
 * <li>{@link #onCreate User creation},</li>
 * <li>{@link #onCreate Group creation},</li>
 * <li>{@link #onRemove Authorizable removal} and</li>
 * <li>{@link #onPasswordChange User password modification}.</li>
 * </ul>
 *
 * <p>Please be aware, that in contrast to {@link org.apache.jackrabbit.oak.spi.commit.Validator}
 * the authorizable actions will only be enforced when user related content
 * modifications are generated by using the user management API.</p>
 *
 * <p>
 * <strong>Note:</strong> user management operations are defined to perform transient
 * modifications, which require an explicit save/commit call by the API consumer to
 * be persisted. For consistency, implementations of the {@code AuthorizableAction} are expected
 * to adhere to this rule and must not 	pre-emptively call {@code Root.commit()}.
 *
 * @see org.apache.jackrabbit.oak.spi.security.ConfigurationParameters
 * @since OAK 1.0
 */
public interface AuthorizableAction {

    /**
     * Initialize this action with the specified security provider and configuration.
     *
     * @param securityProvider
     * @param config
     */
    void init(SecurityProvider securityProvider, ConfigurationParameters config);

    /**
     * Allows to add application specific modifications or validation associated
     * with the creation of a new group. Note, that this method is called
     * <strong>before</strong> any {@code Root#commit()} call.
     *
     *
     * @param group The new group that has not yet been persisted;
     * e.g. the associated tree is still 'NEW'.
     * @param root The root associated with the user manager.
     * @param namePathMapper
     * @throws javax.jcr.RepositoryException If an error occurs.
     */
    void onCreate(Group group, Root root, NamePathMapper namePathMapper) throws RepositoryException;

    /**
     * Allows to add application specific modifications or validation associated
     * with the creation of a new user. Note, that this method is called
     * <strong>before</strong> any {@code Root#commit()} call.
     *
     *
     * @param user The new user that has not yet been persisted;
     * e.g. the associated tree is still 'NEW'.
     * @param password The password that was specified upon user creation.
     * @param root The root associated with the user manager.
     * @param namePathMapper
     * @throws RepositoryException If an error occurs.
     */
    void onCreate(User user, String password, Root root, NamePathMapper namePathMapper) throws RepositoryException;

    /**
     * Allows to add application specific modifications or validation associated
     * with the creation of a new <strong>system</strong>system. Note, that this method is called
     * <strong>before</strong> any {@code Root#commit()} call.
     *
     *
     * @param user The new system user that has not yet been persisted;
     * e.g. the associated tree is still 'NEW'.
     * @param root The root associated with the user manager.
     * @param namePathMapper The {@code NamePathMapper} present with the editing session.
     * @throws RepositoryException If an error occurs.
     */
    default void onCreate(@NotNull User systemUser, @NotNull Root root, @NotNull NamePathMapper namePathMapper) throws RepositoryException {
        // nop
    }

    /**
     * Allows to add application specific behavior associated with the removal
     * of an authorizable. Note, that this method is called <strong>before</strong>
     * {@link org.apache.jackrabbit.api.security.user.Authorizable#remove} is executed (and persisted); thus the
     * target authorizable still exists.
     *
     *
     * @param authorizable The authorizable to be removed.
     * @param root The root associated with the user manager.
     * @param namePathMapper
     * @throws RepositoryException If an error occurs.
     */
    void onRemove(Authorizable authorizable, Root root, NamePathMapper namePathMapper) throws RepositoryException;

    /**
     * Allows to add application specific action or validation associated with
     * changing a user password. Note, that this method is called <strong>before</strong>
     * the password property is being modified in the content.
     *
     *
     * @param user The user that whose password is going to change.
     * @param newPassword The new password as specified in {@link org.apache.jackrabbit.api.security.user.User#changePassword}
     * @param root The root associated with the user manager.
     * @param namePathMapper
     * @throws RepositoryException If an exception or error occurs.
     */
    void onPasswordChange(User user, String newPassword, Root root, NamePathMapper namePathMapper) throws RepositoryException;
}