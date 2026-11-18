package ar.com.cocos.infrastructure.adapter.in.rest.dto.response;

public class InstrumentResponseDto {

    private Long id;
    private String ticker;
    private String name;
    private String type;

    public InstrumentResponseDto() {
    }

    public InstrumentResponseDto(Long id, String ticker, String name, String type) {
        this.id = id;
        this.ticker = ticker;
        this.name = name;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
