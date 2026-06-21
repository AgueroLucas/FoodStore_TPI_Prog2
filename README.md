# Food Store – Sistema de Gestión de Pedidos de Comida

Trabajo Práctico Integrador – Programación 2 (UTN FRM Anexo Rivadavia, Comisión 5 - Grupo 6)

## Descripción

Aplicación de consola en Java 21 que gestiona Categorías, Productos, Usuarios y
Pedidos (con sus Detalles) de un negocio de comidas, utilizando Programación
Orientada a Objetos: herencia (clase abstracta `Base`), interfaces (`Calculable`),
colecciones en memoria y manejo de excepciones propias.

## Requisitos

- JDK 21 o superior.
- IntelliJ IDEA (recomendado) o cualquier IDE compatible con proyectos Java.

## Cómo ejecutar

### Desde IntelliJ IDEA
1. Abrir la carpeta del proyecto (`File > Open`).
2. Esperar a que IntelliJ indexe el proyecto.
3. Ejecutar la clase `integrado.prog2.menu.Main.java`.

### Desde consola
```bash
cd src
javac -d ../out integrado/prog2/**/*.java integrado/prog2/menu/Main.java
java -cp ../out integrado.prog2.menu.Main
```
(o simplemente compilar todos los `.java` bajo `src/` con `javac -d out $(find . -name "*.java")`)

## Estructura del proyecto

```
src/
└── integrado/prog2/
    ├── menu/         -> Main.java (interacción por consola)
    ├── service/       -> Lógica de negocio (CategoriaService, ProductoService, UsuarioService, PedidoService)
    ├── entities/       -> Modelo de dominio (Base, Categoria, Producto, Usuario, Pedido, DetallePedido, Calculable)
    ├── enums/          -> Rol, Estado, FormaPago
    └── exception/      -> Excepciones propias del dominio(DatoDuplicadoException, EntidadNoEncontradaException, StockInvalidoException, ValidacionException)
```

## Reglas de negocio implementadas

- Soft delete (baja lógica) en todas las entidades mediante el flag `eliminado`.
- Precio y stock de `Producto` no pueden ser negativos.
- No se permite crear un `Pedido` sin `Usuario`.
- La cantidad de un `DetallePedido` debe ser mayor a 0.
- El mail de `Usuario` es único.
- El nombre de `Categoria` es único.
- Si falla la carga de un detalle al crear un pedido, se cancela toda la
  operación y se devuelve el stock ya descontado (rollback en memoria).

## Video demostrativo

https://drive.google.com/file/d/13ezeH4pnpn1vu_JRUrPZmLGavqBBVb90/view?usp=drive_link

## Documentación (PDF)

https://drive.google.com/file/d/1q4KdqGzMhwNRQDr8B_i1GnI07KjMTocO/view?usp=drive_link

## Autores

Pamela Chirino, Mayra Mulé, Lucas Agüero – Programación 2 – Comisión 5 - Grupo 6
