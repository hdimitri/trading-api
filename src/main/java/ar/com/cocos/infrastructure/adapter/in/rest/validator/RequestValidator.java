package ar.com.cocos.infrastructure.adapter.in.rest.validator;

public final class RequestValidator {

    private RequestValidator() {
    }

    public static void validatePositiveId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(fieldName + " must be a positive number");
        }
    }
}
