package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record Score(Integer value) {
    public static final int MIN_SCORE = 0;
    public static final int MAX_SCORE = 10000;
    public static final int DEFAULT_STARTING_SCORE = 1000;

    // Score ranges for ranks (1000-1700 distributed across 7 ranks)
    public static final int BRONZE_MIN = 1000;
    public static final int BRONZE_MAX = 1099;
    public static final int SILVER_MIN = 1100;
    public static final int SILVER_MAX = 1199;
    public static final int GOLD_MIN = 1200;
    public static final int GOLD_MAX = 1299;
    public static final int PLATINUM_MIN = 1300;
    public static final int PLATINUM_MAX = 1399;
    public static final int DIAMOND_MIN = 1400;
    public static final int DIAMOND_MAX = 1499;
    public static final int MASTER_MIN = 1500;
    public static final int MASTER_MAX = 1599;
    public static final int GRANDMASTER_MIN = 1600;
    public static final int GRANDMASTER_MAX = 1700;

    public Score {
        if (value == null) {
            throw new IllegalArgumentException("Score value cannot be null");
        }
        if (value < MIN_SCORE) {
            throw new IllegalArgumentException("Score cannot be negative");
        }
        if (value > MAX_SCORE) {
            throw new IllegalArgumentException("Score cannot exceed maximum value: " + MAX_SCORE);
        }
    }

    public static Score defaultScore() {
        return new Score(DEFAULT_STARTING_SCORE);
    }

    public Score add(Integer points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points to add must be positive");
        }
        int newValue = Math.min(MAX_SCORE, this.value + points);
        return new Score(newValue);
    }

    public Score subtract(Integer points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points to subtract must be positive");
        }
        int newValue = Math.max(MIN_SCORE, this.value - points);
        return new Score(newValue);
    }

    public String determineRankName() {
        if (value >= GRANDMASTER_MIN && value <= GRANDMASTER_MAX) {
            return "Grandmaster";
        } else if (value >= MASTER_MIN && value <= MASTER_MAX) {
            return "Master";
        } else if (value >= DIAMOND_MIN && value <= DIAMOND_MAX) {
            return "Diamond";
        } else if (value >= PLATINUM_MIN && value <= PLATINUM_MAX) {
            return "Platinum";
        } else if (value >= GOLD_MIN && value <= GOLD_MAX) {
            return "Gold";
        } else if (value >= SILVER_MIN && value <= SILVER_MAX) {
            return "Silver";
        } else {
            return "Bronze";
        }
    }

    public boolean isInRankRange(String rankName) {
        return determineRankName().equals(rankName);
    }

    public boolean canPromoteToRank(String targetRankName) {
        return switch (targetRankName) {
            case "Silver" -> value >= SILVER_MIN;
            case "Gold" -> value >= GOLD_MIN;
            case "Platinum" -> value >= PLATINUM_MIN;
            case "Diamond" -> value >= DIAMOND_MIN;
            case "Master" -> value >= MASTER_MIN;
            case "Grandmaster" -> value >= GRANDMASTER_MIN;
            default -> false;
        };
    }
}