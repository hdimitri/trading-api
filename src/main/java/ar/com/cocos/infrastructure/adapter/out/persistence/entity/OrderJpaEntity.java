package ar.com.cocos.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrumentid", columnDefinition = "int4")
    private InstrumentJpaEntity instrument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", columnDefinition = "int4")
    private UserJpaEntity user;

    @Column(name = "\"size\"")
    private Double size;

    @Column(name = "price")
    private Double price;

    @Column(name = "\"type\"", length = 10)
    private String type;

    @Column(name = "side", length = 10)
    private String side;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "datetime")
    private LocalDateTime datetime;

    public OrderJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InstrumentJpaEntity getInstrument() {
        return instrument;
    }

    public void setInstrument(InstrumentJpaEntity instrument) {
        this.instrument = instrument;
    }

    public UserJpaEntity getUser() {
        return user;
    }

    public void setUser(UserJpaEntity user) {
        this.user = user;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }
}
