package integrado.prog2.menu;

import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Producto;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.enums.Rol;
import integrado.prog2.exception.DatoDuplicadoException;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.StockInvalidoException;
import integrado.prog2.exception.ValidacionException;
import integrado.prog2.service.CategoriaService;
import integrado.prog2.service.PedidoService;
import integrado.prog2.service.ProductoService;
import integrado.prog2.service.UsuarioService;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner SC = new Scanner(System.in);

    private static final CategoriaService categoriaService = new CategoriaService();
    private static final ProductoService productoService = new ProductoService();
    private static final UsuarioService usuarioService = new UsuarioService();
    private static final PedidoService pedidoService = new PedidoService();

    public static void main(String[] args) {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n=== SISTEMA DE PEDIDOS FOOD STORE ===");
            System.out.println("1. Categorias");
            System.out.println("2. Productos");
            System.out.println("3. Usuarios");
            System.out.println("4. Pedidos");
            System.out.println("0. Salir");
            int opcion = leerEntero("Seleccione: ");

            switch (opcion) {
                case 1 -> menuCategorias();
                case 2 -> menuProductos();
                case 3 -> menuUsuarios();
                case 4 -> menuPedidos();
                case 0 -> salir = true;
                default -> System.out.println("Opcion fuera de rango. Intente nuevamente.");
            }
        }
        System.out.println("Gracias por usar Food Store. Hasta luego!");
        SC.close();
    }

    // ====================== CATEGORIAS ======================

    private static void menuCategorias() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- CATEGORIAS ---");
            System.out.println("1. Listar");
            System.out.println("2. Crear");
            System.out.println("3. Editar");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");
            int opcion = leerEntero("Seleccione: ");

            switch (opcion) {
                case 1 -> listarCategorias();
                case 2 -> crearCategoria();
                case 3 -> editarCategoria();
                case 4 -> eliminarCategoria();
                case 0 -> volver = true;
                default -> System.out.println("Opcion fuera de rango.");
            }
        }
    }

    private static void listarCategorias() {
        List<Categoria> categorias = categoriaService.listarActivas();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorias cargadas.");
            return;
        }
        categorias.forEach(System.out::println);
    }

    private static void crearCategoria() {
        try {
            String nombre = leerTexto("Nombre: ");
            String descripcion = leerTexto("Descripcion: ");
            Categoria categoria = categoriaService.crear(nombre, descripcion);
            System.out.println("Categoria creada con exito. Id asignado: " + categoria.getId());
        } catch (ValidacionException | DatoDuplicadoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void editarCategoria() {
        listarCategorias();
        Long id = leerLong("Ingrese el id de la categoria a editar: ");
        try {
            categoriaService.buscarPorId(id);
            String nombre = leerTextoOpcional("Nuevo nombre (enter para no modificar): ");
            String descripcion = leerTextoOpcional("Nueva descripcion (enter para no modificar): ");
            categoriaService.editar(id, nombre.isBlank() ? null : nombre,
                    descripcion.isBlank() ? null : descripcion);
            System.out.println("Categoria actualizada con exito.");
        } catch (EntidadNoEncontradaException | DatoDuplicadoException | ValidacionException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void eliminarCategoria() {
        listarCategorias();
        Long id = leerLong("Ingrese el id de la categoria a eliminar: ");
        if (!confirmar("Confirma la eliminacion? (S/N): ")) {
            System.out.println("Operacion cancelada.");
            return;
        }
        try {
            Categoria categoria = categoriaService.buscarPorId(id);
            boolean tieneProductos = !productoService.listarPorCategoria(categoria).isEmpty();
            categoriaService.eliminar(id, tieneProductos);
            System.out.println("Categoria eliminada con exito.");
        } catch (EntidadNoEncontradaException | ValidacionException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ====================== PRODUCTOS ======================

    private static void menuProductos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- PRODUCTOS ---");
            System.out.println("1. Listar");
            System.out.println("2. Crear");
            System.out.println("3. Editar");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");
            int opcion = leerEntero("Seleccione: ");

            switch (opcion) {
                case 1 -> listarProductos();
                case 2 -> crearProducto();
                case 3 -> editarProducto();
                case 4 -> eliminarProducto();
                case 0 -> volver = true;
                default -> System.out.println("Opcion fuera de rango.");
            }
        }
    }

    private static void listarProductos() {
        List<Producto> productos = productoService.listarActivos();
        if (productos.isEmpty()) {
            System.out.println("No hay productos cargados.");
            return;
        }
        productos.forEach(System.out::println);
    }

    private static void crearProducto() {
        listarCategorias();
        try {
            String nombre = leerTexto("Nombre: ");
            String descripcion = leerTexto("Descripcion: ");
            double precio = leerDouble("Precio: ");
            int stock = leerEntero("Stock: ");
            String imagen = leerTexto("Imagen (url o nombre de archivo): ");
            boolean disponible = confirmar("Disponible? (S/N): ");
            Long idCategoria = leerLong("Id de categoria: ");
            Categoria categoria = categoriaService.buscarPorId(idCategoria);

            Producto producto = productoService.crear(nombre, descripcion, precio, stock,
                    imagen, disponible, categoria);
            System.out.println("Producto creado con exito. Id asignado: " + producto.getId());
        } catch (ValidacionException | StockInvalidoException | EntidadNoEncontradaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void editarProducto() {
        listarProductos();
        Long id = leerLong("Ingrese el id del producto a editar: ");
        try {
            productoService.buscarPorId(id);
            String nombre = leerTextoOpcional("Nuevo nombre (enter para no modificar): ");
            String descripcion = leerTextoOpcional("Nueva descripcion (enter para no modificar): ");
            String precioStr = leerTextoOpcional("Nuevo precio (enter para no modificar): ");
            String stockStr = leerTextoOpcional("Nuevo stock (enter para no modificar): ");
            String imagen = leerTextoOpcional("Nueva imagen (enter para no modificar): ");
            String dispStr = leerTextoOpcional("Disponible? S/N (enter para no modificar): ");
            String catStr = leerTextoOpcional("Nuevo id de categoria (enter para no modificar): ");

            Double precio = precioStr.isBlank() ? null : Double.parseDouble(precioStr);
            Integer stock = stockStr.isBlank() ? null : Integer.parseInt(stockStr);
            Boolean disponible = dispStr.isBlank() ? null : dispStr.equalsIgnoreCase("S");
            Categoria categoria = catStr.isBlank() ? null : categoriaService.buscarPorId(Long.parseLong(catStr));

            productoService.editar(id, nombre.isBlank() ? null : nombre,
                    descripcion.isBlank() ? null : descripcion, precio, stock, imagen, disponible, categoria);
            System.out.println("Producto actualizado con exito.");
        } catch (NumberFormatException e) {
            System.out.println("Error: dato numerico invalido.");
        } catch (EntidadNoEncontradaException | StockInvalidoException | ValidacionException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void eliminarProducto() {
        listarProductos();
        Long id = leerLong("Ingrese el id del producto a eliminar: ");
        if (!confirmar("Confirma la eliminacion? (S/N): ")) {
            System.out.println("Operacion cancelada.");
            return;
        }
        try {
            productoService.eliminar(id);
            System.out.println("Producto eliminado con exito.");
        } catch (EntidadNoEncontradaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ====================== USUARIOS ======================

    private static void menuUsuarios() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- USUARIOS ---");
            System.out.println("1. Listar");
            System.out.println("2. Crear");
            System.out.println("3. Editar");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");
            int opcion = leerEntero("Seleccione: ");

            switch (opcion) {
                case 1 -> listarUsuarios();
                case 2 -> crearUsuario();
                case 3 -> editarUsuario();
                case 4 -> eliminarUsuario();
                case 0 -> volver = true;
                default -> System.out.println("Opcion fuera de rango.");
            }
        }
    }

    private static void listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarActivos();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios cargados.");
            return;
        }
        usuarios.forEach(System.out::println);
    }

    private static void crearUsuario() {
        try {
            String nombre = leerTexto("Nombre: ");
            String apellido = leerTexto("Apellido: ");
            String mail = leerTexto("Mail: ");
            String celular = leerTexto("Celular: ");
            String contrasena = leerTexto("Contrasena: ");
            Rol rol = leerRol();

            Usuario usuario = usuarioService.crear(nombre, apellido, mail, celular, contrasena, rol);
            System.out.println("Usuario creado con exito. Id asignado: " + usuario.getId());
        } catch (ValidacionException | DatoDuplicadoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void editarUsuario() {
        listarUsuarios();
        Long id = leerLong("Ingrese el id del usuario a editar: ");
        try {
            usuarioService.buscarPorId(id);
            String nombre = leerTextoOpcional("Nuevo nombre (enter para no modificar): ");
            String apellido = leerTextoOpcional("Nuevo apellido (enter para no modificar): ");
            String mail = leerTextoOpcional("Nuevo mail (enter para no modificar): ");
            String celular = leerTextoOpcional("Nuevo celular (enter para no modificar): ");

            usuarioService.editar(id, nombre.isBlank() ? null : nombre,
                    apellido.isBlank() ? null : apellido, mail.isBlank() ? null : mail,
                    celular.isBlank() ? null : celular, null);
            System.out.println("Usuario actualizado con exito.");
        } catch (EntidadNoEncontradaException | DatoDuplicadoException | ValidacionException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void eliminarUsuario() {
        listarUsuarios();
        Long id = leerLong("Ingrese el id del usuario a eliminar: ");
        if (!confirmar("Confirma la eliminacion? (S/N): ")) {
            System.out.println("Operacion cancelada.");
            return;
        }
        try {
            usuarioService.eliminar(id);
            System.out.println("Usuario eliminado con exito.");
        } catch (EntidadNoEncontradaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ====================== PEDIDOS ======================

    private static void menuPedidos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- PEDIDOS ---");
            System.out.println("1. Listar");
            System.out.println("2. Crear");
            System.out.println("3. Actualizar estado/forma de pago");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");
            int opcion = leerEntero("Seleccione: ");

            switch (opcion) {
                case 1 -> listarPedidos();
                case 2 -> crearPedido();
                case 3 -> actualizarPedido();
                case 4 -> eliminarPedido();
                case 0 -> volver = true;
                default -> System.out.println("Opcion fuera de rango.");
            }
        }
    }

    private static void listarPedidos() {
        List<Pedido> pedidos = pedidoService.listarActivos();
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos cargados.");
            return;
        }
        pedidos.forEach(p -> {
            System.out.println(p);
            p.getDetalles().forEach(d -> System.out.println("   -> " + d));
        });
    }

    private static void crearPedido() {
        listarUsuarios();
        Long idUsuario = leerLong("Id del usuario que realiza el pedido: ");
        Pedido pedido;
        try {
            Usuario usuario = usuarioService.buscarPorId(idUsuario);
            FormaPago formaPago = leerFormaPago();
            pedido = pedidoService.iniciarPedido(usuario, formaPago);
        } catch (EntidadNoEncontradaException | ValidacionException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        boolean agregarMas = true;
        boolean error = false;
        while (agregarMas && !error) {
            listarProductos();
            Long idProducto = leerLong("Id del producto a agregar: ");
            int cantidad = leerEntero("Cantidad: ");
            try {
                Producto producto = productoService.buscarPorId(idProducto);
                pedido.addDetallePedido(cantidad, producto.getPrecio(), producto);
                System.out.println("Detalle agregado con exito.");
            } catch (EntidadNoEncontradaException | StockInvalidoException | ValidacionException e) {
                System.out.println("Error al agregar el detalle: " + e.getMessage());
                System.out.println("Se cancela la creacion del pedido para no dejar datos inconsistentes.");
                pedidoService.descartar(pedido);
                error = true;
                break;
            }
            agregarMas = confirmar("Desea agregar otro producto? (S/N): ");
        }

        if (!error) {
            if (pedido.getDetalles().isEmpty()) {
                System.out.println("El pedido no tiene detalles cargados. Se cancela la operacion.");
                return;
            }
            pedidoService.confirmar(pedido);
            System.out.println("Pedido creado con exito. Id asignado: " + pedido.getId() +
                    " - Total: $" + pedido.getTotal());
        }
    }

    private static void actualizarPedido() {
        listarPedidos();
        Long id = leerLong("Id del pedido a actualizar: ");
        try {
            pedidoService.buscarPorId(id);
            Estado estado = leerEstadoOpcional();
            FormaPago formaPago = leerFormaPagoOpcional();
            pedidoService.actualizarEstadoYFormaPago(id, estado, formaPago);
            System.out.println("Pedido actualizado con exito.");
        } catch (EntidadNoEncontradaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void eliminarPedido() {
        listarPedidos();
        Long id = leerLong("Id del pedido a eliminar: ");
        if (!confirmar("Confirma la eliminacion? (S/N): ")) {
            System.out.println("Operacion cancelada.");
            return;
        }
        try {
            pedidoService.eliminar(id);
            System.out.println("Pedido eliminado con exito.");
        } catch (EntidadNoEncontradaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ====================== UTILIDADES DE LECTURA ======================

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = SC.nextLine();
            try {
                return Integer.parseInt(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un numero entero valido.");
            }
        }
    }

    private static double leerDouble(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = SC.nextLine();
            try {
                return Double.parseDouble(entrada.trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un numero valido.");
            }
        }
    }

    private static Long leerLong(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = SC.nextLine();
            try {
                return Long.parseLong(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un id numerico valido.");
            }
        }
    }

    private static String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = SC.nextLine();
            if (!entrada.isBlank()) {
                return entrada.trim();
            }
            System.out.println("El campo no puede estar vacio.");
        }
    }

    private static String leerTextoOpcional(String mensaje) {
        System.out.print(mensaje);
        return SC.nextLine().trim();
    }

    private static boolean confirmar(String mensaje) {
        System.out.print(mensaje);
        String entrada = SC.nextLine().trim();
        return entrada.equalsIgnoreCase("S");
    }

    private static Rol leerRol() {
        System.out.println("Rol: 1) ADMIN  2) USUARIO");
        int opcion = leerEntero("Seleccione: ");
        return (opcion == 1) ? Rol.ADMIN : Rol.USUARIO;
    }

    private static FormaPago leerFormaPago() {
        System.out.println("Forma de pago: 1) TARJETA  2) TRANSFERENCIA  3) EFECTIVO");
        int opcion = leerEntero("Seleccione: ");
        return switch (opcion) {
            case 1 -> FormaPago.TARJETA;
            case 2 -> FormaPago.TRANSFERENCIA;
            default -> FormaPago.EFECTIVO;
        };
    }

    private static FormaPago leerFormaPagoOpcional() {
        System.out.println("Nueva forma de pago: 0) No modificar  1) TARJETA  2) TRANSFERENCIA  3) EFECTIVO");
        int opcion = leerEntero("Seleccione: ");
        return switch (opcion) {
            case 1 -> FormaPago.TARJETA;
            case 2 -> FormaPago.TRANSFERENCIA;
            case 3 -> FormaPago.EFECTIVO;
            default -> null;
        };
    }

    private static Estado leerEstadoOpcional() {
        System.out.println("Nuevo estado: 0) No modificar  1) PENDIENTE  2) CONFIRMADO  3) TERMINADO  4) CANCELADO");
        int opcion = leerEntero("Seleccione: ");
        return switch (opcion) {
            case 1 -> Estado.PENDIENTE;
            case 2 -> Estado.CONFIRMADO;
            case 3 -> Estado.TERMINADO;
            case 4 -> Estado.CANCELADO;
            default -> null;
        };
    }
}
