package ar.com.cocos.application.service;

import ar.com.cocos.application.port.in.ExecuteOrderUseCase;
import ar.com.cocos.application.port.in.command.ExecuteOrderCommand;
import ar.com.cocos.application.port.in.result.OrderResult;
import ar.com.cocos.application.port.out.InstrumentRepository;
import ar.com.cocos.application.port.out.MarketDataRepository;
import ar.com.cocos.application.port.out.OrderRepository;
import ar.com.cocos.application.port.out.UserRepository;
import ar.com.cocos.domain.enums.OrderType;
import ar.com.cocos.domain.enums.Side;
import ar.com.cocos.domain.model.Instrument;
import ar.com.cocos.domain.model.MarketData;
import ar.com.cocos.domain.model.Order;
import ar.com.cocos.domain.model.User;
import ar.com.cocos.domain.service.OrderValidationService;
import ar.com.cocos.domain.service.impl.OrderValidationServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ExecuteOrderService implements ExecuteOrderUseCase {

    private final OrderRepository orderRepository;
    private final InstrumentRepository instrumentRepository;
    private final UserRepository userRepository;
    private final MarketDataRepository marketDataRepository;
    private final OrderValidationService orderValidationService;

    public ExecuteOrderService(OrderRepository orderRepository,
                               InstrumentRepository instrumentRepository,
                               UserRepository userRepository,
                               MarketDataRepository marketDataRepository) {
        this.orderRepository = orderRepository;
        this.instrumentRepository = instrumentRepository;
        this.userRepository = userRepository;
        this.marketDataRepository = marketDataRepository;
        this.orderValidationService = new OrderValidationServiceImpl();
    }

    @Override
    @Transactional
    public OrderResult execute(ExecuteOrderCommand command) {
        if (command == null || command.getUserId() == null) {
            throw new IllegalArgumentException("userId is required");
        }

        Long userId = command.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Instrument instrument = instrumentRepository.findById(command.getInstrumentId())
                .orElseThrow(() -> new IllegalArgumentException("Instrument not found: " + command.getInstrumentId()));

        Side side = Side.valueOf(command.getSide().trim().toUpperCase());
        OrderType type = command.getType() != null && !command.getType().isBlank()
                ? OrderType.valueOf(command.getType().trim().toUpperCase())
                : OrderType.MARKET;

        double executionPrice = resolvePrice(side, type, instrument, command.getPrice());
        if (executionPrice <= 0) {
            Order rejected = new Order(user, instrument, side, type,
                    command.getSize() != null ? command.getSize() : 0.0, 0.0);
            rejected.reject();
            Order saved = orderRepository.save(rejected);
            return OrderResult.rejected(saved.getId(), "No valid price available to execute order");
        }

        double resolvedSize = resolveSize(instrument, command.getSize(), command.getAmount(), executionPrice);

        Order order = new Order(user, instrument, side, type, resolvedSize, executionPrice);
        List<Order> existingOrders = orderRepository.findByUserId(userId);

        var validationResult = orderValidationService.validateOrder(order, user, existingOrders);
        if (validationResult == OrderValidationService.ValidationResult.INVALID) {
            String errorMsg = getRejectionReason(order, existingOrders);
            order.reject();
            Order saved = orderRepository.save(order);
            return OrderResult.rejected(saved.getId(), errorMsg);
        }

        if (type == OrderType.MARKET || side == Side.CASH_IN || side == Side.CASH_OUT) {
            order.executeAtMarket(executionPrice);
        } else {
            order.validateLimit();
        }

        Order saved = orderRepository.save(order);
        boolean accepted = saved.isFilled() || saved.isNew();
        String status = saved.getStatus() != null ? saved.getStatus().name() : null;

        return accepted
                ? OrderResult.success(saved.getId(), status)
                : OrderResult.rejected(saved.getId(), "Order rejected");
    }

    @Override
    @Transactional
    public OrderResult cancel(Long orderId, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getUser() != null && order.getUser().getId() != null
                && !Objects.equals(order.getUser().getId(), userId)) {
            return new OrderResult(order.getId(), order.getStatus().name(), false, "Order does not belong to user");
        }

        if (!order.isNew()) {
            return new OrderResult(order.getId(), order.getStatus().name(), false, "Only orders with NEW status can be cancelled");
        }

        order.cancel();
        Order saved = orderRepository.save(order);
        return OrderResult.success(saved.getId(), saved.getStatus().name());
    }

    private double resolvePrice(Side side, OrderType type, Instrument instrument, Double requestPrice) {
        if (side == Side.CASH_IN || side == Side.CASH_OUT || instrument.isCurrency()) {
            return 1.0;
        }
        if (type == OrderType.LIMIT) {
            return requestPrice != null ? requestPrice : 0.0;
        }
        return marketDataRepository.findLatestByInstrumentId(instrument.getId())
                .map(MarketData::getClose)
                .orElse(0.0);
    }

    private double resolveSize(Instrument instrument, Double requestSize, Double requestAmount, double price) {
        if (requestSize != null && requestSize > 0) {
            return requestSize;
        }
        if (requestAmount != null && requestAmount > 0 && price > 0) {
            if (instrument.isCurrency()) {
                return requestAmount;
            }
            return Math.floor(requestAmount / price);
        }
        return 0.0;
    }

    private String getRejectionReason(Order order, List<Order> existingOrders) {
        if (order.getSize() <= 0) {
            return "Invalid order size or insufficient investment amount for 1 share";
        }
        boolean isCurrency = order.getInstrument().isCurrency();
        Side side = order.getSide();

        if ((side == Side.CASH_IN || side == Side.CASH_OUT) && !isCurrency) {
            return side.name() + " only applies to currency instruments";
        }
        if ((side == Side.BUY || side == Side.SELL) && isCurrency) {
            return side.name() + " cannot be used on currency instruments";
        }
        if (!isCurrency && order.getSize() % 1 != 0) {
            return "Fractional share quantities are not allowed";
        }
        if (side == Side.BUY) {
            double cost = order.getSize() * order.getPrice();
            double available = orderValidationService.calculateAvailableCash(existingOrders);
            if (cost > available) {
                return "Insufficient cash balance. Required: " + cost + ", Available: " + available;
            }
        } else if (side == Side.CASH_OUT) {
            double available = orderValidationService.calculateAvailableCash(existingOrders);
            if (order.getSize() > available) {
                return "Insufficient cash balance for withdrawal. Required: " + order.getSize() + ", Available: " + available;
            }
        } else if (side == Side.SELL) {
            double availableShares = orderValidationService.calculateAvailableShares(
                    order.getInstrument().getId(), existingOrders);
            if (order.getSize() > availableShares) {
                return "Insufficient quantity. Required: " + order.getSize() + ", Available: " + availableShares;
            }
        }
        return "Order rejected";
    }
}
