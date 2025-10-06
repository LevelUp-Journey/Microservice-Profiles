package com.levelup.journey.platform.microserviceprofiles.profiles.application.internal.commandservices;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates.ProfileRank;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.AddScoreCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.CreateProfileRankCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.SubtractScoreCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.entities.ScoreAuditLog;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.Score;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileRankCommandService;
import com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.persistence.jpa.repositories.ProfileRankRepository;
import com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.persistence.jpa.repositories.RankRepository;
import com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.persistence.jpa.repositories.ScoreAuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProfileRankCommandServiceImpl implements ProfileRankCommandService {

    private final ProfileRankRepository profileRankRepository;
    private final RankRepository rankRepository;
    private final ScoreAuditLogRepository scoreAuditLogRepository;

    public ProfileRankCommandServiceImpl(ProfileRankRepository profileRankRepository,
                                       RankRepository rankRepository,
                                       ScoreAuditLogRepository scoreAuditLogRepository) {
        this.profileRankRepository = profileRankRepository;
        this.rankRepository = rankRepository;
        this.scoreAuditLogRepository = scoreAuditLogRepository;
    }

    @Override
    @Transactional
    public Optional<ProfileRank> handle(CreateProfileRankCommand command) {
        // Check if profile rank already exists
        if (profileRankRepository.existsByProfileId(command.profileId())) {
            return Optional.empty();
        }

        // Get Bronze rank (default starting rank)
        var bronzeRank = rankRepository.findByRankName_Name("Bronze");
        if (bronzeRank.isEmpty()) {
            throw new IllegalStateException("Bronze rank not found. Please initialize ranks first.");
        }

        // Create profile rank with default Bronze rank and starting score
        var profileRank = new ProfileRank(command.profileId(), bronzeRank.get().getId());
        var savedProfileRank = profileRankRepository.save(profileRank);

        // Create audit log for initial score
        var initialScore = new Score(Score.DEFAULT_STARTING_SCORE);
        var auditLog = new ScoreAuditLog(
            command.profileId(),
            initialScore,
            new Score(0),
            initialScore,
            "Initial profile rank creation",
            ScoreAuditLog.ScoreChangeType.ADDITION,
            null
        );
        scoreAuditLogRepository.save(auditLog);

        return Optional.of(savedProfileRank);
    }

    @Override
    @Transactional
    public Optional<ProfileRank> handle(AddScoreCommand command) {
        var profileRankOpt = profileRankRepository.findByProfileId(command.profileId());
        if (profileRankOpt.isEmpty()) {
            return Optional.empty();
        }

        var profileRank = profileRankOpt.get();
        var previousScore = profileRank.getCurrentScore();

        // Add score to profile rank
        profileRank.addScore(command.points(), command.reason());
        var newScore = profileRank.getCurrentScore();

        // Check if rank should be updated based on new score
        updateRankIfNecessary(profileRank, newScore);

        var savedProfileRank = profileRankRepository.save(profileRank);

        // Create audit log
        var scoreChange = new Score(command.points());
        var auditLog = new ScoreAuditLog(
            command.profileId(),
            scoreChange,
            new Score(previousScore),
            new Score(newScore),
            command.reason(),
            ScoreAuditLog.ScoreChangeType.ADDITION,
            command.externalReferenceId()
        );
        scoreAuditLogRepository.save(auditLog);

        return Optional.of(savedProfileRank);
    }

    @Override
    @Transactional
    public Optional<ProfileRank> handle(SubtractScoreCommand command) {
        var profileRankOpt = profileRankRepository.findByProfileId(command.profileId());
        if (profileRankOpt.isEmpty()) {
            return Optional.empty();
        }

        var profileRank = profileRankOpt.get();
        var previousScore = profileRank.getCurrentScore();

        // Subtract score from profile rank
        profileRank.subtractScore(command.points(), command.reason());
        var newScore = profileRank.getCurrentScore();

        // Check if rank should be updated based on new score
        updateRankIfNecessary(profileRank, newScore);

        var savedProfileRank = profileRankRepository.save(profileRank);

        // Create audit log
        var scoreChange = new Score(command.points());
        var auditLog = new ScoreAuditLog(
            command.profileId(),
            scoreChange,
            new Score(previousScore),
            new Score(newScore),
            command.reason(),
            ScoreAuditLog.ScoreChangeType.SUBTRACTION,
            command.externalReferenceId()
        );
        scoreAuditLogRepository.save(auditLog);

        return Optional.of(savedProfileRank);
    }

    private void updateRankIfNecessary(ProfileRank profileRank, Integer newScore) {
        var currentRankOpt = rankRepository.findRankByScore(newScore);
        if (currentRankOpt.isPresent()) {
            var currentRank = currentRankOpt.get();
            if (!currentRank.getId().equals(profileRank.getRankId())) {
                profileRank.updateRank(currentRank.getId());
            }
        }
    }
}