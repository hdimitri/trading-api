package ar.com.cocos.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "marketdata")
public class MarketDataJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrumentid", columnDefinition = "int4")
    private InstrumentJpaEntity instrument;

    @Column(name = "\"close\"")
    private Double close;

    @Column(name = "previousclose", precision = 10, scale = 2)
    private BigDecimal previousClose;

    @Column(name = "\"date\"")
    private LocalDate date;

    @Column(name = "high")
    private Double high;

    @Column(name = "low")
    private Double low;

    @Column(name = "\"open\"")
    private Double open;

    public MarketDataJpaEntity() {
    }

    public MarketDataJpaEntity(InstrumentJpaEntity instrument, Double close, BigDecimal previousClose,
                               LocalDate date, Double high, Double low, Double open) {
        this.instrument = instrument;
        this.close = close;
        this.previousClose = previousClose;
        this.date = date;
        this.high = high;
        this.low = low;
        this.open = open;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public InstrumentJpaEntity getInstrument() { return instrument; }
    public void setInstrument(InstrumentJpaEntity instrument) { this.instrument = instrument; }

    public Double getClose() { return close; }
    public void setClose(Double close) { this.close = close; }

    public BigDecimal getPreviousClose() { return previousClose; }
    public void setPreviousClose(BigDecimal previousClose) { this.previousClose = previousClose; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getHigh() { return high; }
    public void setHigh(Double high) { this.high = high; }

    public Double getLow() { return low; }
    public void setLow(Double low) { this.low = low; }

    public Double getOpen() { return open; }
    public void setOpen(Double open) { this.open = open; }
}
