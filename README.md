# ESCOMSurfers
## Documentación completa del proyecto, historia, arquitectura y evidencias

> **Resumen ejecutivo.** ESCOMSurfers es un videojuego móvil tipo *endless runner* desarrollado para Android, inspirado en la experiencia estudiantil de ESCOM/IPN. El proyecto combina una modalidad clásica de juego infinito con ranking en la nube y un **Modo Historia** de diez niveles donde el protagonista, Nico, debe llegar desde su casa hasta el aula de examen enfrentando obstáculos del transporte, la vida académica y el estrés universitario.

## 1. ¿En qué consiste el juego?
ESCOMSurfers consiste en controlar a un estudiante que corre en tres carriles, recoge créditos, utiliza power-ups y esquiva obstáculos. El proyecto se diseñó con una estética cartoon, recursos gráficos propios, escenas narrativas y una integración con servicios externos para autenticación y persistencia de puntajes.

Las dos experiencias principales del juego son las siguientes:

- **Modo Historia:** centrado en la narrativa. Utiliza recursos, diálogos, escenas y progresión local en el dispositivo para que el usuario avance por 10 niveles secuenciales.
- **Modo Clásico / Infinito:** enfocado en sobrevivir, acumular puntos y competir en el ranking. Esta modalidad se complementa con la infraestructura en la nube para el registro y consulta de puntajes.

## 2. Idea central y valor del proyecto
La propuesta convierte un trayecto cotidiano —ir de casa a la escuela y presentar un examen— en una aventura interactiva con humor, identidad estudiantil y componentes técnicos reales. Más allá de lo visual, el proyecto destaca porque integra:

- desarrollo Android con interfaz propia y gameplay continuo;
- autenticación con Google y registro por correo;
- almacenamiento de información y ranking en Firebase/Firestore;
- backend complementario con Node.js/Express;
- pruebas locales con Docker;
- despliegue posterior hacia Google Cloud Run;
- una capa narrativa original que fortalece la presentación del sistema ante un profesor o jurado.

## 3. Historia general del Modo Historia
El Modo Historia se titula **“El Extraordinario del Destino”**. El protagonista, **Nico**, es un estudiante que despierta tarde el día de un examen decisivo. Durante su trayecto aparece **Credi**, su credencial escolar convertida en guía consciente por el exceso de café, estrés y radiación de laboratorio. A lo largo del recorrido surgen escenarios exagerados pero reconocibles para cualquier estudiante: microbuses caóticos, estaciones imposibles, lluvias de tareas, copiadoras saturadas, cafeterías salvadoras, pasillos infinitos, laboratorios con errores de compilación y la presencia amenazante del **Profesor Null**. Todo culmina en el **Aula 404**, donde el examen final se convierte en el gran jefe simbólico del juego.

## 4. Personajes principales
- **Nico:** protagonista del juego. Representa al estudiante bajo presión que debe llegar a tiempo a su examen.
- **Credi:** credencial escolar con personalidad propia; guía, informa y acompaña a Nico durante la aventura.
- **Profesor Null:** antagonista principal. Simboliza el miedo al error, a la reprobación y al examen extraordinario.
- **Chofer, encargado de copias y cafetera:** personajes secundarios que enriquecen la temática y el tono del Modo Historia.

## 5. Construcción del proyecto
### 5.1 Aplicación móvil
La aplicación se construyó sobre Android, utilizando una estructura de actividades para autenticación, menú, ranking, escenas narrativas y gameplay. La lógica del juego se apoya en clases como `GameView`, `GameManager`, `Player`, `Obstacle`, `Coin`, `PowerUp`, `StoryManager` y administradores de progreso y puntaje.

### 5.2 Backend y persistencia
Para el módulo conectado se utilizó un backend con **Node.js/Express**, responsable de recibir puntajes, consultar rankings y exponer endpoints como `/api/health`, `/api/scores` y `/api/ranking`. La persistencia se complementa con **Firebase Authentication** para la identidad de usuario y **Cloud Firestore** para la base de datos del ranking.

