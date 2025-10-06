package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.entities;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.RankName;
import com.levelup.journey.platform.microserviceprofiles.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;

@Entity
@Table(name = "ranks")
public class Rank extends AuditableModel {

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "name", nullable = false, unique = true, length = 20))
    })
    private RankName rankName;

    @Column(name = "min_score", nullable = false)
    private Integer minScore;

    @Column(name = "max_score", nullable = false)
    private Integer maxScore;

    @Column(name = "rank_order", nullable = false, unique = true)
    private Integer rankOrder;

    protected Rank() {
        // Default constructor for JPA
    }

    public Rank(String name, Integer minScore, Integer maxScore, Integer rankOrder) {
        this.rankName = new RankName(name);
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.rankOrder = rankOrder;
    }

    public String getName() {
        return rankName.name();
    }

    public Integer getMinScore() {
        return minScore;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public Integer getRankOrder() {
        return rankOrder;
    }

    public boolean isScoreInRange(Integer score) {
        return score >= minScore && score <= maxScore;
    }
}