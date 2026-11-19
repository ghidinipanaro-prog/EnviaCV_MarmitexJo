package com.enviacv.exception;

public class EmailFalhouException extends RuntimeException {

    public EmailFalhouException(String mensagem) {
        super(mensagem);
    }

    public EmailFalhouException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}