### 5.3 Estrategia de despliegue
El flujo de trabajo se planteó en dos etapas:

1. **Pruebas locales con Docker y Docker Compose.** Esto permitió validar el servidor, el levantamiento del contenedor y el consumo de endpoints antes del despliegue final.
2. **Despliegue en la nube con Google Cloud.** Posteriormente, el backend se desplegó desde **Google Cloud Shell** hacia **Cloud Run**, logrando una URL pública para consulta de estado y servicios de ranking.

### 5.4 Distribución conceptual de responsabilidades
A nivel funcional, el proyecto puede interpretarse así:

- el **Modo Historia** se concentra mayormente en el lado del dispositivo móvil, donde residen los fondos, sprites, escenas, diálogos y avance por niveles;
- el **Modo Clásico / Infinito** se fortalece con la nube, ya que utiliza autenticación, consulta de ranking y registro de puntajes para competir entre usuarios;
- Firestore actúa como un repositorio central de resultados, mientras el backend facilita una capa de acceso más controlada a la información.

## 6. Evidencias técnicas de backend, nube y base de datos
### 6.1 Validación local con Docker
A continuación se muestran evidencias del levantamiento del servidor en entorno local, incluyendo la ejecución del contenedor y los logs de peticiones HTTP.

<img src="docs/evidencias/03_docker_compose_local_logs.jpeg" width="270" />
<img src="docs/evidencias/04_docker_desktop_container.jpeg" width="270" />

<img src="docs/evidencias/02_cloudshell_deploy_cloudrun.jpeg" width="270" />
<img src="docs/evidencias/01_cloudrun_health.jpeg" width="270" />

**Interpretación:** estas capturas evidencian que el servidor fue probado localmente, que el contenedor se ejecutó de forma correcta y que las peticiones a los endpoints principales respondieron con códigos exitosos.

### 6.2 Despliegue en Google Cloud y disponibilidad del servicio
La siguiente evidencia muestra el despliegue del servicio en Google Cloud Shell y su exposición mediante Cloud Run.

<img src="docs/evidencias/02_cloudshell_deploy_cloudrun.jpeg" width="520" />

<img src="docs/evidencias/01_cloudrun_health.jpeg" width="520" />

**Interpretación:** el despliegue hacia Cloud Run permitió publicar una URL funcional del backend, verificando el correcto estado del servicio mediante el endpoint de salud.

### 6.3 Firebase Authentication y Firestore
El acceso al sistema se validó con autenticación, tanto por correo/contraseña como por Google. Además, Firestore se empleó para registrar información de puntajes, usuario, dificultad y créditos.

<img src="docs/evidencias/05_login_google_account_picker.jpeg" width="270" />
<img src="docs/evidencias/06_login_screen.jpeg" width="270" />
<img src="docs/evidencias/07_menu_dificultad.jpeg" width="270" />
<img src="docs/evidencias/10_firestore_scores_collection.jpeg" width="270" />

<img src="docs/evidencias/10_firestore_scores_collection.jpeg" width="520" />

**Interpretación de la captura de Firestore:** se aprecia la colección `scores`, donde se almacenan atributos como `playerName`, `email`, `difficulty`, `level`, `credits`, `score`, `createdAt`, `uid` y `source`. Esto da soporte al ranking y a la trazabilidad de partidas registradas.

## 7. Evidencias de funcionamiento de la aplicación
El siguiente bloque resume el funcionamiento general de la app: autenticación, pantalla principal, selección de dificultad, ranking conectado y acceso al Modo Historia.

