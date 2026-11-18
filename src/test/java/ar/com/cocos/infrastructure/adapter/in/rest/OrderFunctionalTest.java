package ar.com.cocos.infrastructure.adapter.in.rest;

import ar.com.cocos.CocosChallengeApplication;
import ar.com.cocos.application.port.out.InstrumentRepository;
import ar.com.cocos.application.port.out.MarketDataRepository;
import ar.com.cocos.application.port.out.OrderRepository;
import ar.com.cocos.application.port.out.UserRepository;
import ar.com.cocos.domain.enums.InstrumentType;
import ar.com.cocos.domain.enums.OrderStatus;
import ar.com.cocos.domain.enums.OrderType;
import ar.com.cocos.domain.enums.Side;
import ar.com.cocos.domain.model.Instrument;
import ar.com.cocos.domain.model.MarketData;
import ar.com.cocos.domain.model.Order;
import ar.com.cocos.domain.model.User;
import ar.com.cocos.infrastructure.adapter.out.persistence.repository.SpringDataInstrumentRepository;
import ar.com.cocos.infrastructure.adapter.out.persistence.repository.SpringDataMarketDataRepository;
import ar.com.cocos.infrastructure.adapter.out.persistence.repository.SpringDataOrderRepository;
import ar.com.cocos.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CocosChallengeApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderFunctionalTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private InstrumentRepository instrumentRepository;
    @Autowired private MarketDataRepository marketDataRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private SpringDataOrderRepository springDataOrderRepository;
    @Autowired private SpringDataMarketDataRepository springDataMarketDataRepository;
    @Autowired private SpringDataInstrumentRepository springDataInstrumentRepository;
    @Autowired private SpringDataUserRepository springDataUserRepository;

    private User emiliano;
    private Instrument ars;
    private Instrument pamp;
    private Instrument bma;
    private Instrument metr;
    private Instrument ggal;

    @BeforeEach
    void setUp() {
        springDataOrderRepository.deleteAll();
        springDataMarketDataRepository.deleteAll();
        springDataInstrumentRepository.deleteAll();
        springDataUserRepository.deleteAll();

        emiliano = userRepository.save(new User(null, "emiliano@test.com", "10001", Collections.emptyList(), Collections.emptyList()));
        ars = instrumentRepository.save(new Instrument(null, "ARS", "PESOS", InstrumentType.MONEDA));
        pamp = instrumentRepository.save(new Instrument(null, "PAMP", "Pampa Holding S.A.", InstrumentType.ACCIONES));
        bma = instrumentRepository.save(new Instrument(null, "BMA", "Banco Macro S.A.", InstrumentType.ACCIONES));
        metr = instrumentRepository.save(new Instrument(null, "METR", "MetroGAS S.A.", InstrumentType.ACCIONES));
        ggal = instrumentRepository.save(new Instrument(null, "GGAL", "Grupo Financiero Galicia", InstrumentType.ACCIONES));

        marketDataRepository.save(new MarketData(null, pamp, 921.80, 893.65, LocalDate.of(2023, 7, 13), 924.60, 896.00, 900.00));
        marketDataRepository.save(new MarketData(null, pamp, 925.85, 921.80, LocalDate.of(2023, 7, 14), 927.70, 902.05, 920.15));
        marketDataRepository.save(new MarketData(null, bma, 1520.25, 1413.50, LocalDate.of(2023, 7, 13), 1541.00, 1415.00, 1425.00));
        marketDataRepository.save(new MarketData(null, bma, 1502.80, 1520.25, LocalDate.of(2023, 7, 14), 1560.00, 1470.00, 1525.00));
        marketDataRepository.save(new MarketData(null, metr, 232.00, 229.00, LocalDate.of(2023, 7, 13), 232.00, 222.50, 232.00));
        marketDataRepository.save(new MarketData(null, metr, 229.50, 232.00, LocalDate.of(2023, 7, 14), 233.00, 225.25, 232.00));
        marketDataRepository.save(new MarketData(null, ggal, 885.80, 917.75, LocalDate.of(2023, 7, 14), 918.90, 870.00, 915.80));
    }

    @Test
    void shouldCalculateExactPortfolioFromOfficialDataset() throws Exception {
        orderRepository.save(new Order(null, emiliano, ars, Side.CASH_IN, OrderType.MARKET, 1000000.0, 1.00, OrderStatus.FILLED, LocalDateTime.of(2023, 7, 12, 12, 11, 20)));
        orderRepository.save(new Order(null, emiliano, pamp, Side.BUY, OrderType.MARKET, 50.0, 930.00, OrderStatus.FILLED, LocalDateTime.of(2023, 7, 12, 12, 31, 20)));
        orderRepository.save(new Order(null, emiliano, pamp, Side.BUY, OrderType.LIMIT, 50.0, 920.00, OrderStatus.CANCELLED, LocalDateTime.of(2023, 7, 12, 14, 21, 20)));
        orderRepository.save(new Order(null, emiliano, pamp, Side.SELL, OrderType.MARKET, 10.0, 940.00, OrderStatus.FILLED, LocalDateTime.of(2023, 7, 12, 14, 51, 20)));
        orderRepository.save(new Order(null, emiliano, pamp, Side.SELL, OrderType.MARKET, 100.0, 950.00, OrderStatus.REJECTED, LocalDateTime.of(2023, 7, 12, 16, 11, 20)));
        orderRepository.save(new Order(null, emiliano, ars, Side.CASH_OUT, OrderType.MARKET, 100000.0, 1.00, OrderStatus.FILLED, LocalDateTime.of(2023, 7, 13, 12, 31, 20)));
        orderRepository.save(new Order(null, emiliano, bma, Side.BUY, OrderType.LIMIT, 20.0, 1540.00, OrderStatus.FILLED, LocalDateTime.of(2023, 7, 13, 12, 51, 20)));
        orderRepository.save(new Order(null, emiliano, metr, Side.BUY, OrderType.MARKET, 500.0, 250.00, OrderStatus.FILLED, LocalDateTime.of(2023, 7, 13, 14, 11, 20)));
        orderRepository.save(new Order(null, emiliano, bma, Side.SELL, OrderType.MARKET, 30.0, 1530.00, OrderStatus.FILLED, LocalDateTime.of(2023, 7, 13, 15, 13, 20)));

        mockMvc.perform(get("/api/v1/portfolio").header("X-User-Id", emiliano.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber", is("10001")))
                .andExpect(jsonPath("$.availableCash", is(753000.00)))
                .andExpect(jsonPath("$.totalValue", is(904784.00)))
                .andExpect(jsonPath("$.positions", hasSize(2)))
                .andExpect(jsonPath("$.positions[*].ticker", containsInAnyOrder("PAMP", "METR")));
    }

    @Test
    void shouldSearchInstrumentsByTickerAndNameCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/v1/instruments").param("query", "pamp").param("filterBy", "ticker"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ticker", is("PAMP")));

        mockMvc.perform(get("/api/v1/instruments").param("query", "galicia").param("filterBy", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ticker", is("GGAL")));
    }

    @Test
    void shouldGetLatestMarketDataForInstrument() throws Exception {
        mockMvc.perform(get("/api/v1/instruments/" + pamp.getId() + "/marketdata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker", is("PAMP")))
                .andExpect(jsonPath("$.close", is(925.85)))
                .andExpect(jsonPath("$.previousClose", is(921.80)))
                .andExpect(jsonPath("$.date", is("2023-07-14")));
    }

    @Test
    void shouldExecuteMarketBuyByAmountWithoutFractionalSharesAndUpdatePortfolio() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .header("X-User-Id", emiliano.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "instrumentId": %d,
                                  "side": "CASH_IN",
                                  "size": 5000
                                }
                                """.formatted(ars.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("FILLED")));

        mockMvc.perform(post("/api/v1/orders")
                        .header("X-User-Id", emiliano.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "instrumentId": %d,
                                  "side": "BUY",
                                  "type": "MARKET",
                                  "amount": 5000
                                }
                                """.formatted(pamp.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("FILLED")))
                .andExpect(jsonPath("$.success", is(true)));

        mockMvc.perform(get("/api/v1/portfolio").header("X-User-Id", emiliano.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCash", is(370.75)))
                .andExpect(jsonPath("$.totalValue", is(5000.00)))
                .andExpect(jsonPath("$.positions[0].quantity", is(5)))
                .andExpect(jsonPath("$.positions[0].totalValue", is(4629.25)));
    }

    @Test
    void shouldExecuteMarketSellAndUpdateCashAndShares() throws Exception {
        orderRepository.save(new Order(null, emiliano, ars, Side.CASH_IN, OrderType.MARKET, 20000.0, 1.0, OrderStatus.FILLED, LocalDateTime.now()));
        orderRepository.save(new Order(null, emiliano, pamp, Side.BUY, OrderType.MARKET, 10.0, 920.0, OrderStatus.FILLED, LocalDateTime.now()));

        mockMvc.perform(post("/api/v1/orders")
                        .header("X-User-Id", emiliano.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "instrumentId": %d,
                                  "side": "SELL",
                                  "type": "MARKET",
                                  "size": 4
                                }
                                """.formatted(pamp.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("FILLED")))
                .andExpect(jsonPath("$.success", is(true)));

        mockMvc.perform(get("/api/v1/portfolio").header("X-User-Id", emiliano.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCash", is(14503.40)))
                .andExpect(jsonPath("$.positions[0].quantity", is(6)))
                .andExpect(jsonPath("$.positions[0].totalValue", is(5555.10)));
    }

    @Test
    void shouldRejectSellWhenInsufficientSharesAndPersistRejectedStatus() throws Exception {
        orderRepository.save(new Order(null, emiliano, pamp, Side.BUY, OrderType.MARKET, 5.0, 920.0, OrderStatus.FILLED, LocalDateTime.now()));

        mockMvc.perform(post("/api/v1/orders")
                        .header("X-User-Id", emiliano.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "instrumentId": %d,
                                  "side": "SELL",
                                  "type": "MARKET",
                                  "size": 10
                                }
                                """.formatted(pamp.getId())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is("REJECTED")))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorMessage", containsString("Insufficient quantity")));

        List<Order> orders = orderRepository.findByUserId(emiliano.getId());
        assertEquals(2, orders.size());
        assertEquals(1, orders.stream().filter(o -> o.getStatus() == OrderStatus.REJECTED).count());
    }

    @Test
    void shouldRejectBuyAndCashOutWhenInsufficientCash() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .header("X-User-Id", emiliano.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "instrumentId": %d,
                                  "side": "BUY",
                                  "type": "MARKET",
                                  "size": 10
                                }
                                """.formatted(pamp.getId())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is("REJECTED")))
                .andExpect(jsonPath("$.errorMessage", containsString("Insufficient cash balance")));

        mockMvc.perform(post("/api/v1/orders")
                        .header("X-User-Id", emiliano.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "instrumentId": %d,
                                  "side": "CASH_OUT",
                                  "size": 500
                                }
                                """.formatted(ars.getId())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is("REJECTED")))
                .andExpect(jsonPath("$.errorMessage", containsString("Insufficient cash balance for withdrawal")));
    }

    @Test
    void shouldCreateLimitOrderInNewStatusReserveFundsAndAllowCancellationOnlyWhenNew() throws Exception {
        MvcResult cashInResult = mockMvc.perform(post("/api/v1/orders")
                        .header("X-User-Id", emiliano.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "instrumentId": %d,
                                  "side": "CASH_IN",
                                  "size": 10000
                                }
                                """.formatted(ars.getId())))
                .andExpect(status().isCreated())
                .andReturn();

        long filledCashInId = objectMapper.readTree(cashInResult.getResponse().getContentAsString()).get("orderId").asLong();

        MvcResult limitResult = mockMvc.perform(post("/api/v1/orders")
                        .header("X-User-Id", emiliano.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "instrumentId": %d,
                                  "side": "BUY",
                                  "type": "LIMIT",
                                  "size": 10,
                                  "price": 900
                                }
                                """.formatted(pamp.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("NEW")))
                .andExpect(jsonPath("$.success", is(true)))
                .andReturn();

        long limitOrderId = objectMapper.readTree(limitResult.getResponse().getContentAsString()).get("orderId").asLong();

        mockMvc.perform(post("/api/v1/orders")
                        .header("X-User-Id", emiliano.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "instrumentId": %d,
                                  "side": "BUY",
                                  "type": "LIMIT",
                                  "size": 2,
                                  "price": 1000
                                }
                                """.formatted(pamp.getId())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is("REJECTED")));

        mockMvc.perform(delete("/api/v1/orders/" + limitOrderId)
                        .header("X-User-Id", emiliano.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")))
                .andExpect(jsonPath("$.success", is(true)));

        mockMvc.perform(delete("/api/v1/orders/" + filledCashInId)
                        .header("X-User-Id", emiliano.getId()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorMessage", containsString("Only orders with NEW status can be cancelled")));
    }
}
