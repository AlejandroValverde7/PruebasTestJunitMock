package org.example.models;

import java.math.BigDecimal;
import java.util.List;

public class Banco {
    private List<Cuenta> cuentas;
    private String nombre;

    public Banco(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public List<Cuenta> getCuentas() {return cuentas;}
    public void setCuentas(List<Cuenta> cuentas) {this.cuentas = cuentas;}

    public void addCuenta (Cuenta cuenta){
        cuentas.add(cuenta);
    }


    //    Metodo para pasar dinero de una cuenta a otra
    public void transferir(Cuenta origen, Cuenta destino, BigDecimal monto){
        origen.debito(monto);
        destino.credito(monto);
    }
}
