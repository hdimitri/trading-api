package ar.com.cocos.application.port.in;

import ar.com.cocos.application.port.in.command.ExecuteOrderCommand;
import ar.com.cocos.application.port.in.result.OrderResult;

public interface ExecuteOrderUseCase {
    OrderResult execute(ExecuteOrderCommand command);
    OrderResult cancel(Long orderId, Long userId);
}