<img src="docs/evidencias/05_login_google_account_picker.jpeg" width="270" />
<img src="docs/evidencias/06_login_screen.jpeg" width="270" />
<img src="docs/evidencias/07_menu_dificultad.jpeg" width="270" />
<img src="docs/evidencias/08_ranking_general.jpeg" width="270" />
<img src="docs/evidencias/09_ranking_extraordinario.jpeg" width="270" />
<img src="docs/evidencias/14_story_mode_menu.jpeg" width="270" />
<img src="docs/evidencias/32_story_progress_screen.jpeg" width="270" />

**Lectura funcional:**

- La aplicación permite **inicio de sesión con Google**.
- También ofrece **registro e inicio por correo y contraseña**.
- El usuario puede elegir dificultad en el menú principal.
- El ranking puede consultarse por dificultad y muestra el top de jugadores.
- El modo historia guarda progreso y desbloquea niveles conforme se completan.

## 8. Desarrollo narrativo de los niveles
A continuación se describe, con enfoque de presentación académica, la construcción narrativa del Modo Historia y sus evidencias visuales.

### 8.1 Nivel 1: Se me hizo tarde
Nico despierta en su habitación el día de un examen importante. El cuarto desordenado, los apuntes regados y la presión académica introducen de inmediato el conflicto central. En este punto aparece Credi, una credencial consciente que funge como guía del protagonista. El nivel funciona como tutorial narrativo y mecánico: enseña movimiento, salto, cambio de carril y recolección de créditos mientras establece el tono humorístico del proyecto.

**Evidencias visuales del nivel:**

<img src="docs/evidencias/11_story_level1_credi_intro.jpeg" width="270" />
<img src="docs/evidencias/13_story_level1_narrador.jpeg" width="270" />
<img src="docs/evidencias/12_story_level1_gameplay.jpeg" width="270" />

### 8.2 Nivel 2: El microbús dimensional
Tras salir de casa, Nico aborda un microbús extraño que parece moverse entre realidades. El chofer aporta un tono cómico y surrealista a la aventura. Este nivel representa el transporte cotidiano del estudiante, pero reinterpretado como una prueba de reflejos y adaptación. Los obstáculos y el diálogo convierten un trayecto aparentemente normal en un escenario memorable.

**Evidencias visuales del nivel:**

<img src="docs/evidencias/19_story_level2_narrador.jpeg" width="270" />
<img src="docs/evidencias/15_story_level2_nico_intro.jpeg" width="270" />
<img src="docs/evidencias/16_story_level2_chofer_intro.jpeg" width="270" />
<img src="docs/evidencias/17_story_level2_gameplay.jpeg" width="270" />
<img src="docs/evidencias/18_story_level2_chofer_outro.jpeg" width="270" />

### 8.3 Nivel 3: La estación del caos
En esta etapa el jugador debe atravesar una estación inspirada en el transporte público de la ciudad. Los torniquetes, las barreras y el flujo del entorno simbolizan el estrés de los traslados urbanos. La narrativa convierte al torniquete en un antagonista menor, reforzando la personalidad del juego y vinculando lo cotidiano con el lenguaje visual de un endless runner.

**Evidencias visuales del nivel:**

<img src="docs/evidencias/21_story_level3_torniquete_intro.jpeg" width="270" />
<img src="docs/evidencias/20_story_level3_gameplay.jpeg" width="270" />

### 8.4 Nivel 4: Lluvia de tareas
Cuando Nico está más cerca de ESCOM, la presión escolar se materializa literalmente: el cielo se llena de tareas, pendientes y trabajo acumulado. El nivel representa la sensación de saturación académica y utiliza un ambiente lluvioso para reforzar la tensión dramática. Credi acompaña a Nico con comentarios que equilibran la ansiedad con humor.

**Evidencias visuales del nivel:**

<img src="docs/evidencias/23_story_level4_narrador_intro.jpeg" width="270" />
<img src="docs/evidencias/22_story_level4_credi_intro.jpeg" width="270" />
<img src="docs/evidencias/33_story_level6_or_level4_gameplay_extra.jpeg" width="270" />

