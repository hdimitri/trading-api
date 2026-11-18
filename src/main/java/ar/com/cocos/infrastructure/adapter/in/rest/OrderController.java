package ar.com.cocos.infrastructure.adapter.in.rest;

import ar.com.cocos.application.port.in.command.ExecuteOrderCommand;
import ar.com.cocos.application.port.in.ExecuteOrderUseCase;
import ar.com.cocos.application.port.in.result.OrderResult;
import ar.com.cocos.infrastructure.adapter.in.rest.dto.request.OrderRequestDto;
import ar.com.cocos.infrastructure.adapter.in.rest.dto.response.OrderResponseDto;
import ar.com.cocos.infrastructure.adapter.in.rest.validator.RequestValidator;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final ExecuteOrderUseCase executeOrderUseCase;

    public OrderController(ExecuteOrderUseCase executeOrderUseCase) {
        this.executeOrderUseCase = executeOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> execute(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody OrderRequestDto requestDto) {
        RequestValidator.validatePositiveId(userId, "X-User-Id");

        ExecuteOrderCommand command = toCommand(userId, requestDto);
        OrderResult result = executeOrderUseCase.execute(command);

        return toResponseEntity(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> cancel(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId) {
        RequestValidator.validatePositiveId(userId, "X-User-Id");
        RequestValidator.validatePositiveId(orderId, "orderId");

        OrderResult result = executeOrderUseCase.cancel(orderId, userId);

        return toResponseEntity(result, HttpStatus.OK);
    }

    private ExecuteOrderCommand toCommand(Long userId, OrderRequestDto dto) {
        return new ExecuteOrderCommand(
                userId,
                dto.getInstrumentId(),
                dto.getSide(),
                dto.getType(),
                dto.getSize(),
                dto.getAmount(),
                dto.getPrice()
        );
    }

    private ResponseEntity<OrderResponseDto> toResponseEntity(OrderResult result, HttpStatus successStatus) {
        OrderResponseDto responseDto = new OrderResponseDto(
                result.getOrderId(),
                result.getStatus(),
                result.isSuccess(),
                result.getErrorMessage()
        );
        return result.isSuccess()
                ? ResponseEntity.status(successStatus).body(responseDto)
                : ResponseEntity.unprocessableEntity().body(responseDto);
    }
}
