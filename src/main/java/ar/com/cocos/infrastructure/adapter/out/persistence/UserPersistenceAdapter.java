package ar.com.cocos.infrastructure.adapter.out.persistence;

import ar.com.cocos.application.port.out.UserRepository;
import ar.com.cocos.domain.model.User;
import ar.com.cocos.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import ar.com.cocos.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Optional;

@Repository
public class UserPersistenceAdapter implements UserRepository {

    private final SpringDataUserRepository jpaRepository;

    public UserPersistenceAdapter(SpringDataUserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByAccountNumber(String accountNumber) {
        return jpaRepository.findByAccountNumber(accountNumber).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = new UserJpaEntity(user.getId(), user.getEmail(), user.getAccountNumber());
        return toDomain(jpaRepository.save(entity));
    }

    private User toDomain(UserJpaEntity e) {
        return new User(
                e.getId(),
                e.getEmail(),
                e.getAccountNumber(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }
}
