package pe.utp.tienda.behavioral.command;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/** Invoker (GoF): ejecuta comandos, mantiene el historial para deshacer/rehacer, una cola de tareas y la bitacora de auditoria. */
public class InvocadorComandos {
    private final Deque<Comando> hechos = new ArrayDeque<>();
    private final Deque<Comando> deshechos = new ArrayDeque<>();
    private final Deque<Comando> cola = new ArrayDeque<>();
    private final List<String> bitacora = new ArrayList<>();

    public void ejecutar(Comando c) {
        try {
            c.ejecutar();
        } catch (RuntimeException e) {
            bitacora.add("FALLIDO   | " + c.descripcion() + " -> " + e.getMessage());
            throw e;
        }
        hechos.push(c);
        deshechos.clear();                                  // una accion nueva invalida el "rehacer"
        bitacora.add("EJECUTADO | " + c.descripcion());
    }

    public boolean deshacer() {
        if (hechos.isEmpty()) return false;
        Comando c = hechos.pop();
        c.deshacer();
        deshechos.push(c);
        bitacora.add("DESHECHO  | " + c.descripcion());
        return true;
    }

    public boolean rehacer() {
        if (deshechos.isEmpty()) return false;
        Comando c = deshechos.pop();
        c.ejecutar();
        hechos.push(c);
        bitacora.add("REHECHO   | " + c.descripcion());
        return true;
    }

    /** Cola de tareas: se encola ahora y se procesa despues (por ejemplo, al cierre de caja). */
    public void encolar(Comando c) {
        cola.add(c);
        bitacora.add("ENCOLADO  | " + c.descripcion());
    }

    public int procesarCola() {
        int n = 0;
        while (!cola.isEmpty()) { ejecutar(cola.poll()); n++; }
        return n;
    }

    public int pendientesEnCola() { return cola.size(); }
    public int cantidadDeshacer() { return hechos.size(); }
    public List<String> getBitacora() { return Collections.unmodifiableList(bitacora); }
}
