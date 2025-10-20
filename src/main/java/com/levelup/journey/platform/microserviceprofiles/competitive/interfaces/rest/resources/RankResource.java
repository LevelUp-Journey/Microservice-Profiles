package com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.resources;

/**
 * Rank Resource
 * REST resource representation of a competitive rank
 */
public record RankResource(
        String id,
        String name,
        Integer minimumPoints
) {
}