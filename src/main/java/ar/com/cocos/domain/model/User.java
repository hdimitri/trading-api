package ar.com.cocos.domain.model;

import ar.com.cocos.domain.enums.Side;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class User {

    private Long id;
    private String email;
    private String accountNumber;
    private final List<Order> orders;
    private final List<Position> positions;

    public User() {
        this.orders = new ArrayList<>();
        this.positions = new ArrayList<>();
    }

    public User(String email, String accountNumber) {
        this();
        setEmail(email);
        setAccountNumber(accountNumber);
    }

    public User(Long id, String email, String accountNumber, List<Position> positions, List<Order> orders) {
        this();
        this.id = id;
        setEmail(email);
        setAccountNumber(accountNumber);
        if (positions != null) {
            this.positions.addAll(positions);
        }
        if (orders != null) {
            this.orders.addAll(orders);
        }
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    public List<Position> getPositions() {
        return Collections.unmodifiableList(positions);
    }

    private void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email no puede ser nulo o vacío");
        }
        this.email = email.trim();
    }

    private void setAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number no puede ser nulo o vacío");
        }
        this.accountNumber = accountNumber.trim();
    }

    public void addOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order no puede ser nula");
        }
        if (!orders.contains(order)) {
            orders.add(order);
        }
    }

    public void applyOrderExecution(Order order, double executionPrice, MarketData marketData) {
        if (order == null || order.getInstrument() == null) {
            throw new IllegalArgumentException("Order e Instrument no pueden ser nulos");
        }
        if (order.getSide() != Side.BUY && order.getSide() != Side.SELL) {
            return; // CASH_IN / CASH_OUT no modifican tenencias de instrumentos
        }

        Long instrumentId = order.getInstrument().getId();
        Optional<Position> existingPosition = instrumentId != null
                ? getPositionByInstrumentId(instrumentId)
                : positions.stream()
                        .filter(p -> Objects.equals(p.getInstrument(), order.getInstrument()))
                        .findFirst();

        Position targetPosition;
        if (existingPosition.isPresent()) {
            targetPosition = existingPosition.get();
            if (order.getSide() == Side.BUY) {
                targetPosition.addPurchase(order.getSize(), executionPrice);
            } else {
                targetPosition.removeSale(order.getSize(), executionPrice);
            }
            if (targetPosition.isEmpty()) {
                positions.remove(targetPosition);
                return;
            }
        } else {
            if (order.getSide() == Side.SELL) {
                throw new IllegalStateException("No existe posición previa para ejecutar una venta");
            }
            targetPosition = new Position(order.getInstrument(), order.getSize(), executionPrice);
            positions.add(targetPosition);
        }

        if (marketData != null) {
            targetPosition.updateMarketValuation(marketData);
        }
    }

    public Optional<Position> getPositionByInstrumentId(long instrumentId) {
        return positions.stream()
                .filter(p -> p.getInstrument() != null)
                .filter(p -> Objects.equals(p.getInstrument().getId(), instrumentId))
                .findFirst();
    }

    public boolean hasAvailableQuantity(long instrumentId, double requiredQuantity) {
        if (requiredQuantity <= 0) {
            throw new IllegalArgumentException("La cantidad requerida debe ser positiva");
        }
        return getPositionByInstrumentId(instrumentId)
                .filter(p -> p.getQuantity() >= requiredQuantity)
                .isPresent();
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", email=" + email + ", accountNumber=" + accountNumber +
               ", positionsSize=" + positions.size() + ", ordersSize=" + orders.size() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(accountNumber, user.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}
