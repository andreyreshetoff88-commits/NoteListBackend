package ru.reshetoff.notelistbackend.persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.reshetoff.notelistbackend.domain.entity.VerificationToken;
import ru.reshetoff.notelistbackend.domain.repository.VerificationTokenRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VerificationTokenRepositoryImpl implements VerificationTokenRepository {
    private final EntityManager em;

    @Override
    public VerificationToken save(VerificationToken token) {
        if (token.getId() == null) {
            em.persist(token);
            return token;
        } else {
            return em.merge(token);
        }
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        String jpql = "SELECT v FROM VerificationToken v WHERE v.token = :token";
        TypedQuery<VerificationToken> query = em.createQuery(jpql, VerificationToken.class);
        query.setParameter("token", token);
        List<VerificationToken> tokens = query.getResultList();
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
    }

    @Override
    public void delete(VerificationToken token) {
        em.remove(em.contains(token) ? token : em.merge(token));
    }
}
