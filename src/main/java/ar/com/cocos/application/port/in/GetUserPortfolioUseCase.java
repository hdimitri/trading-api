package ar.com.cocos.application.port.in;

import ar.com.cocos.application.port.in.result.PortfolioResult;

public interface GetUserPortfolioUseCase {
    PortfolioResult execute(Long userId);
}
