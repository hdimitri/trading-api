package ar.com.cocos.infrastructure.adapter.in.rest.dto.response;

import java.time.LocalDate;

public class MarketDataResponseDto {

    private Long id;
    private String ticker;
    private double close;
    private double previousClose;
    private LocalDate date;

    public MarketDataResponseDto() {
    }

    public MarketDataResponseDto(Long id, String ticker, double close, double previousClose, LocalDate date) {
        this.id = id;
        this.ticker = ticker;
        this.close = close;
        this.previousClose = previousClose;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
