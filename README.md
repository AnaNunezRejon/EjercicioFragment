<img width="1029" height="524" alt="image" src="https://github.com/user-attachments/assets/00b4f585-ae66-42a9-ae54-6737edc470bc" />


# EjercicioFragment  

Proyecto de práctica desarrollado en **Android Studio** para el ciclo formativo de **Desarrollo de Aplicaciones Multiplataforma (DAM)**.  
El objetivo principal es **aprender el uso de Fragments**, la **navegación dentro de una sola Activity**, y la **comunicación entre pantallas** en Java.

---

## Objetivos del proyecto  

- Comprender la estructura de una aplicación con una sola `Activity` y varios `Fragments`.  
- Aprender a realizar transacciones de fragments (`replace`, `addToBackStack`).  
- Pasar información entre pantallas sin usar bases de datos ni almacenamiento persistente.  
- Simular un flujo de trabajo con **inicio de sesión, pantalla principal y creación de tickets**.  

---

## Estructura del proyecto  

### **MainActivity (Contenedor principal)**  

- Es la **única Activity** del proyecto.  
- Contiene un `FrameLayout` que actúa como contenedor para todos los fragments.  
- Al iniciar la aplicación:
  - Si hay datos cargados en memoria, muestra el `HomeFragment`.  
  - Si no, carga el `LoginFragment`.  

```java
if (controlador.haySesionActiva()) {
    getSupportFragmentManager().beginTransaction()
            .replace(R.id.contenedor, new HomeFragment())
            .commit();
} else {
    getSupportFragmentManager().beginTransaction()
            .replace(R.id.contenedor, new LoginFragment())
            .commit();
}
```

### Fragments del proyecto
#### LoginFragment -------------------------------------------------------------------------------------------------------------------

Pantalla de inicio donde el usuario introduce su nombre, apellido y código de trabajador.
Los datos se guardan temporalmente en variables estáticas del controlador.

Campos principales:

Nombre
Apellido
Código de trabajador
Botón “Entrar”

Cuando el usuario pulsa Entrar, se cargan los datos y se abre el fragment principal (HomeFragment).

#### HomeFragment-------------------------------------------------------------------------------------------------------------------

Pantalla principal del sistema, donde se muestran los datos del usuario y las opciones de navegación.

Elementos principales:

Mensaje de bienvenida con el nombre del usuario.
Código del trabajador debajo del saludo.
Spinner con filtro de estados (solo decorativo por ahora).

Botones:
Nuevo Ticket → abre el fragment EditarFragment.
Cerrar Sesión → limpia los datos del usuario y vuelve al login.

#### EditarFragment-------------------------------------------------------------------------------------------------------------------

Pantalla vacía por el momento.
Incluye únicamente un botón Guardar, que al pulsarse vuelve a la pantalla anterior.
Más adelante se usará para crear o editar tickets con campos como descripción, resolución, estado, etc.

#### Controlador-------------------------------------------------------------------------------------------------------------------

Archivo situado en viewmodel/.
En esta versión inicial no hay persistencia real; los datos se guardan en memoria mediante variables estáticas.

### Estructura de carpetas
app/
 └── src/  
     └── main/  
         ├── java/com/example/ejerciciofragment/  
         │    ├── view/  
         │    │    ├── MainActivity.java  
         │    │    ├── LoginFragment.java  
         │    │    ├── HomeFragment.java  
         │    │    └── EditarFragment.java  
         │    └── viewmodel/  
         │         └── controlador.java  
         └── res/  
              ├── layout/  
              │    ├── activity_main.xml  
              │    ├── fragment_login.xml  
              │    ├── fragment_home.xml  
              │    └── fragment_editar.xml  
              └── values/  
                   └── strings.xml  

### Flujo de navegación  
Pantalla origen	Acción del usuario	Pantalla destino  
LoginFragment	Pulsar “Entrar”	HomeFragment  
HomeFragment	Pulsar “Nuevo Ticket”	EditarFragment  
HomeFragment	Pulsar “Cerrar sesión”	LoginFragment  
EditarFragment	Pulsar “Guardar”	HomeFragment  

### Tecnologías utilizadas

Java (Android Studio)  
Fragments  
XML Layouts  
LinearLayout / ScrollView  
Spinner y Button  
Gestión básica de navegación con FragmentManager  

### Próximas mejoras

Añadir una lista (RecyclerView) de tickets.  
Implementar un archivo .txt con datos simulados de tickets.  
Permitir editar y guardar tickets.  
Aplicar estilos y colores más parecidos al diseño de Figma.  

### Autora

Ana Núñez Rejón
Diseñadora gráfica y estudiante de Desarrollo de Aplicaciones Multiplataforma (DAM).
Proyecto académico para practicar el manejo de Fragments, transacciones y gestión de datos en memoria en Android Studio.