### 8.5 Nivel 5: La copiadora infinita
La zona de copias se transforma en un espacio casi laberíntico. El jugador observa cómo la burocracia universitaria y la urgencia previa al examen se condensan en un nivel cargado de identidad estudiantil. El encargado de copias funciona como personaje secundario y la escena subraya que la travesía de Nico no es sólo física, sino también administrativa y mental.

**Evidencias visuales del nivel:**

<img src="docs/evidencias/24_story_level5_narrador_intro.jpeg" width="270" />
<img src="docs/evidencias/25_story_level5_nico_intro.jpeg" width="270" />
<img src="docs/evidencias/26_story_level5_copias_intro.jpeg" width="270" />
<img src="docs/evidencias/27_story_level5_gameplay.jpeg" width="270" />
<img src="docs/evidencias/28_story_level5_copias_outro.jpeg" width="270" />
<img src="docs/evidencias/29_story_level5_copias_outro_2.jpeg" width="270" />

### 8.6 Nivel 6: Cafetería de los sobrevivientes
Esta parte introduce un respiro narrativo: la cafetería se plantea como una estación de recuperación. Sin embargo, incluso este espacio cotidiano está gamificado con power-ups, obstáculos y un personaje especial: la cafetera. El nivel aporta color, variedad visual y un motivo temático clave: el café como combustible del estudiante.

**Evidencias visuales del nivel:**

<img src="docs/evidencias/30_story_level6_cafetera_intro.jpeg" width="270" />
<img src="docs/evidencias/31_story_level6_gameplay.jpeg" width="270" />

### 8.7 Nivel 7: El pasillo infinito de ESCOM
Nico finalmente entra al entorno universitario, pero el pasillo parece no terminar nunca. Esta exageración representa la sensación de que, aun estando ya en la escuela, el objetivo final sigue lejos. Credi vuelve a tomar protagonismo para guiar al jugador y reforzar la relación entre ambos personajes.

**Evidencias visuales del nivel:**

<img src="docs/evidencias/34_story_level7_nico_intro.jpeg" width="270" />
<img src="docs/evidencias/42_story_level7_credi_intro.jpeg" width="270" />
<img src="docs/evidencias/50_story_level7_nico_outro.jpeg" width="270" />

### 8.8 Nivel 8: Laboratorio sin compilación
En el laboratorio, la temática cambia hacia el universo de la programación y los errores de sistema. Una computadora con fallo se vuelve parte activa de la narrativa y del diseño de obstáculos. Este nivel conecta de forma directa con el perfil académico de ESCOM y aporta uno de los escenarios más coherentes con la identidad del proyecto.

**Evidencias visuales del nivel:**

<img src="docs/evidencias/43_story_level8_narrador_intro.jpeg" width="270" />
<img src="docs/evidencias/44_story_level8_computadora_intro.jpeg" width="270" />
<img src="docs/evidencias/45_story_level8_gameplay.jpeg" width="270" />
<img src="docs/evidencias/46_story_level8_computadora_outro.jpeg" width="270" />
<img src="docs/evidencias/47_story_level8_credi_outro.jpeg" width="270" />

### 8.9 Nivel 9: El profesor fantasma
Antes del examen final, Nico encara al Profesor Null, una figura simbólica que concentra el miedo al extraordinario, la reprobación y la incertidumbre académica. El nivel añade dramatismo y eleva la tensión narrativa. La presencia del antagonista le da unidad al modo historia y prepara el clímax.

**Evidencias visuales del nivel:**

<img src="docs/evidencias/35_story_level9_profnull_intro.jpeg" width="270" />
<img src="docs/evidencias/48_story_level9_profnull_intro_2.jpeg" width="270" />
<img src="docs/evidencias/49_story_level9_nico_intro.jpeg" width="270" />
<img src="docs/evidencias/36_story_level9_gameplay.jpeg" width="270" />

