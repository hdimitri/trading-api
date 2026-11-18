package ar.com.cocos.infrastructure.adapter.out.persistence.repository;

import ar.com.cocos.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByAccountNumber(String accountNumber);
}
