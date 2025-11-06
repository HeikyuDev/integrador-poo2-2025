package com.gpp.servisoft.model.enums;

public enum Periodicidad {
    MENSUAL(30),
    BIMESTRAL(60),
    TRIMESTRAL(90),
    ANUAL(365);

    private final int dias;

    Periodicidad(int dias) {
        this.dias = dias;
    }

    public int getDias() {
        return dias;
    }
}
