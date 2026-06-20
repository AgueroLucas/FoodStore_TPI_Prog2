package integrado.prog2.service;

import integrado.prog2.entities.DetallePedido;
import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.ValidacionException;

import java.util.ArrayList;
import java.util.List;

public class PedidoService {

    private final List<Pedido> pedidos = new ArrayList<>();
    private long correlativoId = 1;

    public Pedido iniciarPedido(Usuario usuario, FormaPago formaPago) throws ValidacionException {
        if (usuario == null || usuario.isEliminado()) {
            throw new ValidacionException("No se puede crear un pedido sin un usuario valido.");
        }
        return new Pedido(correlativoId, usuario, formaPago);
    }

    public void confirmar(Pedido pedido) {
        pedido.calcularTotal();
        pedido.setId(correlativoId++);
        pedidos.add(pedido);
    }

    public void descartar(Pedido pedido) {
        if (pedido == null) return;
        for (DetallePedido detalle : pedido.getDetalles()) {
            detalle.getProducto().setStock(detalle.getProducto().getStock() + detalle.getCantidad());
        }
    }

    public List<Pedido> listarActivos() {
        List<Pedido> activos = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (!p.isEliminado()) {
                activos.add(p);
            }
        }
        return activos;
    }

    public List<Pedido> listarPorUsuario(Usuario usuario) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (!p.isEliminado() && p.getUsuario().equals(usuario)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public Pedido buscarPorId(Long id) throws EntidadNoEncontradaException {
        for (Pedido p : pedidos) {
            if (p.getId().equals(id) && !p.isEliminado()) {
                return p;
            }
        }
        throw new EntidadNoEncontradaException(
                "No se encontro un pedido activo con id " + id);
    }

    public void actualizarEstadoYFormaPago(Long id, Estado nuevoEstado, FormaPago nuevaFormaPago)
            throws EntidadNoEncontradaException {
        Pedido pedido = buscarPorId(id);
        if (nuevoEstado != null) {
            pedido.setEstado(nuevoEstado);
        }
        if (nuevaFormaPago != null) {
            pedido.setFormaPago(nuevaFormaPago);
        }
    }

    public void eliminar(Long id) throws EntidadNoEncontradaException {
        Pedido pedido = buscarPorId(id);
        for (DetallePedido detalle : pedido.getDetalles()) {
            detalle.getProducto().aumentarStock(detalle.getCantidad());
        }
        pedido.setEliminado(true);
    }
}
