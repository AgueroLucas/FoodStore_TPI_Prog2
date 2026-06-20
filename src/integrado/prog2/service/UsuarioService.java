package integrado.prog2.service;

import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Rol;
import integrado.prog2.exception.DatoDuplicadoException;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.ValidacionException;

import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    private final List<Usuario> usuarios = new ArrayList<>();
    private long correlativoId = 1;

    public Usuario crear(String nombre, String apellido, String mail, String celular,
                          String contrasena, Rol rol) throws ValidacionException, DatoDuplicadoException {

        if (mail == null || mail.isBlank() || !mail.contains("@")) {
            throw new ValidacionException("El mail ingresado no es valido.");
        }
        for (Usuario u : usuarios) {
            if (!u.isEliminado() && u.getMail().equalsIgnoreCase(mail.trim())) {
                throw new DatoDuplicadoException("Ya existe un usuario registrado con el mail '" + mail + "'.");
            }
        }

        Usuario usuario = new Usuario(correlativoId++, nombre, apellido, mail, celular, contrasena, rol);
        usuarios.add(usuario);
        return usuario;
    }

    public List<Usuario> listarActivos() {
        List<Usuario> activos = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (!u.isEliminado()) {
                activos.add(u);
            }
        }
        return activos;
    }

    public Usuario buscarPorId(Long id) throws EntidadNoEncontradaException {
        for (Usuario u : usuarios) {
            if (u.getId().equals(id) && !u.isEliminado()) {
                return u;
            }
        }
        throw new EntidadNoEncontradaException("No se encontro un usuario activo con id " + id);
    }

    public void editar(Long id, String nombre, String apellido, String mail,
                        String celular, Rol rol)
            throws EntidadNoEncontradaException, DatoDuplicadoException, ValidacionException {

        Usuario usuario = buscarPorId(id);

        if (mail != null && !mail.isBlank()) {
            if (!mail.contains("@")) {
                throw new ValidacionException("El mail ingresado no es valido.");
            }
            for (Usuario u : usuarios) {
                if (!u.isEliminado() && !u.getId().equals(id) && u.getMail().equalsIgnoreCase(mail.trim())) {
                    throw new DatoDuplicadoException("Ya existe otro usuario con el mail '" + mail + "'.");
                }
            }
            usuario.setMail(mail);
        }
        if (nombre != null && !nombre.isBlank()) {
            usuario.setNombre(nombre);
        }
        if (apellido != null && !apellido.isBlank()) {
            usuario.setApellido(apellido);
        }
        if (celular != null) {
            usuario.setCelular(celular);
        }
        if (rol != null) {
            usuario.setRol(rol);
        }
    }

    public void eliminar(Long id) throws EntidadNoEncontradaException {
        Usuario usuario = buscarPorId(id);
        usuario.setEliminado(true);
    }
}
