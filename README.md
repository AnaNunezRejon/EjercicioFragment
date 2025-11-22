<img width="1028" height="520" alt="image" src="https://github.com/user-attachments/assets/a9073a79-3820-4d28-9a09-aeca794b689f" />



#  EjercicioFragment – Proyecto Android con Fragments (DAM)

Proyecto desarrollado en **Android Studio** como práctica para el módulo de **Programación Multimedia y Dispositivos Móviles (DAM)**.  
El objetivo es aprender a trabajar con **Fragments**, **navegación en una sola Activity** y **gestión de datos en memoria**, simulando un sistema básico de tickets.

---

##  Objetivos del proyecto

- Comprender la arquitectura **Single-Activity** con múltiples **Fragments**.  
- Realizar transacciones de navegación entre fragments.  
- Practicar la comunicación entre pantallas usando un controlador.  
- Simular un flujo real de aplicación con:
  - Inicio de sesión  
  - Pantalla principal  
  - Creación / edición de tickets  

---

##  Arquitectura general

El proyecto sigue una estructura sencilla basada en **View / ViewModel**:

- **View:** Activity + Fragments  
- **ViewModel:** Clase `controlador` que gestiona los datos en memoria  

El flujo principal se ejecuta siempre dentro de una sola Activity, que actúa como contenedor de los fragments.

---

##  Fragments del proyecto

### 🔹 LoginFragment

Pantalla inicial donde el usuario introduce:

- Nombre  
- Apellido  
- Código de trabajador  

Al pulsar **Entrar**:  
Los datos se guardan temporalmente en el controlador y se navega al **HomeFragment**.  
Incluye validación básica de campos vacíos.

---

### 🔹 HomeFragment

Pantalla principal del sistema. Muestra:

- Saludo personalizado con el nombre del usuario  
- Código del trabajador  
- Spinner decorativo con estados de ticket  
- Botones principales:
  - **Nuevo Ticket**
  - **Cerrar sesión**

Desde aquí se accede al EditarFragment y también se puede cerrar sesión, lo que limpia los datos del controlador.

---

### 🔹 EditarFragment

Pantalla destinada a crear o editar un ticket.  
Versión actual:

- Campos básicos (ID, usuario, estado, descripción, resolución)  
- Botón **Guardar** que registra el ticket en memoria y vuelve al HomeFragment  

En futuras versiones soportará edición real y validación avanzada.

---

### 🔹 Controlador (ViewModel)

Clase que almacena:

- Datos del usuario en sesión  
- Lista de tickets  
- Operaciones de creación y consulta  

Actúa como una “base de datos” en memoria en esta fase inicial del proyecto.

---

##  Estructura del proyecto

app/
└── src/main/
├── java/com/example/ejerciciofragment/
│ ├── view/
│ │ ├── MainActivity.java
│ │ ├── LoginFragment.java
│ │ ├── HomeFragment.java
│ │ └── EditarFragment.java
│ └── viewmodel/
│ └── controlador.java
└── res/
├── layout/
│ ├── activity_main.xml
│ ├── fragment_login.xml
│ ├── fragment_home.xml
│ └── fragment_editar.xml
└── values/
└── strings.xml


---

##  Flujo de navegación

| Pantalla Origen     | Acción del usuario    | Pantalla destino |
|---------------------|------------------------|------------------|
| LoginFragment       | Entrar                 | HomeFragment     |
| HomeFragment        | Nuevo Ticket           | EditarFragment   |
| HomeFragment        | Cerrar sesión          | LoginFragment    |
| EditarFragment      | Guardar                | HomeFragment     |

---

##  Tecnologías utilizadas

- Java  
- Fragments  
- XML Layouts  
- FragmentManager  
- LinearLayout y ScrollView  
- Spinner y Buttons  
- Controlador en memoria (ViewModel simple)  

---

##  Mejoras implementadas

- Validación en Login  
- Control de sesión activo  
- Navegación fluida entre fragments  
- Limpieza y organización del código  
- Base del sistema de tickets funcional  

---

##  Próximas mejoras

- Integrar un **RecyclerView** para mostrar los tickets.  
- Leer tickets desde un archivo **.txt** o **.json**.  
- Guardar datos al cerrar la app (SharedPreferences o ficheros).  
- Añadir colores según estado del ticket.  
- Crear modo edición completo.  
- Mejorar el diseño según el prototipo de Figma.  

---

##  Autora

**Ana Núñez Rejón**  
Diseñadora gráfica y estudiante de **Desarrollo de Aplicaciones Multiplataforma (DAM)**.  
Proyecto: Uso de Fragments, navegación y gestión de datos en memoria dentro de Android Studio.
