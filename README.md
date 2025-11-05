# EjercicioFragment

Abordaje del Problema y Estructura
El núcleo de la aplicación será la combinación de MVVM y Fragments dentro de esa única Activity.

1. La Única Activity
Propósito: Actúa como el contenedor principal. Su única responsabilidad es cargar los Fragments y ser el punto de acceso para servicios a nivel de aplicación (como la configuración de la barra de herramientas principal).
Implementación: Contendrá un FrameLayout para reemplazar los Fragments a medida que el usuario navegue.
Navegación: Utiliza un menú inferior para manejarse entre fragments

2. Los Fragments (Vistas)
Cada pantalla o sección de tu aplicación será un Fragment. Estos representan la View en el patrón MVVM.
Responsabilidad: Mostrar datos e información (UI) y capturar la interacción del usuario (clics, entradas de texto).
Implementación: Tendrán una referencia al ViewModel asociado y observarán su LiveData para actualizar la UI automáticamente.

Hay 3 Fragment:  Editar - Mostrar - Crear (Ticket)

4. El ViewModel
Contiene la lógica y prepara los datos para la UI.
Responsabilidad: Mantener el estado de los datos  y exponer los datos a la View (el Fragment) a través de LiveData.
No conoce la View: Solo trata con datos puros y la capa de Modelo/Datos.

Filtrarticket, buscarticket, cambiarticket















1. Pantalla Principal: ListaTicketsFragment
Muestra un resumen de los tickets con opciones de filtrado y creación.
UI: Usar un RecyclerView para listar los objetos Ticket. Puedes tener pestañas o filtros (tickets abiertos, asignados a mí, etc.).
Interacción: Al hacer clic en un ítem, navega a DetalleTicketFragment, pasando el ID del ticket.

+------------------------------------+
| 🔵 STATUS BAR (Hora, Batería)      |
+------------------------------------+
| 🔵 MAIN ACTIVITY (TOOLBAR)         |
| [≡]  Mis Tickets                   |
+------------------------------------+
| ⬜ LISTA TICKETS FRAGMENT          |
| ---------------------------------- |
| | [Pestañas/Filtros]               |
| | Abiertos (12) | En Progreso (5)  |
| ---------------------------------- |
| | RecyclerView (Lista de Tickets)  |
| | ------------------------------ | |
| | | Ticket ID #123               | |
| | | Título: No funciona el Wi-Fi | |
| | | Prioridad: [ALTA]            | |
| | ------------------------------ | |
| | ------------------------------ | |
| | | Ticket ID #124               | |
| | | Título: Error de Impresión   | |
| | | Prioridad: [MEDIA]           | |
| | ------------------------------ | |
| |                                | |
| |                                | |
| ---------------------------------- |
| [➕] (Botón Flotante: Nuevo Ticket) |
+------------------------------------+

2. Creación de Ticket: NuevoTicketFragment
Un formulario simple para la entrada de datos, reemplazando la lista.
UI: Campos de texto para el título, descripción, estado, prioridad. Botones para "Cambiar Estado" o "Asignar Técnico".
Opcional: Un segundo RecyclerView dentro para mostrar los comentarios/actualizaciones del ticket.

+------------------------------------+
| 🔵 STATUS BAR                      |
+------------------------------------+
| 🔵 MAIN ACTIVITY (TOOLBAR)         |
| [←]  Crear Nuevo Ticket         [✓]| (Guardar)
+------------------------------------+
| ⬜ NUEVO TICKET FRAGMENT           |
| ---------------------------------- |
| | [Campo de Texto] Título:         |
| | ______________________________ | |
| | [Campo de Texto] Descripción:    |
| | ______________________________ | |
| | [Area de Texto Grande]           |
| | ______________________________ | |
| | [Spinner] Prioridad:             |
| | ▼ Media ______________________ | |
| |                                | |
| |                                | |
| |                                | |
| ---------------------------------- |
| [ CREAR Y ENVIAR TICKET ]          |
+------------------------------------+

3. Detalle y Gestión: DetalleTicketFragment
Muestra toda la información del Ticket y permite su modificación.

+------------------------------------+
| 🔵 STATUS BAR                      |
+------------------------------------+
| 🔵 MAIN ACTIVITY (TOOLBAR)         |
| [←]  Detalle Ticket #123        [⋮]| (Opciones)
+------------------------------------+
| ⬜ DETALLE TICKET FRAGMENT         |
| ---------------------------------- |
| | Título: No funciona el Wi-Fi     |
| | Prioridad: [ALTA] (Editable)     |
| | Asignado a: Juan Pérez (Editable)|
| ---------------------------------- |
| | Descripción del Problema:        |
| | (Texto largo aquí...)            |
| ---------------------------------- |
| | [Spinner] Cambiar Estado a:      |
| | ▼ En Progreso ________________ | |
| ---------------------------------- |
| | Historial/Comentarios:           |
| | - 05/Nov: Abierto por Cliente.   |
| | - 05/Nov: Asignado a Juan Pérez. |
| ---------------------------------- |
| [ AÑADIR COMENTARIO ]              |
+------------------------------------+

➕ NuevoTicketFragment
Función: Permitir al usuario crear un nuevo ticket.
UI: EditTexts para título y descripción. Spinners o RadioGroups para seleccionar la prioridad inicial.
Acción: Un botón para "Crear Ticket" que llama a la función del ViewModel para añadir el nuevo objeto a la base de datos (o la lista en memoria).


A. Estructuras de Datos (Model)

Ticket (Clase principal):
id (int/String)
titulo (String)
descripcion (String)
estado (Enum/String: "Abierto", "En Progreso", "Cerrado")
prioridad (Enum/String: "Baja", "Media", "Alta")
fechaCreacion
asignadoA (Usuario)

Usuario:
id
nombre
rol (Enum/String: "Técnico", "Cliente")


