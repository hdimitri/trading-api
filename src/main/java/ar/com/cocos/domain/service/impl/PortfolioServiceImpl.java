package ar.com.cocos.domain.service.impl;

import ar.com.cocos.domain.enums.OrderStatus;
import ar.com.cocos.domain.enums.Side;
import ar.com.cocos.domain.model.MarketData;
import ar.com.cocos.domain.model.Order;
import ar.com.cocos.domain.model.PortfolioResult;
import ar.com.cocos.domain.model.Position;
import ar.com.cocos.domain.model.User;
import ar.com.cocos.domain.service.PortfolioService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PortfolioServiceImpl implements PortfolioService {

    @Override
    public PortfolioResult calculatePortfolio(User user, List<Order> userOrders, List<MarketData> allMarketData) {
        List<Order> filledOrders = userOrders == null ? List.of() : userOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.FILLED)
                .sorted(Comparator.comparing(Order::getDateTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        Map<Long, MarketData> latestMarketDataMap = new HashMap<>();
        if (allMarketData != null) {
            for (MarketData md : allMarketData) {
                if (md.getInstrument() != null && md.getInstrument().getId() != null) {
                    Long instId = md.getInstrument().getId();
                    MarketData existing = latestMarketDataMap.get(instId);
                    if (existing == null || (md.getDate() != null && existing.getDate() != null && md.getDate().isAfter(existing.getDate()))) {
                        latestMarketDataMap.put(instId, md);
                    }
                }
            }
        }

        double availableCash = 0.0;
        Map<Long, Position> positionsMap = new HashMap<>();

        for (Order order : filledOrders) {
            if (order.getSide() == Side.CASH_IN) {
                availableCash += order.getSize();
                continue;
            }
            if (order.getSide() == Side.CASH_OUT) {
                availableCash -= order.getSize();
                continue;
            }
            if (order.getSide() == Side.BUY) {
                availableCash -= (order.getSize() * order.getPrice());
            } else if (order.getSide() == Side.SELL) {
                availableCash += (order.getSize() * order.getPrice());
            }

            if (order.getInstrument() == null || order.getInstrument().isCurrency()) {
                continue;
            }

            Long instrumentId = order.getInstrument().getId();
            Position position = positionsMap.computeIfAbsent(
                    instrumentId,
                    id -> new Position(order.getInstrument(), 0, 0)
            );

            if (order.getSide() == Side.BUY) {
                position.addPurchase(order.getSize(), order.getPrice());
            } else if (order.getSide() == Side.SELL) {
                double sellQty = Math.min(order.getSize(), position.getQuantity());
                if (sellQty > 0) {
                    position.removeSale(sellQty, order.getPrice());
                }
            }
        }

        List<Position> activePositions = new ArrayList<>();
        double investedMarketValue = 0.0;

        for (Position position : positionsMap.values()) {
            if (position.getQuantity() <= 0) {
                continue;
            }
            MarketData md = latestMarketDataMap.get(position.getInstrument().getId());
            if (md != null) {
                double totalReturnPercent = position.getAveragePrice() > 0
                        ? ((md.getClose() - position.getAveragePrice()) / position.getAveragePrice()) * 100.0
                        : 0.0;
                position.updateMarketValuation(md.getClose(), totalReturnPercent);
            }
            investedMarketValue += position.getTotalValue();
            activePositions.add(position);
        }

        double totalAccountValue = availableCash + investedMarketValue;
        return new PortfolioResult(activePositions, totalAccountValue, availableCash);
    }
}
