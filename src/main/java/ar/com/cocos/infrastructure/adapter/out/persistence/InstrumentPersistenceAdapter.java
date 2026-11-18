package ar.com.cocos.infrastructure.adapter.out.persistence;

import ar.com.cocos.application.port.out.InstrumentRepository;
import ar.com.cocos.domain.enums.InstrumentType;
import ar.com.cocos.domain.model.Instrument;
import ar.com.cocos.infrastructure.adapter.out.persistence.entity.InstrumentJpaEntity;
import ar.com.cocos.infrastructure.adapter.out.persistence.repository.SpringDataInstrumentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InstrumentPersistenceAdapter implements InstrumentRepository {

    private final SpringDataInstrumentRepository jpaRepository;

    public InstrumentPersistenceAdapter(SpringDataInstrumentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Instrument> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Instrument> search(String query) {
        return jpaRepository.findByTickerContainingIgnoreCaseOrNameContainingIgnoreCase(query, query)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Instrument> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Instrument save(Instrument instrument) {
        InstrumentJpaEntity entity = new InstrumentJpaEntity(
                instrument.getId(),
                instrument.getTicker(),
                instrument.getName(),
                instrument.getType() != null ? instrument.getType().name() : null
        );
        return toDomain(jpaRepository.save(entity));
    }

    private Instrument toDomain(InstrumentJpaEntity e) {
        InstrumentType type = e.getType() != null
                ? InstrumentType.valueOf(e.getType().trim().toUpperCase())
                : null;
        return new Instrument(e.getId(), e.getTicker(), e.getName(), type);
    }
}
