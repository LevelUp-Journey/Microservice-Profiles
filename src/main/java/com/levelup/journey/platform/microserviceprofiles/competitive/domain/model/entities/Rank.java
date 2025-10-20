package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.entities;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveRank;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

/**
 * Rank Entity
 * Represents a competitive rank as a database entity
 * This is a reference/master data table
 */
@Entity
@Table(name = "ranks")
@Getter
public class Rank {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank_name", nullable = false, unique = true, length = 20)
    private CompetitiveRank rankName;

    @Column(name = "minimum_points", nullable = false)
    private Integer minimumPoints;

    @Column(name = "description", length = 255)
    private String description;

    protected Rank() {
        // JPA constructor
    }

    /**
     * Constructor for Rank entity
     *
     * @param rankName The competitive rank enum value
     * @param minimumPoints Minimum points required for this rank
     * @param description Description of the rank
     */
    public Rank(CompetitiveRank rankName, Integer minimumPoints, String description) {
        this.id = UUID.randomUUID();
        this.rankName = rankName;
        this.minimumPoints = minimumPoints;
        this.description = description;
    }

    /**
     * Get the competitive rank enum value
     */
    public CompetitiveRank getRankName() {
        return rankName;
    }

    /**
     * Get minimum points required for this rank
     */
    public Integer getMinimumPoints() {
        return minimumPoints;
    }

    /**
     * Check if given points qualify for this rank
     */
    public boolean qualifiesForRank(Integer points) {
        return points >= minimumPoints;
    }
}
