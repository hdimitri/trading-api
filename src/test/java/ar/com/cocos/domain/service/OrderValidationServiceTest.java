package ar.com.cocos.domain.service;

import ar.com.cocos.domain.enums.InstrumentType;
import ar.com.cocos.domain.enums.OrderStatus;
import ar.com.cocos.domain.enums.OrderType;
import ar.com.cocos.domain.enums.Side;
import ar.com.cocos.domain.model.Instrument;
import ar.com.cocos.domain.model.Order;
import ar.com.cocos.domain.model.User;
import ar.com.cocos.domain.service.impl.OrderValidationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderValidationServiceTest {

    private OrderValidationService validationService;
    private User user;
    private Instrument ars;
    private Instrument pamp;

    @BeforeEach
    void setUp() {
        validationService = new OrderValidationServiceImpl();
        user = new User(1L, "trader@cocos.com", "10001", Collections.emptyList(), Collections.emptyList());
        ars = new Instrument(66L, "ARS", "PESOS", InstrumentType.MONEDA);
        pamp = new Instrument(31L, "PAMP", "Pampa Energía S.A.", InstrumentType.ACCIONES);
    }

    @Test
    void shouldValidateCashInOnCurrencyInstrument() {
        Order cashIn = new Order(user, ars, Side.CASH_IN, OrderType.MARKET, 100_000.0, 1.0);
        assertEquals(OrderValidationService.ValidationResult.VALID,
                validationService.validateOrder(cashIn, user, Collections.emptyList()));
    }

    @Test
    void shouldRejectBuyWhenInsufficientCash() {
        Order cashIn = filledOrder(1L, ars, Side.CASH_IN, 50_000.0, 1.0);
        Order buyOrder = new Order(user, pamp, Side.BUY, OrderType.MARKET, 100.0, 925.85); // 92,585 > 50,000

        assertEquals(OrderValidationService.ValidationResult.INVALID,
                validationService.validateOrder(buyOrder, user, List.of(cashIn)));
    }

    @Test
    void shouldRejectFractionalSharesOnStockInstrument() {
        Order cashIn = filledOrder(1L, ars, Side.CASH_IN, 100_000.0, 1.0);
        Order fractionalBuy = new Order(user, pamp, Side.BUY, OrderType.MARKET, 10.5, 925.85);

        assertEquals(OrderValidationService.ValidationResult.INVALID,
                validationService.validateOrder(fractionalBuy, user, List.of(cashIn)));
    }

    @Test
    void shouldRejectBuyWhenActiveNewLimitOrderAlreadyReservesCash() {
        Order cashIn = filledOrder(1L, ars, Side.CASH_IN, 100_000.0, 1.0);
        Order pendingLimitBuy = new Order(2L, user, pamp, Side.BUY, OrderType.LIMIT, 90.0, 1_000.0, OrderStatus.NEW, LocalDateTime.now());
        Order secondBuy = new Order(user, pamp, Side.BUY, OrderType.MARKET, 20.0, 1_000.0); // 90k + 20k > 100k

        assertEquals(OrderValidationService.ValidationResult.INVALID,
                validationService.validateOrder(secondBuy, user, List.of(cashIn, pendingLimitBuy)));
    }

    @Test
    void shouldRejectSellWhenInsufficientAvailableShares() {
        Order buyFilled = filledOrder(1L, pamp, Side.BUY, 50.0, 900.0);
        Order sellOrder = new Order(user, pamp, Side.SELL, OrderType.MARKET, 60.0, 925.85);

        assertEquals(OrderValidationService.ValidationResult.INVALID,
                validationService.validateOrder(sellOrder, user, List.of(buyFilled)));
    }

    @Test
    void shouldRejectBuyOrSellDirectlyOnCurrencyInstrument() {
        Order cashIn = filledOrder(1L, ars, Side.CASH_IN, 100_000.0, 1.0);
        Order buyCurrency = new Order(user, ars, Side.BUY, OrderType.MARKET, 1_000.0, 1.0);

        assertEquals(OrderValidationService.ValidationResult.INVALID,
                validationService.validateOrder(buyCurrency, user, List.of(cashIn)));
    }

    private Order filledOrder(Long id, Instrument instrument, Side side, double size, double price) {
        return new Order(id, user, instrument, side, OrderType.MARKET, size, price, OrderStatus.FILLED, LocalDateTime.now());
    }
}
