package pe.utp.tienda.structural.decorator;

import pe.utp.tienda.creational.singleton.ConfiguracionApp;

public class ConIgv extends DecoradorPrecio {
    public ConIgv(CalculoPrecio envuelto) { super(envuelto); }

    @Override public double calcular() {
        return super.calcular() * (1 + ConfiguracionApp.getInstancia().getIgv());
    }
    @Override public String detalle() {
        return super.detalle() + String.format(" -> IGV %.0f%%", ConfiguracionApp.getInstancia().getIgv() * 100);
    }
}
