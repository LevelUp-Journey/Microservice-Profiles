package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.CreateProfileFromUserCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.PersonName;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.ProfileUrl;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.Provider;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.StudentCycle;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.Username;
import com.levelup.journey.platform.microserviceprofiles.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
public class Profile extends AuditableAbstractAggregateRoot<Profile> implements Persistable<UUID> {
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "user_id", unique = true))})
    private UserId userId;

    @Embedded
    private PersonName name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "username", column = @Column(name = "username"))})
    private Username username;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "url", column = @Column(name = "profile_url"))})
    private ProfileUrl profileUrl;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "provider"))})
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "student_cycle")
    private StudentCycle cycle;

    @Transient
    private boolean isNew = true;

    public Profile(String firstName, String lastName, String username, String profileUrl, String provider) {
        this.name = new PersonName(firstName, lastName);
        this.username = new Username(username);
        this.profileUrl = new ProfileUrl(profileUrl);
        this.provider = new Provider(provider);
    }

    public Profile() {
        // Default constructor for JPA
    }

    public Profile(CreateProfileFromUserCommand command, String username) {
        this.userId = new UserId(command.userId());
        this.name = new PersonName(command.firstName(), command.lastName());
        this.username = new Username(username);
        this.profileUrl = new ProfileUrl(command.profileUrl());
        this.provider = new Provider(command.provider());
    }

    public String getFullName() {
        return name.getFullName();
    }

    public String getUsername() {
        return username.username();
    }

    public String getFirstName() {
        return name.firstName();
    }

    public String getLastName() {
        return name.lastName();
    }

    public String getProfileUrl() {
        return profileUrl != null ? profileUrl.value() : null;
    }

    public String getProvider() {
        return provider != null ? provider.value() : null;
    }

    public StudentCycle getCycle() {
        return cycle;
    }

    public void updateName(String firstName, String lastName) {
        this.name = new PersonName(firstName, lastName);
    }

    public void updateUsername(String username) {
        // Username allows both:
        // - Generated format (USER + 9 digits)
        // - Custom format (3-15 chars, alphanumeric + _.-)
        this.username = new Username(username);
    }

    public void updateProfileUrl(String profileUrl) {
        this.profileUrl = new ProfileUrl(profileUrl);
    }

    public void updateProvider(String provider) {
        this.provider = new Provider(provider);
    }

    public void updateCycle(StudentCycle cycle) {
        this.cycle = cycle;
    }

    public String getUserId() {
        return userId != null ? userId.userId() : null;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

}