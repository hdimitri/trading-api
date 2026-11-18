package ar.com.cocos.infrastructure.adapter.out.persistence.repository;

import ar.com.cocos.infrastructure.adapter.out.persistence.entity.InstrumentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataInstrumentRepository extends JpaRepository<InstrumentJpaEntity, Long> {
    List<InstrumentJpaEntity> findByTickerContainingIgnoreCaseOrNameContainingIgnoreCase(String ticker, String name);
}
