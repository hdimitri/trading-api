package ar.com.cocos.application.port.in.result;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PositionResult {

    private final String ticker;
    private final String name;
    private final int quantity;
    private final BigDecimal totalValue;
    private final BigDecimal totalReturn;

    public PositionResult(String ticker, String name, double quantity, double totalValue, double totalReturn) {
        this.ticker = ticker;
        this.name = name;
        this.quantity = (int) Math.round(quantity);
        this.totalValue = BigDecimal.valueOf(totalValue).setScale(2, RoundingMode.HALF_UP);
        this.totalReturn = BigDecimal.valueOf(totalReturn).setScale(2, RoundingMode.HALF_UP);
    }

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public BigDecimal getTotalReturn() {
        return totalReturn;
    }
}
