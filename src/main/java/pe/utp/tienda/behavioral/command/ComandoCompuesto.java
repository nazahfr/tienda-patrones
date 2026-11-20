package pe.utp.tienda.behavioral.command;

import java.util.ArrayList;
import java.util.List;

/** Macro-comando: agrupa varios comandos como una sola operacion (por ejemplo, una guia de ingreso con varias prendas). */
public class ComandoCompuesto implements Comando {
    private final String nombre;
    private final List<Comando> hijos;

    public ComandoCompuesto(String nombre, List<Comando> hijos) {
        this.nombre = nombre;
        this.hijos = new ArrayList<>(hijos);
    }

    @Override
    public void ejecutar() {
        List<Comando> hechos = new ArrayList<>();
        try {
            for (Comando c : hijos) { c.ejecutar(); hechos.add(c); }
        } catch (RuntimeException e) {                      // atomicidad: si uno falla, se revierten los anteriores
            for (int i = hechos.size() - 1; i >= 0; i--) hechos.get(i).deshacer();
            throw e;
        }
    }

    @Override
    public void deshacer() {
        for (int i = hijos.size() - 1; i >= 0; i--) hijos.get(i).deshacer();
    }

    @Override public String descripcion() { return nombre + " (" + hijos.size() + " operaciones)"; }
}
