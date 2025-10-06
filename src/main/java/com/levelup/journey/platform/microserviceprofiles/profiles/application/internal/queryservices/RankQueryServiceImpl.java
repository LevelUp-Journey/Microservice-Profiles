package com.levelup.journey.platform.microserviceprofiles.profiles.application.internal.queryservices;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.entities.Rank;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetAllRanksQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetRankByNameQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.RankQueryService;
import com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.persistence.jpa.repositories.RankRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RankQueryServiceImpl implements RankQueryService {

    private final RankRepository rankRepository;

    public RankQueryServiceImpl(RankRepository rankRepository) {
        this.rankRepository = rankRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rank> handle(GetAllRanksQuery query) {
        return rankRepository.findAllByOrderByRankOrderAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rank> handle(GetRankByNameQuery query) {
        return rankRepository.findByRankName_Name(query.rankName());
    }
}