package ar.com.cocos.application.service;

import ar.com.cocos.application.port.in.SearchInstrumentsUseCase;
import ar.com.cocos.application.port.out.InstrumentRepository;
import ar.com.cocos.domain.model.Instrument;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchInstrumentsService implements SearchInstrumentsUseCase {

    private final InstrumentRepository instrumentRepository;

    public SearchInstrumentsService(InstrumentRepository instrumentRepository) {
        this.instrumentRepository = instrumentRepository;
    }

    @Override
    public List<Instrument> execute(String query, String filterBy) {
        if (query == null || query.isBlank()) {
            return instrumentRepository.findAll();
        }

        String normalizedQuery = query.trim().toUpperCase();
        String normalizedFilter = filterBy != null ? filterBy.trim().toLowerCase() : "all";

        return instrumentRepository.search(normalizedQuery).stream()
                .filter(instrument -> matchesFilter(instrument, normalizedQuery, normalizedFilter))
                .collect(Collectors.toList());
    }

    private boolean matchesFilter(Instrument instrument, String normalizedQuery, String normalizedFilter) {
        boolean matchesTicker = instrument.getTicker() != null
                && instrument.getTicker().toUpperCase().contains(normalizedQuery);
        boolean matchesName = instrument.getName() != null
                && instrument.getName().toUpperCase().contains(normalizedQuery);

        return switch (normalizedFilter) {
            case "ticker" -> matchesTicker;
            case "name" -> matchesName;
            default -> matchesTicker || matchesName;
        };
    }
}
