package integrado.prog2.service;

import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Producto;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.StockInvalidoException;
import integrado.prog2.exception.ValidacionException;

import java.util.ArrayList;
import java.util.List;

public class ProductoService {

    private final List<Producto> productos = new ArrayList<>();
    private long correlativoId = 1;

    public Producto crear(String nombre, String descripcion, double precio, int stock,
                           String imagen, boolean disponible, Categoria categoria)
            throws ValidacionException, StockInvalidoException {

        if (nombre == null || nombre.isBlank()) {
            throw new ValidacionException("El nombre del producto no puede estar vacio.");
        }
        if (categoria == null || categoria.isEliminado()) {
            throw new ValidacionException("La categoria seleccionada no existe o esta eliminada.");
        }
        if (precio < 0) {
            throw new StockInvalidoException("El precio no puede ser negativo.");
        }
        if (stock < 0) {
            throw new StockInvalidoException("El stock no puede ser negativo.");
        }

        Producto producto = new Producto(correlativoId++, nombre, precio, descripcion,
                stock, imagen, disponible, categoria);
        productos.add(producto);
        return producto;
    }

    public List<Producto> listarActivos() {
        List<Producto> activos = new ArrayList<>();
        for (Producto p : productos) {
            if (!p.isEliminado()) {
                activos.add(p);
            }
        }
        return activos;
    }

    public List<Producto> listarPorCategoria(Categoria categoria) {
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : productos) {
            if (!p.isEliminado() && p.getCategoria().equals(categoria)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public Producto buscarPorId(Long id) throws EntidadNoEncontradaException {
        for (Producto p : productos) {
            if (p.getId().equals(id) && !p.isEliminado()) {
                return p;
            }
        }
        throw new EntidadNoEncontradaException(
                "No se encontro un producto activo con id " + id);
    }

    public void editar(Long id, String nombre, String descripcion, Double precio,
                        Integer stock, String imagen, Boolean disponible, Categoria categoria)
            throws EntidadNoEncontradaException, StockInvalidoException, ValidacionException {

        Producto producto = buscarPorId(id);

        if (nombre != null && !nombre.isBlank()) {
            producto.setNombre(nombre);
        }
        if (descripcion != null) {
            producto.setDescripcion(descripcion);
        }
        if (precio != null) {
            if (precio < 0) throw new StockInvalidoException("El precio no puede ser negativo.");
            producto.setPrecio(precio);
        }
        if (stock != null) {
            if (stock < 0) throw new StockInvalidoException("El stock no puede ser negativo.");
            producto.setStock(stock);
        }
        if (imagen != null) {
            producto.setImagen(imagen);
        }
        if (disponible != null) {
            producto.setDisponible(disponible);
        }
        if (categoria != null) {
            if (categoria.isEliminado()) {
                throw new ValidacionException("La categoria seleccionada esta eliminada.");
            }
            producto.setCategoria(categoria);
        }
    }

    public void eliminar(Long id) throws EntidadNoEncontradaException {
        Producto producto = buscarPorId(id);
        producto.setEliminado(true);
    }
}
