package com.levelup.journey.platform.microserviceprofiles.profiles.domain.services;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates.Profile;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetAllProfilesQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileByUsernameQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileByIdQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.SearchUsersByUsernameQuery;

import java.util.List;
import java.util.Optional;

/**
 * Profile Query Service
 */
public interface ProfileQueryService {
    /**
     * Handle Get Profile By ID Query
     *
     * @param query The {@link GetProfileByIdQuery} Query
     * @return A {@link Profile} instance if the query is valid, otherwise empty
     */
    Optional<Profile> handle(GetProfileByIdQuery query);

    /**
     * Handle Get Profile By Username Query
     *
     * @param query The {@link GetProfileByUsernameQuery} Query
     * @return A {@link Profile} instance if the query is valid, otherwise empty
     */
    Optional<Profile> handle(GetProfileByUsernameQuery query);

    /**
     * Handle Get Profile By User ID Query
     *
     * @param query The {@link GetProfileByUserIdQuery} Query
     * @return A {@link Profile} instance if the query is valid, otherwise empty
     */
    Optional<Profile> handle(GetProfileByUserIdQuery query);

    /**
     * Handle Get All Profiles Query
     *
     * @param query The {@link GetAllProfilesQuery} Query
     * @return A list of {@link Profile} instances
     */
    List<Profile> handle(GetAllProfilesQuery query);

    /**
     * Handle Search Users By Username Query
     *
     * @param query The {@link SearchUsersByUsernameQuery} Query
     * @return A list of {@link Profile} instances matching the username pattern
     */
    List<Profile> handle(SearchUsersByUsernameQuery query);
}