### 8.10 Nivel 10: Aula 404: Examen no encontrado
El cierre del modo historia lleva a Nico al aula final, donde el examen adquiere una dimensión casi épica. Este nivel resume toda la travesía: el cansancio, la presión, la perseverancia y la superación. El Profesor Null vuelve a aparecer como parte del desenlace, y el juego concluye transformando la experiencia estudiantil en una aventura completa con principio, desarrollo y final.

**Evidencias visuales del nivel:**

<img src="docs/evidencias/37_story_level10_narrador_intro.jpeg" width="270" />
<img src="docs/evidencias/38_story_level10_profnull_intro.jpeg" width="270" />
<img src="docs/evidencias/39_story_level10_gameplay.jpeg" width="270" />
<img src="docs/evidencias/40_story_level10_narrador_outro.jpeg" width="270" />
<img src="docs/evidencias/41_story_level10_profnull_outro.jpeg" width="270" />

## 9. Funcionamiento global del sistema
En conjunto, el proyecto funciona como una experiencia híbrida entre juego móvil y sistema conectado. El usuario entra a la app, se autentica, selecciona una modalidad y juega. En el caso del Modo Historia, el avance se desarrolla sobre el dispositivo con escenas, assets y lógica local. En el caso del modo clásico, los resultados pueden enviarse al backend para reflejarse en el ranking general. Este diseño da la impresión de un ecosistema completo, donde la experiencia individual del usuario se complementa con una infraestructura remota para servicios compartidos.

## 10. Conclusión
ESCOMSurfers no es únicamente un ejercicio visual o un prototipo aislado: es una propuesta integral que combina narrativa, diseño de juego, desarrollo móvil, autenticación, persistencia, contenedores y despliegue en la nube. El Modo Historia le otorga identidad propia y lo vuelve fácil de explicar y defender ante un profesor, mientras que el soporte con Firebase, Docker y Google Cloud refuerza su peso técnico. Desde el punto de vista académico, el proyecto demuestra la capacidad de integrar frontend móvil, backend, base de datos y experiencia de usuario en una sola solución.

## 11. Anexo: galería completa de capturas (sin omitir evidencias)
En esta sección se incluyen **todas las capturas proporcionadas**, ordenadas y renombradas para su referencia en la documentación final.

### 11.1 Evidencia 1
Prueba de disponibilidad del servidor desplegado en Cloud Run.

<img src="docs/evidencias/01_cloudrun_health.jpeg" width="420" />

### 11.2 Evidencia 2
Despliegue del backend desde Google Cloud Shell hacia Cloud Run.

<img src="docs/evidencias/02_cloudshell_deploy_cloudrun.jpeg" width="420" />

### 11.3 Evidencia 3
Pruebas locales del servidor ejecutadas con Docker Compose.

<img src="docs/evidencias/03_docker_compose_local_logs.jpeg" width="420" />

### 11.4 Evidencia 4
Validación visual del contenedor del servidor dentro de Docker Desktop.

<img src="docs/evidencias/04_docker_desktop_container.jpeg" width="420" />

### 11.5 Evidencia 5
Inicio de sesión con cuenta de Google desde la aplicación móvil.

<img src="docs/evidencias/05_login_google_account_picker.jpeg" width="420" />

### 11.6 Evidencia 6
Pantalla principal de autenticación con registro por correo y acceso con Google.

<img src="docs/evidencias/06_login_screen.jpeg" width="420" />

### 11.7 Evidencia 7
Menú principal con selección de dificultad y acceso al ranking.

<img src="docs/evidencias/07_menu_dificultad.jpeg" width="420" />

### 11.8 Evidencia 8
Pantalla del ranking general con consulta remota de puntajes.

<img src="docs/evidencias/08_ranking_general.jpeg" width="420" />

### 11.9 Evidencia 9
Pantalla de ranking filtrada por dificultad extraordinario.

<img src="docs/evidencias/09_ranking_extraordinario.jpeg" width="420" />

