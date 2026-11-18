package ar.com.cocos.infrastructure.adapter.in.rest.dto.response;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class PortfolioResponseDto {

    private String accountNumber;
    private BigDecimal totalValue;
    private BigDecimal availableCash;
    private List<PositionResponseDto> positions;

    public PortfolioResponseDto() {
    }

    public PortfolioResponseDto(String accountNumber, BigDecimal totalValue, BigDecimal availableCash, List<PositionResponseDto> positions) {
        this.accountNumber = accountNumber;
        this.totalValue = totalValue;
        this.availableCash = availableCash;
        this.positions = positions != null ? positions : Collections.emptyList();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigDecimal getAvailableCash() {
        return availableCash;
    }

    public void setAvailableCash(BigDecimal availableCash) {
        this.availableCash = availableCash;
    }

    public List<PositionResponseDto> getPositions() {
        return positions;
    }

    public void setPositions(List<PositionResponseDto> positions) {
        this.positions = positions;
    }
}
