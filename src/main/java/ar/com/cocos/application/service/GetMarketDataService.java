package ar.com.cocos.application.service;

import ar.com.cocos.application.port.in.GetMarketDataUseCase;
import ar.com.cocos.application.port.out.MarketDataRepository;
import ar.com.cocos.domain.model.MarketData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetMarketDataService implements GetMarketDataUseCase {

    private final MarketDataRepository marketDataRepository;

    public GetMarketDataService(MarketDataRepository marketDataRepository) {
        this.marketDataRepository = marketDataRepository;
    }

    @Override
    public List<MarketData> executeByInstruments(List<Long> instrumentIds) {
        if (instrumentIds == null || instrumentIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, MarketData> latestByInstrument = new HashMap<>();
        for (MarketData md : marketDataRepository.findAll()) {
            if (md.getInstrument() == null || md.getInstrument().getId() == null) {
                continue;
            }
            Long instId = md.getInstrument().getId();
            if (!instrumentIds.contains(instId)) {
                continue;
            }
            MarketData existing = latestByInstrument.get(instId);
            if (existing == null || (md.getDate() != null && existing.getDate() != null
                    && md.getDate().isAfter(existing.getDate()))) {
                latestByInstrument.put(instId, md);
            }
        }

        return new ArrayList<>(latestByInstrument.values());
    }

    @Override
    public MarketData executeByInstrument(Long instrumentId) {
        if (instrumentId == null) {
            return null;
        }
        return marketDataRepository.findLatestByInstrumentId(instrumentId).orElse(null);
    }
}
