package ar.com.cocos.domain.service;

import ar.com.cocos.domain.model.Order;
import ar.com.cocos.domain.model.User;
import java.util.List;

public interface OrderValidationService {

    enum ValidationResult {
        VALID, INVALID
    }

    ValidationResult validateOrder(Order order, User user, List<Order> existingOrders);

    double calculateAvailableCash(List<Order> existingOrders);

    double calculateAvailableShares(Long instrumentId, List<Order> existingOrders);

    boolean validateLimitPrice(double price);
}
