package ar.com.cocos.application.port.in;

import ar.com.cocos.domain.model.*;
import java.util.List;

public interface SearchInstrumentsUseCase {
    List<Instrument> execute(String query, String filterBy);
}