package ar.com.cocos.application.port.in.command;

public class ExecuteOrderCommand {

    private final Long userId;
    private final Long instrumentId;
    private final String side;
    private final String type;
    private final Double size;
    private final Double amount;
    private final Double price;

    public ExecuteOrderCommand(Long userId, Long instrumentId, String side, String type,
                               Double size, Double amount, Double price) {
        this.userId = userId;
        this.instrumentId = instrumentId;
        this.side = side;
        this.type = type;
        this.size = size;
        this.amount = amount;
        this.price = price;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public String getSide() {
        return side;
    }

    public String getType() {
        return type;
    }

    public Double getSize() {
        return size;
    }

    public Double getAmount() {
        return amount;
    }

    public Double getPrice() {
        return price;
    }
}
