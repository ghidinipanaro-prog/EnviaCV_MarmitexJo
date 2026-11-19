package com.enviacv.dto;

import java.time.LocalDateTime;

public record CandidaturaResponse(Long id, String mensagem, LocalDateTime dataEnvio) {
}