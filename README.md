# 🃏 Arquitectura de Software - Proyecto Juego Rummy

Este proyecto implementa el juego de Rummy, centrándose en una arquitectura de software MVC limpia y bien definida para asegurar la escalabilidad y el mantenimiento.

Release: 03/10/2025
---

## 🛠️ Tecnologías Utilizadas

*   **Java**: Lenguaje de programación principal.
*   **Java Swing / JavaFX**: Utilizada para la interfaz de usuario.
*   **Maven / Gradle**: Para la gestión de dependencias y la construcción del proyecto.

---

## 🚀 Cómo Empezar

### 📋 Prerrequisitos

Asegúrate de tener instalado el JDK (Java Development Kit) en tu máquina.

### ⚙️ Instalación

1.  **Clonar el repositorio**:
    ```bash
    git clone https://github.com/SebastianMorenoV/RummyJuego.git
    ```
2.  **Navegar al directorio del proyecto**:
    ```bash
    cd proyecto-rummy
    ```
3.  **Hacer clean y build**:
    > [!NOTE]
    > Es crucial realizar un `clean and build with dependencies` antes de ejecutar. 

### ▶️ Ejecutar el Programa

> [!TIP]
> Para iniciar el juego, ejecuta la clase `Main` ubicada en el paquete `main`.

1.  **Ejecutar la clase principal**:
    Despues de clean and build correr la clase main.

## 🏗️ Arquitectura del Software

El proyecto sigue un diseño modular para separar la lógica de negocio de la interfaz de usuario. Las clases principales incluyen:

*   **`Ficha`**: Representa una ficha individual (Color,numero y id).
*   **`Tablero`**: Gestiona la baraja de fichas y repartir, almacena los grupos de las fichas de los jugadores y valida jugadas. 
*   **`Grupo`**: Representa un conjunto de fichas (Tipo de grupo,cantidad fichas y las fichas).
*   **`Jugador`**: Representa un jugador en la partida de el juego, cada uno tiene su mano.
*   **`Mano`**: Representa la mano de un jugador, cada mano contiene sus fichas..


## CONTENIDO DE MVC:
*   **`Vista`**: Responsable de toda la logica de presentación, pintar objetos y repintar los objetos de presentación.
(Obtiene los datos gracias a la implementacion de el patron observer)
*   **`Controlador`**: Responsable de atender las llamadas de la vista, dirigiendolas hacia el modelo.
*   **`Modelo`**: Responsable de la logica principal de el juego y dirigir llamadas a entidades necesarias para validar las reglas de el juego. (Se comunica con la vista a traves de segregar una interfaz con metodos para obtener datos, pasandoselo como notificacion a los observadores de esta misma). 

---

## 👨‍💻 Equipo de Desarrollo
- Sebastian Moreno 00000252840
- Benjamin Soto 00000253183
- Luciano Barcelo 00000252086
- Chris Fitch ID
- Gael Guerra