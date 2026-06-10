package ru.reshetoff.notelistbackend.persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.reshetoff.notelistbackend.domain.entity.User;
import ru.reshetoff.notelistbackend.domain.entity.VerificationToken;
import ru.reshetoff.notelistbackend.domain.repository.VerificationTokenRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VerificationTokenRepositoryImpl implements VerificationTokenRepository {
    private final EntityManager em;

    @Override
    public void save(VerificationToken token) {
        if (token.getId() == null) {
            em.persist(token);
        } else {
            em.merge(token);
        }
    }

    @Override
    public Optional<VerificationToken> findByCode(String code) {
        String jpql = "SELECT v FROM VerificationToken v WHERE v.code = :code";
        TypedQuery<VerificationToken> query = em.createQuery(jpql, VerificationToken.class);
        query.setParameter("code", code);
        List<VerificationToken> tokens = query.getResultList();
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
    }

    @Override
    public Optional<VerificationToken> findByUserEmail(String email) {
        String jpql = "SELECT v FROM VerificationToken v WHERE v.user.email = :email";
        TypedQuery<VerificationToken> query = em.createQuery(jpql, VerificationToken.class);
        query.setParameter("email", email);
        List<VerificationToken> tokens = query.getResultList();
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
    }

    @Override
    public void delete(VerificationToken token) {
        em.remove(em.contains(token) ? token : em.merge(token));
    }

    @Override
    public void deleteByUser(User user) {
        String jpql = "DELETE FROM VerificationToken v WHERE v.user = :user";
        em.createQuery(jpql).setParameter("user", user).executeUpdate();
    }
}
