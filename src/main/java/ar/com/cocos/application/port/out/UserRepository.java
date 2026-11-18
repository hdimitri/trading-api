package ar.com.cocos.application.port.out;

import ar.com.cocos.domain.model.*;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByAccountNumber(String accountNumber);
    User save(User user);
}