### 11.10 Evidencia 10
Vista de la colección scores en Cloud Firestore con documentos registrados desde la app.

<img src="docs/evidencias/10_firestore_scores_collection.jpeg" width="420" />

### 11.11 Evidencia 11
Nivel 1: introducción de Credi en la habitación de Nico.

<img src="docs/evidencias/11_story_level1_credi_intro.jpeg" width="420" />

### 11.12 Evidencia 12
Nivel 1: evidencia de gameplay con créditos y obstáculos.

<img src="docs/evidencias/12_story_level1_gameplay.jpeg" width="420" />

### 11.13 Evidencia 13
Nivel 1: escena narrativa que establece el retraso del protagonista.

<img src="docs/evidencias/13_story_level1_narrador.jpeg" width="420" />

### 11.14 Evidencia 14
Pantalla de progreso del modo historia y desbloqueo de niveles.

<img src="docs/evidencias/14_story_mode_menu.jpeg" width="420" />

### 11.15 Evidencia 15
Nivel 2: Nico entra al microbús dimensional.

<img src="docs/evidencias/15_story_level2_nico_intro.jpeg" width="420" />

### 11.16 Evidencia 16
Nivel 2: aparición del chofer como personaje secundario.

<img src="docs/evidencias/16_story_level2_chofer_intro.jpeg" width="420" />

### 11.17 Evidencia 17
Nivel 2: gameplay dentro del microbús con monedas y obstáculos.

<img src="docs/evidencias/17_story_level2_gameplay.jpeg" width="420" />

### 11.18 Evidencia 18
Nivel 2: diálogo del chofer al cierre del nivel.

<img src="docs/evidencias/18_story_level2_chofer_outro.jpeg" width="420" />

### 11.19 Evidencia 19
Nivel 2: escena narrativa complementaria.

<img src="docs/evidencias/19_story_level2_narrador.jpeg" width="420" />

### 11.20 Evidencia 20
Nivel 3: gameplay en la estación del caos.

<img src="docs/evidencias/20_story_level3_gameplay.jpeg" width="420" />

### 11.21 Evidencia 21
Nivel 3: intro del torniquete como obstáculo emblemático.

<img src="docs/evidencias/21_story_level3_torniquete_intro.jpeg" width="420" />

### 11.22 Evidencia 22
Nivel 4: Credi acompaña a Nico durante la lluvia de tareas.

<img src="docs/evidencias/22_story_level4_credi_intro.jpeg" width="420" />

### 11.23 Evidencia 23
Nivel 4: escena narrativa de apertura del nivel.

<img src="docs/evidencias/23_story_level4_narrador_intro.jpeg" width="420" />

### 11.24 Evidencia 24
Nivel 5: narrador presenta la copiadora infinita.

<img src="docs/evidencias/24_story_level5_narrador_intro.jpeg" width="420" />

### 11.25 Evidencia 25
Nivel 5: Nico llega a la zona de copias.

<img src="docs/evidencias/25_story_level5_nico_intro.jpeg" width="420" />

### 11.26 Evidencia 26
Nivel 5: personaje de copias introduce el conflicto del nivel.

<img src="docs/evidencias/26_story_level5_copias_intro.jpeg" width="420" />

### 11.27 Evidencia 27
Nivel 5: gameplay en la papelería.

<img src="docs/evidencias/27_story_level5_gameplay.jpeg" width="420" />

### 11.28 Evidencia 28
Nivel 5: cierre narrativo del área de copias.

<img src="docs/evidencias/28_story_level5_copias_outro.jpeg" width="420" />

### 11.29 Evidencia 29
Nivel 5: diálogo adicional del encargado de copias.

<img src="docs/evidencias/29_story_level5_copias_outro_2.jpeg" width="420" />

### 11.30 Evidencia 30
Nivel 6: introducción de la cafetera como personaje especial.

