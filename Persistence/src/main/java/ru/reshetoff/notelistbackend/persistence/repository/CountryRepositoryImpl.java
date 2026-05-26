package ru.reshetoff.notelistbackend.persistence.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.reshetoff.notelistbackend.domain.entity.Country;
import ru.reshetoff.notelistbackend.domain.repository.CountryRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CountryRepositoryImpl implements CountryRepository {
    private final EntityManager em;

    @Override
    public List<Country> findAll() {
        return em.createQuery("SELECT c FROM Country c", Country.class).getResultList();
    }
}
