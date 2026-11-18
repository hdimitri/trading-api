package ar.com.cocos.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PortfolioResult {

    private final List<Position> positions;
    private final double totalValue;
    private final double availableCash;

    public PortfolioResult(List<Position> positions, double totalValue, double availableCash) {
        this.positions = positions != null
                ? Collections.unmodifiableList(new ArrayList<>(positions))
                : Collections.emptyList();
        this.totalValue = totalValue;
        this.availableCash = availableCash;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public double getAvailableCash() {
        return availableCash;
    }

    /** Retorna el valor total invertido (totalValue - availableCash). */
    public double getInvestedValue() {
        return totalValue - availableCash;
    }

    /** Retorna true si el portafolio tiene alguna posición de activo. */
    public boolean hasPositions() {
        return !positions.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioResult that = (PortfolioResult) o;
        return Double.compare(that.totalValue, totalValue) == 0
                && Double.compare(that.availableCash, availableCash) == 0
                && positions.equals(that.positions);
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(totalValue);
        int result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(availableCash);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + positions.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PortfolioResult{positions=" + positions +
                ", totalValue=" + totalValue +
                ", availableCash=" + availableCash +
                ", investedValue=" + getInvestedValue() + "}";
    }
}