package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects;

/**
 * Competitive Rank Enumeration
 * Represents the different competitive ranks a user can achieve
 */
public enum CompetitiveRank {
    BRONZE(0),
    SILVER(500),
    GOLD(1500),
    PLATINUM(3000),
    DIAMOND(5000),
    MASTER(7500),
    GRANDMASTER(10000);

    private final int minimumPoints;

    CompetitiveRank(int minimumPoints) {
        this.minimumPoints = minimumPoints;
    }

    public int getMinimumPoints() {
        return minimumPoints;
    }

    /**
     * Calculate rank based on total points
     * @param points Total points
     * @return Appropriate CompetitiveRank
     */
    public static CompetitiveRank fromPoints(int points) {
        if (points >= GRANDMASTER.minimumPoints) {
            return GRANDMASTER;
        } else if (points >= MASTER.minimumPoints) {
            return MASTER;
        } else if (points >= DIAMOND.minimumPoints) {
            return DIAMOND;
        } else if (points >= PLATINUM.minimumPoints) {
            return PLATINUM;
        } else if (points >= GOLD.minimumPoints) {
            return GOLD;
        } else if (points >= SILVER.minimumPoints) {
            return SILVER;
        } else {
            return BRONZE;
        }
    }

    /**
     * Get next rank in progression
     * @return Next rank or null if already at highest
     */
    public CompetitiveRank getNextRank() {
        return switch (this) {
            case BRONZE -> SILVER;
            case SILVER -> GOLD;
            case GOLD -> PLATINUM;
            case PLATINUM -> DIAMOND;
            case DIAMOND -> MASTER;
            case MASTER -> GRANDMASTER;
            case GRANDMASTER -> null;
        };
    }

    /**
     * Get points needed for next rank
     * @param currentPoints Current total points
     * @return Points needed, or 0 if at max rank
     */
    public int getPointsNeededForNextRank(int currentPoints) {
        CompetitiveRank nextRank = getNextRank();
        if (nextRank == null) {
            return 0;
        }
        return Math.max(0, nextRank.minimumPoints - currentPoints);
    }
}
