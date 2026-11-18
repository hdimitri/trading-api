package ar.com.cocos.application.port.in.result;

public class OrderResult {

    private final Long orderId;
    private final String status;
    private final boolean success;
    private final String errorMessage;

    public OrderResult(Long orderId, String status, boolean success, String errorMessage) {
        this.orderId = orderId;
        this.status = status;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static OrderResult rejected(Long orderId, String errorMessage) {
        return new OrderResult(orderId, "REJECTED", false, errorMessage);
    }

    public static OrderResult failure(String errorMessage) {
        return new OrderResult(null, null, false, errorMessage);
    }

    public static OrderResult success(Long orderId, String status) {
        return new OrderResult(orderId, status, true, null);
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
