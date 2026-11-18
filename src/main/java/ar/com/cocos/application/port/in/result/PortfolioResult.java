package ar.com.cocos.application.port.in.result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

public class PortfolioResult {

    private final String accountNumber;
    private final BigDecimal totalValue;
    private final BigDecimal availableCash;
    private final List<PositionResult> positions;

    public PortfolioResult(String accountNumber, double totalValue, double availableCash, List<PositionResult> positions) {
        this.accountNumber = accountNumber;
        this.totalValue = BigDecimal.valueOf(totalValue).setScale(2, RoundingMode.HALF_UP);
        this.availableCash = BigDecimal.valueOf(availableCash).setScale(2, RoundingMode.HALF_UP);
        this.positions = positions != null ? Collections.unmodifiableList(positions) : Collections.emptyList();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public BigDecimal getAvailableCash() {
        return availableCash;
    }

    public List<PositionResult> getPositions() {
        return positions;
    }
}
