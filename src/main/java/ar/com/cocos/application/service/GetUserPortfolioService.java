package ar.com.cocos.application.service;

import ar.com.cocos.application.port.in.GetUserPortfolioUseCase;
import ar.com.cocos.application.port.in.result.PortfolioResult;
import ar.com.cocos.application.port.in.result.PositionResult;
import ar.com.cocos.application.port.out.MarketDataRepository;
import ar.com.cocos.application.port.out.OrderRepository;
import ar.com.cocos.application.port.out.UserRepository;
import ar.com.cocos.domain.enums.Side;
import ar.com.cocos.domain.model.MarketData;
import ar.com.cocos.domain.model.Order;
import ar.com.cocos.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GetUserPortfolioService implements GetUserPortfolioUseCase {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final MarketDataRepository marketDataRepository;

    public GetUserPortfolioService(UserRepository userRepository,
                                       OrderRepository orderRepository,
                                       MarketDataRepository marketDataRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.marketDataRepository = marketDataRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioResult execute(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<Order> filledUserOrders = orderRepository.findByUserId(userId).stream()
                .filter(Order::isFilled)
                .sorted(Comparator.comparing(Order::getDateTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        Map<Long, MarketData> latestMarketDataMap = marketDataRepository.findAll().stream()
                .filter(md -> md.getInstrument() != null && md.getInstrument().getId() != null)
                .collect(Collectors.toMap(
                        md -> md.getInstrument().getId(),
                        md -> md,
                        (md1, md2) -> isAfter(md2, md1) ? md2 : md1
                ));

        double availableCash = calculateAvailableCash(filledUserOrders);
        Map<Long, DomainPosition> positionsMap = buildPositions(filledUserOrders);

        List<PositionResult> positionResults = positionsMap.values().stream()
                .filter(pos -> pos.getQuantity() > 0)
                .map(pos -> toPositionResult(pos, latestMarketDataMap.get(pos.getInstrumentId())))
                .toList();

        double positionsMarketValue = positionResults.stream()
                .mapToDouble(pos -> pos.getTotalValue().doubleValue())
                .sum();

        double totalAccountValue = availableCash + positionsMarketValue;

        return new PortfolioResult(
                user.getAccountNumber(),
                totalAccountValue,
                availableCash,
                positionResults
        );
    }

    private double calculateAvailableCash(List<Order> filledOrders) {
        double cash = 0.0;
        for (Order order : filledOrders) {
            if (order.getSide() == Side.CASH_IN) {
                cash += order.getSize();
            } else if (order.getSide() == Side.CASH_OUT) {
                cash -= order.getSize();
            } else if (order.getSide() == Side.BUY) {
                cash -= order.getSize() * order.getPrice();
            } else if (order.getSide() == Side.SELL) {
                cash += order.getSize() * order.getPrice();
            }
        }
        return cash;
    }

    private Map<Long, DomainPosition> buildPositions(List<Order> filledOrders) {
        Map<Long, DomainPosition> positionsMap = new HashMap<>();
        for (Order order : filledOrders) {
            if (order.getInstrument() == null || order.getInstrument().isCurrency()) {
                continue;
            }
            Long instrumentId = order.getInstrument().getId();
            DomainPosition pos = positionsMap.computeIfAbsent(instrumentId, id -> new DomainPosition(
                    id,
                    order.getInstrument().getTicker(),
                    order.getInstrument().getName()
            ));

            if (order.getSide() == Side.BUY) {
                pos.addPurchase(order.getSize(), order.getPrice());
            } else if (order.getSide() == Side.SELL) {
                pos.removeSale(order.getSize());
            }
        }
        return positionsMap;
    }

    private PositionResult toPositionResult(DomainPosition pos, MarketData md) {
        double currentPrice = md != null ? md.getClose() : pos.getAveragePrice();
        double marketValue = pos.getQuantity() * currentPrice;
        double totalReturnPercentage = pos.getAveragePrice() > 0
                ? ((currentPrice - pos.getAveragePrice()) / pos.getAveragePrice()) * 100.0
                : 0.0;

        return new PositionResult(
                pos.getTicker(),
                pos.getName(),
                pos.getQuantity(),
                marketValue,
                totalReturnPercentage
        );
    }

    private boolean isAfter(MarketData candidate, MarketData current) {
        if (candidate.getDate() == null) {
            return false;
        }
        if (current.getDate() == null) {
            return true;
        }
        return candidate.getDate().isAfter(current.getDate());
    }

    private static class DomainPosition {
        private final Long instrumentId;
        private final String ticker;
        private final String name;
        private double quantity;
        private double averagePrice;

        private DomainPosition(Long instrumentId, String ticker, String name) {
            this.instrumentId = instrumentId;
            this.ticker = ticker;
            this.name = name;
        }

        private void addPurchase(double orderSize, double orderPrice) {
            double totalCost = (this.quantity * this.averagePrice) + (orderSize * orderPrice);
            this.quantity += orderSize;
            this.averagePrice = this.quantity > 0 ? totalCost / this.quantity : 0.0;
        }

        private void removeSale(double orderSize) {
            this.quantity -= orderSize;
            if (this.quantity <= 0) {
                this.quantity = 0.0;
                this.averagePrice = 0.0;
            }
        }

        private Long getInstrumentId() {
            return instrumentId;
        }

        private String getTicker() {
            return ticker;
        }

        private String getName() {
            return name;
        }

        private double getQuantity() {
            return quantity;
        }

        private double getAveragePrice() {
            return averagePrice;
        }
    }
}
