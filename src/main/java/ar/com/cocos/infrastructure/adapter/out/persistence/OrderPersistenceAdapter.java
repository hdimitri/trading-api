package ar.com.cocos.infrastructure.adapter.out.persistence;

import ar.com.cocos.application.port.out.OrderRepository;
import ar.com.cocos.domain.enums.InstrumentType;
import ar.com.cocos.domain.enums.OrderStatus;
import ar.com.cocos.domain.enums.OrderType;
import ar.com.cocos.domain.enums.Side;
import ar.com.cocos.domain.model.Instrument;
import ar.com.cocos.domain.model.Order;
import ar.com.cocos.domain.model.User;
import ar.com.cocos.infrastructure.adapter.out.persistence.entity.InstrumentJpaEntity;
import ar.com.cocos.infrastructure.adapter.out.persistence.entity.OrderJpaEntity;
import ar.com.cocos.infrastructure.adapter.out.persistence.repository.SpringDataInstrumentRepository;
import ar.com.cocos.infrastructure.adapter.out.persistence.repository.SpringDataOrderRepository;
import ar.com.cocos.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OrderPersistenceAdapter implements OrderRepository {

    private final SpringDataOrderRepository jpaRepository;
    private final SpringDataUserRepository userJpaRepository;
    private final SpringDataInstrumentRepository instrumentJpaRepository;

    public OrderPersistenceAdapter(SpringDataOrderRepository jpaRepository,
                                   SpringDataUserRepository userJpaRepository,
                                   SpringDataInstrumentRepository instrumentJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.instrumentJpaRepository = instrumentJpaRepository;
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return jpaRepository.findByStatus(status.name()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByInstrumentIdAndStatus(Long instrumentId, OrderStatus status) {
        return jpaRepository.findByInstrumentIdAndStatus(instrumentId, status.name()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(order.getId());

        if (order.getUser() != null && order.getUser().getId() != null) {
            entity.setUser(userJpaRepository.findById(order.getUser().getId()).orElse(null));
        }
        if (order.getInstrument() != null && order.getInstrument().getId() != null) {
            entity.setInstrument(instrumentJpaRepository.findById(order.getInstrument().getId()).orElse(null));
        }

        entity.setSide(order.getSide() != null ? order.getSide().name() : null);
        entity.setType(order.getType() != null ? order.getType().name() : null);
        entity.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        entity.setSize(order.getSize());
        entity.setPrice(order.getPrice());
        entity.setDatetime(order.getDateTime());

        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public void delete(Order order) {
        if (order != null && order.getId() != null) {
            jpaRepository.deleteById(order.getId());
        }
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    private Order toDomain(OrderJpaEntity e) {
        User user = e.getUser() != null
                ? new User(
                        e.getUser().getId(),
                        e.getUser().getEmail(),
                        e.getUser().getAccountNumber(),
                        Collections.emptyList(),
                        Collections.emptyList()
                )
                : null;

        Instrument instrument = null;
        if (e.getInstrument() != null) {
            InstrumentJpaEntity ie = e.getInstrument();
            InstrumentType type = ie.getType() != null
                    ? InstrumentType.valueOf(ie.getType().trim().toUpperCase())
                    : null;
            instrument = new Instrument(ie.getId(), ie.getTicker(), ie.getName(), type);
        }

        Side side = e.getSide() != null ? Side.valueOf(e.getSide().trim().toUpperCase()) : null;
        OrderType type = e.getType() != null ? OrderType.valueOf(e.getType().trim().toUpperCase()) : null;
        OrderStatus status = e.getStatus() != null ? OrderStatus.valueOf(e.getStatus().trim().toUpperCase()) : null;
        double size = e.getSize() != null ? e.getSize() : 0.0;
        double price = e.getPrice() != null ? e.getPrice() : 0.0;

        return new Order(e.getId(), user, instrument, side, type, size, price, status, e.getDatetime());
    }
}
