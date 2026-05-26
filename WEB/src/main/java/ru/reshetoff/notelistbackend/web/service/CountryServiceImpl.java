package ru.reshetoff.notelistbackend.web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.reshetoff.notelistbackend.domain.entity.Country;
import ru.reshetoff.notelistbackend.domain.repository.CountryRepository;
import ru.reshetoff.notelistbackend.domain.service.CountryService;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    @Override
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }
}
