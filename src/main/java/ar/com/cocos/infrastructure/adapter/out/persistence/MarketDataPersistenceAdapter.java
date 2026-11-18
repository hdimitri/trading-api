package ar.com.cocos.infrastructure.adapter.out.persistence;

import ar.com.cocos.application.port.out.MarketDataRepository;
import ar.com.cocos.domain.enums.InstrumentType;
import ar.com.cocos.domain.model.Instrument;
import ar.com.cocos.domain.model.MarketData;
import ar.com.cocos.infrastructure.adapter.out.persistence.entity.InstrumentJpaEntity;
import ar.com.cocos.infrastructure.adapter.out.persistence.entity.MarketDataJpaEntity;
import ar.com.cocos.infrastructure.adapter.out.persistence.repository.SpringDataInstrumentRepository;
import ar.com.cocos.infrastructure.adapter.out.persistence.repository.SpringDataMarketDataRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MarketDataPersistenceAdapter implements MarketDataRepository {

    private final SpringDataMarketDataRepository jpaRepository;
    private final SpringDataInstrumentRepository instrumentJpaRepository;

    public MarketDataPersistenceAdapter(SpringDataMarketDataRepository jpaRepository,
                                        SpringDataInstrumentRepository instrumentJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.instrumentJpaRepository = instrumentJpaRepository;
    }

    @Override
    public List<MarketData> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarketData> findByInstrumentId(Long instrumentId) {
        return jpaRepository.findByInstrumentId(instrumentId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MarketData> findLatestByInstrumentId(Long instrumentId) {
        return jpaRepository.findFirstByInstrumentIdOrderByDateDesc(instrumentId)
                .map(this::toDomain);
    }

    @Override
    public MarketData save(MarketData md) {
        InstrumentJpaEntity instEntity = (md.getInstrument() != null && md.getInstrument().getId() != null)
                ? instrumentJpaRepository.findById(md.getInstrument().getId()).orElse(null)
                : null;

        MarketDataJpaEntity entity = new MarketDataJpaEntity(
                instEntity,
                md.getClose(),
                BigDecimal.valueOf(md.getPreviousClose()),
                md.getDate(),
                md.getHigh(),
                md.getLow(),
                md.getOpen()
        );
        entity.setId(md.getId());
        return toDomain(jpaRepository.save(entity));
    }

    private MarketData toDomain(MarketDataJpaEntity e) {
        Instrument instrument = null;
        if (e.getInstrument() != null) {
            InstrumentJpaEntity ie = e.getInstrument();
            InstrumentType type = ie.getType() != null
                    ? InstrumentType.valueOf(ie.getType().trim().toUpperCase())
                    : null;
            instrument = new Instrument(ie.getId(), ie.getTicker(), ie.getName(), type);
        }
        double close = e.getClose() != null ? e.getClose() : 0.0;
        double previousClose = e.getPreviousClose() != null ? e.getPreviousClose().doubleValue() : 0.0;
        double high = e.getHigh() != null ? e.getHigh() : 0.0;
        double low = e.getLow() != null ? e.getLow() : 0.0;
        double open = e.getOpen() != null ? e.getOpen() : 0.0;

        return new MarketData(e.getId(), instrument, close, previousClose, e.getDate(), high, low, open);
    }
}
