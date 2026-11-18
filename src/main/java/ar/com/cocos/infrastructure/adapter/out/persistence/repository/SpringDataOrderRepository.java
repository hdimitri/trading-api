package ar.com.cocos.infrastructure.adapter.out.persistence.repository;

import ar.com.cocos.infrastructure.adapter.out.persistence.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, Long> {

    @Override
    @EntityGraph(attributePaths = {"user", "instrument"})
    Optional<OrderJpaEntity> findById(Long id);

    @EntityGraph(attributePaths = {"user", "instrument"})
    List<OrderJpaEntity> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "instrument"})
    List<OrderJpaEntity> findByStatus(String status);

    @EntityGraph(attributePaths = {"user", "instrument"})
    List<OrderJpaEntity> findByInstrumentIdAndStatus(Long instrumentId, String status);
}
