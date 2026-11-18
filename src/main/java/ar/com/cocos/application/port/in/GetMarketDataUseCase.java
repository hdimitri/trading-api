package ar.com.cocos.application.port.in;

import ar.com.cocos.domain.model.*;
import java.util.List;

public interface GetMarketDataUseCase {
    List<MarketData> executeByInstruments(List<Long> instrumentIds);
    MarketData executeByInstrument(Long instrumentId);
}