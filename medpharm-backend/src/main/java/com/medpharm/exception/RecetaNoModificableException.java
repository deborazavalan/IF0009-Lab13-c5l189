package com.medpharm.exception;

public class RecetaNoModificableException extends RuntimeException {
    public RecetaNoModificableException(String mensaje) {
        super(mensaje);
    }
}
