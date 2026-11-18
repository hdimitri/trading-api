package ar.com.cocos.domain.model;

import java.util.Objects;

public class Position {

    private Long id;
    private Instrument instrument;
    private double quantity; // cantidad de acciones (entera, nunca fraccionaria)
    private double averagePrice; // precio promedio de compra
    private double totalValue; // valor de mercado actual
    private double dailyReturn; // retorno diario en porcentaje

    public Position(Instrument instrument, double quantity, double averagePrice) {
        if (instrument == null) {
            throw new IllegalArgumentException("Instrument no puede ser nulo");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Cantidad no puede ser negativa");
        }
        if (averagePrice < 0) {
            throw new IllegalArgumentException("Precio promedio no puede ser negativo");
        }
        this.instrument = instrument;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.totalValue = 0.0;
        this.dailyReturn = 0.0;
    }

    public Position(Long id, Instrument instrument, double quantity, double averagePrice,
                    double totalValue, double dailyReturn) {
        this(instrument, quantity, averagePrice);
        this.id = id;
        this.totalValue = totalValue;
        this.dailyReturn = dailyReturn;
    }

    public Long getId() { return id; }

    public Instrument getInstrument() { return instrument; }

    public double getQuantity() { return quantity; }

    public double getAveragePrice() { return averagePrice; }

    public double getTotalValue() { return totalValue; }

    public double getDailyReturn() { return dailyReturn; }

    public boolean isEmpty() {
        return quantity == 0;
    }

    public double getUnrealizedPnL() {
        return totalValue - (quantity * averagePrice);
    }

    /** Compra de títulos: valida orderSize > 0 y orderPrice > 0, actualiza PPP */
    public void addPurchase(double orderSize, double orderPrice) {
        if (orderSize <= 0) {
            throw new IllegalArgumentException("Tamaño de orden debe ser positivo");
        }
        if (orderPrice <= 0) {
            throw new IllegalArgumentException("Precio de orden debe ser positivo");
        }
        if (quantity == 0) {
            quantity = orderSize;
            averagePrice = orderPrice;
        } else {
            double previousCost = quantity * averagePrice;
            quantity += orderSize;
            averagePrice = (previousCost + (orderSize * orderPrice)) / quantity;
        }
    }

    /** Venta de títulos: valida orderSize > 0 y orderSize <= quantity */
    public void removeSale(double orderSize, double orderPrice) {
        if (orderSize <= 0) {
            throw new IllegalArgumentException("Tamaño de orden de venta debe ser positivo");
        }
        if (orderSize > quantity) {
            throw new IllegalArgumentException("No se puede vender más de la tenencia actual");
        }
        quantity -= orderSize;
        if (quantity == 0) {
            averagePrice = 0;
            totalValue = 0;
        }
    }

    /** Actualiza valuación de mercado usando MarketData */
    public void updateMarketValuation(MarketData marketData) {
        if (marketData == null) {
            throw new IllegalArgumentException("MarketData no puede ser nulo");
        }
        updateMarketValuation(marketData.getClose(), marketData.getDailyReturnPercent());
    }

    /** Actualiza valuación de mercado con precio actual y retorno porcentual */
    public void updateMarketValuation(double currentPrice, double dailyReturnPercent) {
        if (currentPrice < 0) {
            throw new IllegalArgumentException("El precio actual no puede ser negativo");
        }
        totalValue = quantity * currentPrice;
        dailyReturn = dailyReturnPercent;
    }

    @Override
    public String toString() {
        String ticker = instrument != null ? instrument.getTicker() : "NULO";
        return "Position{id=" + (id != null ? id : "null") + ", instrument=" + ticker +
               ", quantity=" + quantity + ", avgPrice=" + averagePrice +
               ", totalValue=" + totalValue + ", dailyReturn=" + dailyReturn + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Objects.equals(instrument, position.instrument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instrument);
    }
}
