package com.enviacv.exception;

public class ArmazenamentoException extends RuntimeException {

    public ArmazenamentoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}