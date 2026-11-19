package com.enviacv.exception;

public class CandidaturaNaoEncontradaException extends RuntimeException {

    public CandidaturaNaoEncontradaException(Long id) {
        super("Candidatura " + id + " não encontrada.");
    }
}