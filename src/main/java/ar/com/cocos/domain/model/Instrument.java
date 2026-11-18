package ar.com.cocos.domain.model;

import ar.com.cocos.domain.enums.InstrumentType;
import java.util.Objects;

public class Instrument {

    private Long id;
    private String ticker;
    private String name;
    private InstrumentType type;

    public Instrument() {
    }

    public Instrument(String ticker, String name, InstrumentType type) {
        this(null, ticker, name, type);
    }

    public Instrument(Long id, String ticker, String name, InstrumentType type) {
        this.id = id;
        setTicker(ticker);
        setName(name);
        setType(type);
    }

    // Getters (solo lectura para mutación controlada)

    public Long getId() { return id; }

    public String getTicker() { return ticker; }

    public String getName() { return name; }

    public InstrumentType getType() { return type; }

    /** Normaliza el ticker: trim y toUpperCase */
    private void setTicker(String ticker) {
        if (ticker == null || ticker.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticker no puede ser nulo o vacío");
        }
        this.ticker = ticker.trim().toUpperCase();
    }

    /** Valida y establece el nombre */
    private void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre no puede ser nulo o vacío");
        }
        this.name = name;
    }

    /** Valida y establece el tipo */
    private void setType(InstrumentType type) {
        if (type == null) {
            throw new IllegalArgumentException("Tipo de instrumento no puede ser nulo");
        }
        this.type = type;
    }

    @Override
    public String toString() {
        return "Instrument{id=" + id + ", ticker=" + ticker + ", name=" + name + ", type=" + type + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instrument instrument = (Instrument) o;
        return Objects.equals(ticker, instrument.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker);
    }

    /** Verifica si el instrumento es una moneda */
    public boolean isCurrency() {
        return type == InstrumentType.MONEDA;
    }

    /** Verifica si el instrumento es una acción */
    public boolean isStock() {
        return type == InstrumentType.ACCIONES;
    }
}
