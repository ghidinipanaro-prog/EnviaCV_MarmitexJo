package com.enviacv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enviacv.model.FilaEmail;

public interface FilaEmailRepository extends JpaRepository<FilaEmail, Long> {

    List<FilaEmail> findByStatusOrderByCriadoEmAsc(FilaEmail.Status status);
}