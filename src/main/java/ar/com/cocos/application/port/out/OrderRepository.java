package ar.com.cocos.application.port.out;

import ar.com.cocos.domain.enums.OrderStatus;
import ar.com.cocos.domain.model.*;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByInstrumentIdAndStatus(Long instrumentId, OrderStatus status);
    Order save(Order order);
    void delete(Order order);
    Optional<Order> findById(Long id);  // Added for order lookup
}