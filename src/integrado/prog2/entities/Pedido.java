package integrado.prog2.entities;

import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.exception.StockInvalidoException;
import integrado.prog2.exception.ValidacionException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pedido extends Base implements Calculable {

    private LocalDate fecha;
    private Estado estado;
    private double total;
    private FormaPago formaPago;
    private Usuario usuario;
    private final List<DetallePedido> detalles;

    private static long correlativoDetalle = 1;

    public Pedido(Long id, Usuario usuario, FormaPago formaPago) {
        super(id);
        setUsuario(usuario);
        this.fecha = LocalDate.now();
        this.estado = Estado.PENDIENTE;
        this.formaPago = (formaPago == null) ? FormaPago.EFECTIVO : formaPago;
        this.total = 0.0;
        this.detalles = new ArrayList<>();
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        if (formaPago == null) {
            throw new IllegalArgumentException("La forma de pago no puede ser nula.");
        }
        this.formaPago = formaPago;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El pedido debe tener un usuario asociado.");
        }
        this.usuario = usuario;
    }

    public List<DetallePedido> getDetalles() {
        return new ArrayList<>(detalles);
    }


    public void addDetallePedido(int cantidad, Double precioUnitario, Producto producto)
            throws StockInvalidoException, ValidacionException {

        if (producto == null) {
            throw new ValidacionException("El producto del detalle no puede ser nulo.");
        }
        if (cantidad <= 0) {
            throw new ValidacionException("La cantidad debe ser mayor a 0.");
        }
        if (cantidad > producto.getStock()) {
            throw new StockInvalidoException("Stock insuficiente para '" + producto.getNombre() +
                    "'. Disponible: " + producto.getStock() + ", solicitado: " + cantidad);
        }

        producto.descontarStock(cantidad);

        double precio = (precioUnitario != null) ? precioUnitario : producto.getPrecio();
        double subtotal = cantidad * precio;

        DetallePedido detalle = new DetallePedido(correlativoDetalle++, cantidad, subtotal, producto);
        detalles.add(detalle);

        calcularTotal();
    }

    public DetallePedido findeDetallePedidoByProducto(Producto producto) {
        if (producto == null) return null;

        for (DetallePedido d : detalles) {
            if (d.getProducto().equals(producto)) {
                return d;
            }
        }
        return null;
    }

    public void deleteDetallePedidoByProducto(Producto producto) {
        DetallePedido detalle = findeDetallePedidoByProducto(producto);
        if (detalle != null) {
            producto.setStock(producto.getStock() + detalle.getCantidad());
            detalles.remove(detalle);
            calcularTotal();
        }
    }

    @Override
    public void calcularTotal() {
        this.total = 0.0;
        for (DetallePedido detalle : detalles) {
            this.total += detalle.getSubtotal();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pedido)) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(getId(), pedido.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + getId() +
                ", usuario=" + (usuario != null ? usuario.getNombre() + " " + usuario.getApellido() : "N/A") +
                ", fecha=" + fecha +
                ", estado=" + estado +
                ", formaPago=" + formaPago +
                ", total=" + total +
                ", cantDetalles=" + detalles.size() +
                ", eliminado=" + isEliminado() +
                '}';
    }
}
