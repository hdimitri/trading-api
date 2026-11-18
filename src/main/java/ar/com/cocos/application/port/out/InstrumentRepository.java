package ar.com.cocos.application.port.out;

import ar.com.cocos.domain.model.*;
import java.util.List;
import java.util.Optional;

public interface InstrumentRepository {
    List<Instrument> findAll();
    List<Instrument> search(String query);
    Optional<Instrument> findById(Long id);
    Instrument save(Instrument instrument);
}