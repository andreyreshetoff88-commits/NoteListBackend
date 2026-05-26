package ru.reshetoff.notelistbackend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "countries")
@Getter
@Setter
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "country_code", nullable = false, unique = true)
    private String countryCode;

    @Column(name = "phone_code", nullable = false)
    private String phoneCode;

    @Column(name = "phone_mask", nullable = false)
    private String phoneMask;

    @Column(name = "flag_url", nullable = false)
    private String flagUrl;
}
