package com.enviacv.dto;

import java.time.LocalDateTime;

import com.enviacv.model.Candidatura;

public record CandidaturaDetalheResponse(
        Long id,
        String nomeCompleto,
        String email,
        String telefone,
        String vagaPretendida,
        String linkedin,
        String apresentacao,
        String curriculoNome,
        LocalDateTime dataEnvio) {

    public static CandidaturaDetalheResponse de(Candidatura c) {
        return new CandidaturaDetalheResponse(
                c.getId(),
                c.getNomeCompleto(),
                c.getEmail(),
                c.getTelefone(),
                c.getVagaPretendida(),
                c.getLinkedin(),
                c.getApresentacao(),
                c.getCurriculoNome(),
                c.getDataEnvio());
    }
}