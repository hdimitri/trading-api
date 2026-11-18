package ar.com.cocos.domain.model;

import ar.com.cocos.domain.enums.OrderStatus;
import ar.com.cocos.domain.enums.OrderType;
import ar.com.cocos.domain.enums.Side;
import java.time.LocalDateTime;
import java.util.Objects;

public class Order {

    private Long id;
    private Instrument instrument;
    private User user;
    private Side side;
    private double size;
    private double price;
    private OrderType type;
    private OrderStatus status;
    private LocalDateTime datetime;

    public Order() {
        this.datetime = LocalDateTime.now();
    }

    public Order(Long id, User user, Instrument instrument, Side side, OrderType type, double size, double price, OrderStatus status, LocalDateTime datetime) {
        this.id = id;
        this.user = user;
        this.instrument = instrument;
        this.side = side;
        this.type = type;
        this.size = size;
        this.price = price;
        this.status = status;
        this.datetime = datetime;
    }

    public Order(User user, Instrument instrument, Side side, OrderType type, double size, double price) {
        this.user = user;
        this.instrument = instrument;
        this.side = side;
        this.type = type;
        this.size = size;
        this.price = price;
        this.status = OrderStatus.NEW;
        this.datetime = LocalDateTime.now();
    }

    // Getters (solo lectura para mutación controlada)

    public Long getId() { return id; }

    public User getUser() { return user; }

    public Instrument getInstrument() { return instrument; }

    public Side getSide() { return side; }

    public double getSize() { return size; }

    public double getPrice() { return price; }

    public OrderType getType() { return type; }

    public OrderStatus getStatus() { return status; }

    public LocalDateTime getDateTime() { return datetime; }

    /** Calcula el monto total de la orden (size * price) */
    public double getTotalAmount() {
        return size * price;
    }

    /** Verifica si la orden es de compra */
    public boolean isBuy() {
        return side == Side.BUY;
    }

    /** Verifica si la orden es de venta */
    public boolean isSell() {
        return side == Side.SELL;
    }

    /** Verifica si la orden está llena */
    public boolean isFilled() {
        return status == OrderStatus.FILLED;
    }

    /** Verifica si la orden está en estado NEW */
    public boolean isNew() {
        return status == OrderStatus.NEW;
    }

    /** Rechazar la orden (solo desde estado NEW) */
    public void reject() {
        if (this.status == OrderStatus.NEW) {
            this.status = OrderStatus.REJECTED;
        }
    }

    /** Cancelar la orden (solo desde estado NEW) */
    public void cancel() {
        if (this.status == OrderStatus.NEW) {
            this.status = OrderStatus.CANCELLED;
        }
    }

    /** Ejecuta una orden de mercado con validación rigurosa */
    public void executeAtMarket(double marketPrice) {
        // Validar invariantes antes de mutar estado
        if (this.type != OrderType.MARKET) {
            throw new IllegalArgumentException("Solo órdenes MARKET pueden ejecutarse a mercado");
        }
        if (this.status != OrderStatus.NEW) {
            throw new IllegalStateException("Solo órdenes en estado NEW pueden ejecutarse");
        }
        if (marketPrice <= 0) {
            this.reject();
            return;
        }
        this.price = marketPrice;
        this.status = OrderStatus.FILLED;
    }

    /** Validar y transitar una orden límite */
    public void validateLimit() {
        if (this.type != OrderType.LIMIT) {
            throw new IllegalArgumentException("Solo órdenes LIMIT pueden validarse como límite");
        }
        if (this.price <= 0) {
            this.reject();
        } else {
            this.status = OrderStatus.NEW;
        }
    }

    @Override
    public String toString() {
        // Proteger contra referencias nulas
        String ticker = instrument != null ? instrument.getTicker() : "NULO";
        String account = user != null ? user.getAccountNumber() : "NULO";
        return "Order{id=" + id + ", instrument=" + ticker +
               ", user=" + account + ", side=" + side +
               ", type=" + type + ", size=" + size +
               ", price=" + price + ", status=" + status +
               ", datetime=" + datetime + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
