package ar.com.cocos.infrastructure.adapter.in.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponseDto {

    private Long orderId;
    private String status;
    private boolean success;
    private String errorMessage;

    public OrderResponseDto() {
    }

    public OrderResponseDto(Long orderId, String status, boolean success, String errorMessage) {
        this.orderId = orderId;
        this.status = status;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
