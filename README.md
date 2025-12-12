# 🃏 Arquitectura de Software - Proyecto Juego Rummy

Este proyecto representa el juego de Mesa Rummy Multijugador Online o Localhost, centrándose en una arquitectura de software MVC limpia integrada con la Arquitectura Blackboard bien definida para asegurar la escalabilidad y el mantenimiento manejando una comunicacion intermitente asincrona con sockets TCP.

Release: 12/12/2025
---

## 🛠️ Tecnologías Utilizadas

*   **Java**: Lenguaje de programación principal.
*   **Java Swing**: Utilizada para la interfaz de usuario.
*   **Maven**: Para la gestión de dependencias y la construcción del proyecto.
*   **Sockets TCP**: Para la comunicacion TCP entre cliente y servidor.

---

## 🚀 Cómo Empezar

### 📋 Prerrequisitos

Asegúrate de tener instalado el JDK17+ (Java Development Kit) en tu PC.

### ⚙️ Instalación por Git

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
> Es prioritario primero ejecutar la clase main `BlackboardServer.java` del paquete `main` en el proyecto `BlackboardServer`
> Una vez corriendo, Abrir el Proyecto `Ensambladores` y Cambiar la clase `Configuracion` dentro del paquete `Util` la IP prestablecida, por la IP que este corriendo el `BlackboardServer.java`.
> Para iniciar el juego como cliente y poder jugar, ejecuta la clase `EnsambladorMVC.java` ubicada en el paquete `Ensambladores` del Proyecto `Ensambladores`.

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


## CONTENIDO DE BLACKBOARD:
*  **`Blackboard`** : Ubicada como EstadoJuegoPizarra.java es utilizada como pizarra "dummy" el estado del juego sin logica de negocio, responsable de guardar datos escenciales del juego y notificar al Controlador mediante el patron `Observer` cuando recibe un cambio por la comunicacion TCP.
*  **`Controlador`** : Ubicado como ControladorBlackboard.java es utilizado como un mediador encargado de hablarle a los Agentes(`Componentes MVC`) cuando se le notifica el cambio , esta comunicacion se realiza mediante Sockets TCP que se abren y se cierran para mantener una comunicacion Asincrona Intermitente y no cargar los recursos.
*  **`Agentes de Conocimiento`** : Ubicados como los `Componentes MVC` de todo el proyecto son los encargados de escribir en la pizarra y como anteriormente se explico ellos mantienen la logica de negocio y tambien utilizan el patron `Observer`, escriben dentro de la Pizarra utilizando los mismos SocketsTCP, `Controlador` los notifica mientras que ellos escuchan esos cambios gracias a la interfaz PropertyChangeListener.
---

## 👨‍💻 Equipo de Desarrollo
- Sebastian Moreno 00000252840
- Benjamin Soto 00000253183
- Luciano Barcelo 00000252086
- Chris Fitch 00000252379
- Gael Guerra
