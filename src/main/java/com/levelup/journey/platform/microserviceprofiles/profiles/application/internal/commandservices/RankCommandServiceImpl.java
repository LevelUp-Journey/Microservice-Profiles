package com.levelup.journey.platform.microserviceprofiles.profiles.application.internal.commandservices;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.InitializeRanksCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.entities.Rank;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.RankCommandService;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.Score;
import com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.persistence.jpa.repositories.RankRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RankCommandServiceImpl implements RankCommandService {

    private final RankRepository rankRepository;

    public RankCommandServiceImpl(RankRepository rankRepository) {
        this.rankRepository = rankRepository;
    }

    @Override
    @Transactional
    public void handle(InitializeRanksCommand command) {
        // Only initialize if ranks don't already exist
        if (rankRepository.count() == 0) {
            createDefaultRanks();
        }
    }

    private void createDefaultRanks() {
        // Create the 7 ranks with their score ranges
        var bronze = new Rank("Bronze", Score.BRONZE_MIN, Score.BRONZE_MAX, 1);
        var silver = new Rank("Silver", Score.SILVER_MIN, Score.SILVER_MAX, 2);
        var gold = new Rank("Gold", Score.GOLD_MIN, Score.GOLD_MAX, 3);
        var platinum = new Rank("Platinum", Score.PLATINUM_MIN, Score.PLATINUM_MAX, 4);
        var diamond = new Rank("Diamond", Score.DIAMOND_MIN, Score.DIAMOND_MAX, 5);
        var master = new Rank("Master", Score.MASTER_MIN, Score.MASTER_MAX, 6);
        var grandmaster = new Rank("Grandmaster", Score.GRANDMASTER_MIN, Score.GRANDMASTER_MAX, 7);

        rankRepository.save(bronze);
        rankRepository.save(silver);
        rankRepository.save(gold);
        rankRepository.save(platinum);
        rankRepository.save(diamond);
        rankRepository.save(master);
        rankRepository.save(grandmaster);
    }
}