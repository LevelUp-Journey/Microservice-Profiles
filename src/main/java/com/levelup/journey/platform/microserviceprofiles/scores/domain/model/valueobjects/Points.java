package com.levelup.journey.platform.microserviceprofiles.scores.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value object representing score points
 */
@Embeddable
public record Points(Integer value) {
    
    public Points {
        if (value == null || value < 0) {
            throw new IllegalArgumentException("Points cannot be null or negative");
        }
        if (value > 1000000) {
            throw new IllegalArgumentException("Points cannot exceed 1,000,000");
        }
    }

    public Points() {
        this(0);
    }

    public Points add(Points other) {
        return new Points(this.value + other.value);
    }

    public Points subtract(Points other) {
        int result = this.value - other.value;
        if (result < 0) {
            throw new IllegalArgumentException("Cannot subtract more points than available");
        }
        return new Points(result);
    }
}
