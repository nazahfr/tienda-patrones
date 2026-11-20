package pe.utp.tienda.persistence;

import java.util.ArrayList;
import java.util.List;
import pe.utp.tienda.model.Pedido;

public class PedidoDAOMemoria implements PedidoDAO {
    private final List<Pedido> datos = new ArrayList<>();
    private int siguienteId = 1;

    @Override
    public void guardar(Pedido pedido) {
        if (pedido.getId() == 0) {
            pedido.setId(siguienteId++);
            datos.add(pedido);
        }
    }

    @Override public List<Pedido> listar() { return new ArrayList<>(datos); }
}
