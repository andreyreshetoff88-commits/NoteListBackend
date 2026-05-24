package ru.reshetoff.notelistbackend.persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.reshetoff.notelistbackend.domain.entity.User;
import ru.reshetoff.notelistbackend.domain.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final EntityManager em;

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            em.persist(user);
            return user;
        } else {
            return em.merge(user);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String jpql = "SELECT u FROM User u WHERE u.email = :email";
        TypedQuery<User> query = em.createQuery(jpql, User.class);
        query.setParameter("email", email);
        List<User> users = query.getResultList();
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        String jpql = "SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber";
        TypedQuery<User> query = em.createQuery(jpql, User.class);
        query.setParameter("phoneNumber", phoneNumber);
        List<User> users = query.getResultList();
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public Optional<User> findByRefreshToken(String refreshToken) {
        String jpql = "SELECT u FROM User u WHERE u.refreshToken = :refreshToken";
        TypedQuery<User> query = em.createQuery(jpql, User.class);
        query.setParameter("refreshToken", refreshToken);
        List<User> users = query.getResultList();
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public boolean existsByEmail(String email) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.email = :email";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("email", email);
        Long count = query.getSingleResult();
        return count > 0;
    }
}
