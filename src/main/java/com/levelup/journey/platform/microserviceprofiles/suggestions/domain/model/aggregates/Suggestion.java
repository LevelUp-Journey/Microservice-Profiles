package com.levelup.journey.platform.microserviceprofiles.suggestions.domain.model.aggregates;

import com.levelup.journey.platform.microserviceprofiles.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.model.commands.CreateSuggestionCommand;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Suggestion Aggregate Root
 * Represents a user suggestion in the platform
 */
@Entity
@Table(name = "suggestions")
public class Suggestion extends AuditableAbstractAggregateRoot<Suggestion> {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_resolved", nullable = false)
    private Boolean isResolved;

    protected Suggestion() {
        // JPA constructor
    }

    /**
     * Constructor from CreateSuggestionCommand
     *
     * @param command The creation command
     */
    public Suggestion(CreateSuggestionCommand command) {
        this.comment = command.comment();
        this.createdAt = LocalDateTime.now();
        this.isResolved = false;
    }

    // Getters

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Boolean getIsResolved() {
        return isResolved;
    }

    /**
     * Mark suggestion as resolved
     */
    public void markAsResolved() {
        this.isResolved = true;
    }
}
