package ru.reshetoff.notelistbackend.domain.service;

import ru.reshetoff.notelistbackend.domain.entity.Country;

import java.util.List;

public interface CountryService {
    List<Country> getAllCountries();
}
