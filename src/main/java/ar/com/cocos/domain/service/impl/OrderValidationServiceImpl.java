package ar.com.cocos.domain.service.impl;

import ar.com.cocos.domain.enums.OrderStatus;
import ar.com.cocos.domain.enums.Side;
import ar.com.cocos.domain.model.Order;
import ar.com.cocos.domain.model.User;
import ar.com.cocos.domain.service.OrderValidationService;

import java.util.List;
import java.util.Objects;

public class OrderValidationServiceImpl implements OrderValidationService {

    @Override
    public ValidationResult validateOrder(Order order, User user, List<Order> existingOrders) {
        if (order == null || order.getInstrument() == null || order.getSide() == null) {
            return ValidationResult.INVALID;
        }
        if (order.getSize() <= 0) {
            return ValidationResult.INVALID;
        }

        boolean isCurrency = order.getInstrument().isCurrency();
        Side side = order.getSide();

        if (side == Side.CASH_IN || side == Side.CASH_OUT) {
            if (!isCurrency) {
                return ValidationResult.INVALID;
            }
            if (side == Side.CASH_IN) {
                return ValidationResult.VALID;
            }
            double availableCash = calculateAvailableCash(existingOrders);
            return order.getSize() <= availableCash ? ValidationResult.VALID : ValidationResult.INVALID;
        }

        if (isCurrency) {
            return ValidationResult.INVALID;
        }
        if (order.getSize() % 1 != 0) {
            return ValidationResult.INVALID;
        }
        if (order.getPrice() <= 0) {
            return ValidationResult.INVALID;
        }

        if (side == Side.BUY) {
            double cost = order.getSize() * order.getPrice();
            double availableCash = calculateAvailableCash(existingOrders);
            return cost <= availableCash ? ValidationResult.VALID : ValidationResult.INVALID;
        }

        if (side == Side.SELL) {
            double availableShares = calculateAvailableShares(order.getInstrument().getId(), existingOrders);
            return order.getSize() <= availableShares ? ValidationResult.VALID : ValidationResult.INVALID;
        }

        return ValidationResult.INVALID;
    }

    @Override
    public double calculateAvailableCash(List<Order> existingOrders) {
        if (existingOrders == null) {
            return 0.0;
        }
        double available = 0.0;
        for (Order o : existingOrders) {
            if (o.getStatus() == OrderStatus.FILLED) {
                if (o.getSide() == Side.CASH_IN) {
                    available += o.getSize();
                } else if (o.getSide() == Side.CASH_OUT) {
                    available -= o.getSize();
                } else if (o.getSide() == Side.BUY) {
                    available -= (o.getSize() * o.getPrice());
                } else if (o.getSide() == Side.SELL) {
                    available += (o.getSize() * o.getPrice());
                }
            } else if (o.getStatus() == OrderStatus.NEW) {
                if (o.getSide() == Side.CASH_OUT) {
                    available -= o.getSize();
                } else if (o.getSide() == Side.BUY) {
                    available -= (o.getSize() * o.getPrice());
                }
            }
        }
        return available;
    }

    @Override
    public double calculateAvailableShares(Long instrumentId, List<Order> existingOrders) {
        if (instrumentId == null || existingOrders == null) {
            return 0.0;
        }
        double shares = 0.0;
        for (Order o : existingOrders) {
            if (o.getInstrument() != null && Objects.equals(o.getInstrument().getId(), instrumentId)) {
                if (o.getStatus() == OrderStatus.FILLED) {
                    if (o.getSide() == Side.BUY) {
                        shares += o.getSize();
                    } else if (o.getSide() == Side.SELL) {
                        shares -= o.getSize();
                    }
                } else if (o.getStatus() == OrderStatus.NEW && o.getSide() == Side.SELL) {
                    shares -= o.getSize();
                }
            }
        }
        return shares;
    }

    @Override
    public boolean validateLimitPrice(double price) {
        return price > 0;
    }
}
