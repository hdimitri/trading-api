package ar.com.cocos.application.port.out;

import ar.com.cocos.domain.model.*;
import java.util.List;
import java.util.Optional;

public interface MarketDataRepository {
    List<MarketData> findAll();
    List<MarketData> findByInstrumentId(Long instrumentId);
    Optional<MarketData> findLatestByInstrumentId(Long instrumentId);
    MarketData save(MarketData marketData);
}