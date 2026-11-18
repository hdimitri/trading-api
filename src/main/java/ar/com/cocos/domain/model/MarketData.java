package ar.com.cocos.domain.model;

import java.time.LocalDate;
import java.util.Objects;

public class MarketData {

    private Long id;
    private Instrument instrument;
    private double close;
    private double previousClose;
    private LocalDate date;
    private double high;
    private double low;
    private double open;

    // Constructor de creación sin id (para nuevas cotizaciones)
    public MarketData(Instrument instrument, double close, double previousClose, LocalDate date,
                      double high, double low, double open) {
        if (instrument == null) {
            throw new IllegalArgumentException("Instrument no puede ser nulo");
        }
        if (date == null) {
            throw new IllegalArgumentException("Fecha no puede ser nula");
        }
        if (close < 0) {
            throw new IllegalArgumentException("Close no puede ser negativo");
        }
        if (previousClose < 0) {
            throw new IllegalArgumentException("Previous close no puede ser negativo");
        }
        if (high < 0) {
            throw new IllegalArgumentException("High no puede ser negativo");
        }
        if (low < 0) {
            throw new IllegalArgumentException("Low no puede ser negativo");
        }
        if (open < 0) {
            throw new IllegalArgumentException("Open no puede ser negativo");
        }
        this.instrument = instrument;
        this.close = close;
        this.previousClose = previousClose;
        this.date = date;
        this.high = high;
        this.low = low;
        this.open = open;
    }

    // Constructor completo con id (para reconstitución desde persistencia)
    public MarketData(Long id, Instrument instrument, double close, double previousClose, LocalDate date,
                      double high, double low, double open) {
        this(instrument, close, previousClose, date, high, low, open);
        this.id = id;
    }

    // Getters (solo lectura para mutación controlada)

    public Long getId() { return id; }

    public Instrument getInstrument() { return instrument; }

    public double getClose() { return close; }

    public double getPreviousClose() { return previousClose; }

    public LocalDate getDate() { return date; }

    public double getHigh() { return high; }

    public double getLow() { return low; }

    public double getOpen() { return open; }

    /** Calcula el retorno diario */
    public double calculateDailyReturn() {
        if (previousClose <= 0) return 0;
        return (close - previousClose) / previousClose;
    }

    public double getDailyReturnPercent() {
        return calculateDailyReturn() * 100;
    }

    /** Precio de cambio bruto */
    public double getPriceChange() {
        return close - previousClose;
    }

    /** Verifica si hay precio de cierre válido */
    public boolean hasValidClosePrice() {
        return close > 0;
    }

    /** Verifica si el retorno es positivo */
    public boolean isPositiveReturn() {
        return calculateDailyReturn() > 0;
    }

    /** Verifica si el retorno es negativo */
    public boolean isNegativeReturn() {
        return calculateDailyReturn() < 0;
    }

    @Override
    public String toString() {
        // Proteger contra referencias nulas
        String ticker = instrument != null ? instrument.getTicker() : "NULO";
        return "MarketData{id=" + id + ", instrument=" + ticker +
               ", close=" + close + ", previousClose=" + previousClose +
               ", date=" + date + ", dailyReturn=" + calculateDailyReturn() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketData that = (MarketData) o;
        return Objects.equals(instrument, that.instrument) &&
               Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instrument, date);
    }
}
