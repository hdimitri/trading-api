package ar.com.cocos.infrastructure.adapter.in.rest;

import ar.com.cocos.application.port.in.GetMarketDataUseCase;
import ar.com.cocos.application.port.in.SearchInstrumentsUseCase;
import ar.com.cocos.domain.model.Instrument;
import ar.com.cocos.domain.model.MarketData;
import ar.com.cocos.infrastructure.adapter.in.rest.dto.response.InstrumentResponseDto;
import ar.com.cocos.infrastructure.adapter.in.rest.dto.response.MarketDataResponseDto;
import ar.com.cocos.infrastructure.adapter.in.rest.validator.RequestValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instruments")
public class InstrumentController {

    private final SearchInstrumentsUseCase searchInstrumentsUseCase;
    private final GetMarketDataUseCase getMarketDataUseCase;

    public InstrumentController(SearchInstrumentsUseCase searchInstrumentsUseCase,
                                GetMarketDataUseCase getMarketDataUseCase) {
        this.searchInstrumentsUseCase = searchInstrumentsUseCase;
        this.getMarketDataUseCase = getMarketDataUseCase;
    }

    @GetMapping
    public ResponseEntity<List<InstrumentResponseDto>> search(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "filterBy", required = false) String filterBy) {
        List<InstrumentResponseDto> response = searchInstrumentsUseCase.execute(query, filterBy).stream()
                .map(this::toInstrumentDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{instrumentId}/marketdata")
    public ResponseEntity<MarketDataResponseDto> getMarketData(@PathVariable Long instrumentId) {
        RequestValidator.validatePositiveId(instrumentId, "instrumentId");

        MarketData md = getMarketDataUseCase.executeByInstrument(instrumentId);
        if (md == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toMarketDataDto(md));
    }

    private InstrumentResponseDto toInstrumentDto(Instrument instrument) {
        return new InstrumentResponseDto(
                instrument.getId(),
                instrument.getTicker(),
                instrument.getName(),
                instrument.getType() != null ? instrument.getType().name() : null
        );
    }

    private MarketDataResponseDto toMarketDataDto(MarketData md) {
        String ticker = md.getInstrument() != null ? md.getInstrument().getTicker() : null;
        return new MarketDataResponseDto(
                md.getId(),
                ticker,
                md.getClose(),
                md.getPreviousClose(),
                md.getDate()
        );
    }
}
