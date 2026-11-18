package ar.com.cocos.infrastructure.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderRequestDto {

    @NotNull(message = "instrumentId is required")
    @Positive(message = "instrumentId must be a positive number")
    private Long instrumentId;

    @NotBlank(message = "side is required")
    private String side;

    private String type;

    @Positive(message = "size must be greater than zero")
    private Double size;

    @Positive(message = "amount must be greater than zero")
    private Double amount;

    @Positive(message = "price must be greater than zero")
    private Double price;

    public OrderRequestDto() {
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Long instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
