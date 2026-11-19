package com.enviacv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enviacv.model.Candidatura;

public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {

    List<Candidatura> findAllByOrderByDataEnvioDesc();
}