<img src="docs/evidencias/30_story_level6_cafetera_intro.jpeg" width="420" />

### 11.31 Evidencia 31
Nivel 6: gameplay en la cafetería.

<img src="docs/evidencias/31_story_level6_gameplay.jpeg" width="420" />

### 11.32 Evidencia 32
Pantalla intermedia del progreso desbloqueado del modo historia.

<img src="docs/evidencias/32_story_progress_screen.jpeg" width="420" />

### 11.33 Evidencia 33
Gameplay adicional con ambiente lluvioso y power-ups.

<img src="docs/evidencias/33_story_level6_or_level4_gameplay_extra.jpeg" width="420" />

### 11.34 Evidencia 34
Nivel 7: Nico entra al pasillo infinito de ESCOM.

<img src="docs/evidencias/34_story_level7_nico_intro.jpeg" width="420" />

### 11.35 Evidencia 35
Nivel 9: introducción del Profesor Null en el pasillo fantasma.

<img src="docs/evidencias/35_story_level9_profnull_intro.jpeg" width="420" />

### 11.36 Evidencia 36
Nivel 9: gameplay del enfrentamiento simbólico con el profesor.

<img src="docs/evidencias/36_story_level9_gameplay.jpeg" width="420" />

### 11.37 Evidencia 37
Nivel 10: entrada al Aula 404 y al examen final.

<img src="docs/evidencias/37_story_level10_narrador_intro.jpeg" width="420" />

### 11.38 Evidencia 38
Nivel 10: diálogo previo con Profesor Null.

<img src="docs/evidencias/38_story_level10_profnull_intro.jpeg" width="420" />

### 11.39 Evidencia 39
Nivel 10: gameplay del examen final extraordinario.

<img src="docs/evidencias/39_story_level10_gameplay.jpeg" width="420" />

### 11.40 Evidencia 40
Nivel 10: cierre narrativo posterior al desafío final.

<img src="docs/evidencias/40_story_level10_narrador_outro.jpeg" width="420" />

### 11.41 Evidencia 41
Nivel 10: desenlace con Profesor Null.

<img src="docs/evidencias/41_story_level10_profnull_outro.jpeg" width="420" />

### 11.42 Evidencia 42
Nivel 7: Credi orienta a Nico dentro del pasillo infinito.

<img src="docs/evidencias/42_story_level7_credi_intro.jpeg" width="420" />

### 11.43 Evidencia 43
Nivel 8: apertura del laboratorio sin compilación.

<img src="docs/evidencias/43_story_level8_narrador_intro.jpeg" width="420" />

### 11.44 Evidencia 44
Nivel 8: la computadora con error se presenta como obstáculo.

<img src="docs/evidencias/44_story_level8_computadora_intro.jpeg" width="420" />

### 11.45 Evidencia 45
Nivel 8: gameplay dentro del laboratorio.

<img src="docs/evidencias/45_story_level8_gameplay.jpeg" width="420" />

### 11.46 Evidencia 46
Nivel 8: escena de cierre con la computadora averiada.

<img src="docs/evidencias/46_story_level8_computadora_outro.jpeg" width="420" />

### 11.47 Evidencia 47
Nivel 8: Credi comenta la superación del laboratorio.

<img src="docs/evidencias/47_story_level8_credi_outro.jpeg" width="420" />

### 11.48 Evidencia 48
Nivel 9: nueva aparición de Profesor Null.

<img src="docs/evidencias/48_story_level9_profnull_intro_2.jpeg" width="420" />

### 11.49 Evidencia 49
Nivel 9: Nico se prepara para enfrentar la fase previa al examen.

<img src="docs/evidencias/49_story_level9_nico_intro.jpeg" width="420" />

### 11.50 Evidencia 50
Nivel 7: cierre narrativo del pasillo infinito de ESCOM.

<img src="docs/evidencias/50_story_level7_nico_outro.jpeg" width="420" />
