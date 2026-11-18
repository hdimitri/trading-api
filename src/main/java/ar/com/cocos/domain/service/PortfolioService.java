package ar.com.cocos.domain.service;

import ar.com.cocos.domain.model.MarketData;
import ar.com.cocos.domain.model.Order;
import ar.com.cocos.domain.model.PortfolioResult;
import ar.com.cocos.domain.model.User;
import java.util.List;

public interface PortfolioService {
    PortfolioResult calculatePortfolio(User user, List<Order> userOrders, List<MarketData> marketData);
}
