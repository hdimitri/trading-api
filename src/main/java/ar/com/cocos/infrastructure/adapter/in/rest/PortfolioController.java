package ar.com.cocos.infrastructure.adapter.in.rest;

import ar.com.cocos.application.port.in.GetUserPortfolioUseCase;
import ar.com.cocos.application.port.in.result.PortfolioResult;
import ar.com.cocos.application.port.in.result.PositionResult;
import ar.com.cocos.infrastructure.adapter.in.rest.dto.response.PortfolioResponseDto;
import ar.com.cocos.infrastructure.adapter.in.rest.dto.response.PositionResponseDto;
import ar.com.cocos.infrastructure.adapter.in.rest.validator.RequestValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {

    private final GetUserPortfolioUseCase getUserPortfolioUseCase;

    public PortfolioController(GetUserPortfolioUseCase getUserPortfolioUseCase) {
        this.getUserPortfolioUseCase = getUserPortfolioUseCase;
    }

    @GetMapping
    public ResponseEntity<PortfolioResponseDto> getPortfolio(
            @RequestHeader("X-User-Id") Long userId) {
        RequestValidator.validatePositiveId(userId, "X-User-Id");

        PortfolioResult result = getUserPortfolioUseCase.execute(userId);
        return ResponseEntity.ok(toResponseDto(result));
    }

    private PortfolioResponseDto toResponseDto(PortfolioResult result) {
        List<PositionResponseDto> positions = result.getPositions().stream()
                .map(this::toPositionDto)
                .toList();

        return new PortfolioResponseDto(
                result.getAccountNumber(),
                result.getTotalValue(),
                result.getAvailableCash(),
                positions
        );
    }

    private PositionResponseDto toPositionDto(PositionResult pos) {
        return new PositionResponseDto(
                pos.getTicker(),
                pos.getName(),
                pos.getQuantity(),
                pos.getTotalValue(),
                pos.getTotalReturn()
        );
    }
}
