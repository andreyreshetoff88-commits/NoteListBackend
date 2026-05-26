package ru.reshetoff.notelistbackend.domain.repository;

import ru.reshetoff.notelistbackend.domain.entity.Country;

import java.util.List;

public interface CountryRepository {
    List<Country> findAll();
}
