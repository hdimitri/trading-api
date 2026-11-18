package ar.com.cocos.infrastructure.adapter.out.persistence.repository;

import ar.com.cocos.infrastructure.adapter.out.persistence.entity.MarketDataJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataMarketDataRepository extends JpaRepository<MarketDataJpaEntity, Long> {

    @Override
    @EntityGraph(attributePaths = {"instrument"})
    List<MarketDataJpaEntity> findAll();

    @EntityGraph(attributePaths = {"instrument"})
    List<MarketDataJpaEntity> findByInstrumentId(Long instrumentId);

    @EntityGraph(attributePaths = {"instrument"})
    Optional<MarketDataJpaEntity> findFirstByInstrumentIdOrderByDateDesc(Long instrumentId);
}
