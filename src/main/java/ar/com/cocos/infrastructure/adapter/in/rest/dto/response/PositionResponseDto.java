package ar.com.cocos.infrastructure.adapter.in.rest.dto.response;

import java.math.BigDecimal;

public class PositionResponseDto {

    private String ticker;
    private String name;
    private int quantity;
    private BigDecimal totalValue;
    private BigDecimal totalReturn;

    public PositionResponseDto() {
    }

    public PositionResponseDto(String ticker, String name, int quantity, BigDecimal totalValue, BigDecimal totalReturn) {
        this.ticker = ticker;
        this.name = name;
        this.quantity = quantity;
        this.totalValue = totalValue;
        this.totalReturn = totalReturn;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigDecimal getTotalReturn() {
        return totalReturn;
    }

    public void setTotalReturn(BigDecimal totalReturn) {
        this.totalReturn = totalReturn;
    }
}
