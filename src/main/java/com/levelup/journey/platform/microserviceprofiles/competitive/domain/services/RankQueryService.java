package com.levelup.journey.platform.microserviceprofiles.competitive.domain.services;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.entities.Rank;

import java.util.List;
import java.util.Optional;

/**
 * Rank Query Service
 * Handles queries related to competitive ranks
 */
public interface RankQueryService {

    /**
     * Get all available ranks
     *
     * @return List of all competitive ranks
     */
    List<Rank> getAllRanks();

    /**
     * Get rank by name
     *
     * @param rankName The rank name
     * @return Optional rank entity
     */
    Optional<Rank> getRankByName(String rankName);
}