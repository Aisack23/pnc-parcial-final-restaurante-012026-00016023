package com.uca.pncparcialfinalrestaurante.exception;

public class SesionInactivaException extends RuntimeException {
    public SesionInactivaException(String mensaje) { super(mensaje); }
}