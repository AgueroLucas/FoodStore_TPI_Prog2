package integrado.prog2.service;

import integrado.prog2.entities.Categoria;
import integrado.prog2.exception.DatoDuplicadoException;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.ValidacionException;

import java.util.ArrayList;
import java.util.List;

public class CategoriaService {

    private final List<Categoria> categorias = new ArrayList<>();
    private long correlativoId = 1;

    public Categoria crear(String nombre, String descripcion)
            throws ValidacionException, DatoDuplicadoException {

        if (nombre == null || nombre.isBlank()) {
            throw new ValidacionException("El nombre de la categoria no puede estar vacio.");
        }

        // Validación con for-each
        for (Categoria c : categorias) {
            if (!c.isEliminado() && c.getNombre().equalsIgnoreCase(nombre.trim())) {
                throw new DatoDuplicadoException("Ya existe una categoria con el nombre '" + nombre + "'.");
            }
        }

        Categoria categoria = new Categoria(correlativoId++, nombre, descripcion);
        categorias.add(categoria);
        return categoria;
    }

    public List<Categoria> listarActivas() {
        List<Categoria> activas = new ArrayList<>();
        for (Categoria c : categorias) {
            if (!c.isEliminado()) {
                activas.add(c);
            }
        }
        return activas;
    }

    public Categoria buscarPorId(Long id) throws EntidadNoEncontradaException {
        // Búsqueda con for-each
        for (Categoria c : categorias) {
            if (c.getId().equals(id) && !c.isEliminado()) {
                return c;
            }
        }
        throw new EntidadNoEncontradaException("No se encontro una categoria activa con id " + id);
    }

    public void editar(Long id, String nuevoNombre, String nuevaDescripcion)
            throws EntidadNoEncontradaException, DatoDuplicadoException, ValidacionException {

        Categoria categoria = buscarPorId(id);

        if (nuevoNombre != null && !nuevoNombre.isBlank()) {
            for (Categoria c : categorias) {
                if (!c.isEliminado() && !c.getId().equals(id) &&
                        c.getNombre().equalsIgnoreCase(nuevoNombre.trim())) {
                    throw new DatoDuplicadoException("Ya existe otra categoria con el nombre '" + nuevoNombre + "'.");
                }
            }
            categoria.setNombre(nuevoNombre);
        }
        if (nuevaDescripcion != null) {
            categoria.setDescripcion(nuevaDescripcion);
        }
    }

    public void eliminar(Long id, boolean tieneProductosAsociados) throws EntidadNoEncontradaException, ValidacionException {
        Categoria categoria = buscarPorId(id);
        if (tieneProductosAsociados) {
            throw new ValidacionException(
                    "No se puede eliminar la categoria '" + categoria.getNombre() +
                            "' porque tiene productos asociados.");
        }
        categoria.setEliminado(true);
    }
}
