package com.escom.escomsurfers;

public final class StoryManager {

    private static StoryLevel[] levels;

    private StoryManager() {
    }

    public static int getLevelCount() {
        return getLevels().length;
    }

    public static StoryLevel[] getLevels() {
        if (levels == null) {
            levels = buildLevels();
        }
        return levels;
    }

    public static StoryLevel getLevel(int index) {
        StoryLevel[] allLevels = getLevels();

        if (index < 1) {
            return allLevels[0];
        }

        if (index > allLevels.length) {
            return allLevels[allLevels.length - 1];
        }

        return allLevels[index - 1];
    }

    private static DialogueLine nico(String text) {
        return new DialogueLine("Nico", text, R.drawable.story_nico_idle_1);
    }

    private static DialogueLine credi(String text) {
        return new DialogueLine("Credi", text, R.drawable.story_credi_1);
    }

    private static DialogueLine nullTeacher(String text) {
        return new DialogueLine("Profesor Null", text, R.drawable.story_prof_null_1);
    }

    private static DialogueLine narrator(String text) {
        return new DialogueLine("Narrador", text, R.drawable.escom_ui_logo);
    }

    private static StoryLevel[] buildLevels() {
        return new StoryLevel[]{
                new StoryLevel(
                        1,
                        "Se me hizo tarde",
                        "Nico despierta tarde y descubre que su credencial sabe demasiado.",
                        45_000,
                        "FACIL",
                        R.drawable.story_bg_home,
                        new int[]{R.drawable.story_obstacle_mochila, R.drawable.story_obstacle_zapato, R.drawable.story_obstacle_perro},
                        new DialogueLine[]{
                                narrator("La leyenda dice que todo estudiante de ESCOM tiene una mañana que define su semestre."),
                                narrator("Para Nico Byte, esa mañana empezó con el celular al 2%, la mochila abierta y una alarma que llevaba sonando desde hace veinte minutos."),
                                nico("¿Qué hora es?... No, no, no. ¡El examen de Algoritmos de Supervivencia es hoy!"),
                                credi("Confirmo: dormiste como si ya estuvieras de vacaciones."),
                                nico("¿Quién dijo eso?"),
                                credi("Soy Credi, tu credencial. Cobré conciencia por exceso de café, estrés y radiación de laboratorio."),
                                nico("Mi credencial está hablando. Perfecto. Ya empecé a alucinar."),
                                credi("No estás alucinando. El Profesor Null cerrará la puerta del examen a las 8:00 exactas."),
                                nico("Entonces tengo que correr."),
                                credi("Corre, salta, deslízate y junta créditos. Hoy no basta con llegar: tienes que sobrevivir al camino.")
                        },
                        new DialogueLine[]{
                                nico("Salí de mi casa. Eso ya cuenta como logro desbloqueado."),
                                credi("Logro desbloqueado: estudiante funcional durante cuarenta y cinco segundos."),
                                narrator("Pero al cruzar la puerta, Nico sintió que la ciudad estaba diferente, como si el examen hubiera alterado la realidad."),
                                credi("El camino a ESCOM se está defendiendo. El Profesor Null sabe que venimos."),
                                nico("¿El examen tiene sistema de seguridad?"),
                                credi("En ESCOM, hasta las fotocopias tienen fase dos.")
                        },
                        new StoryMessage[]{
                                new StoryMessage(5, "Credi", "Bienvenido al modo historia. Sobrevive hasta que termine el tiempo."),
                                new StoryMessage(12, "Nico", "¡Mi mochila está intentando escapar!"),
                                new StoryMessage(21, "Credi", "Las cosas pequeñas también reprueban. Esquívalas."),
                                new StoryMessage(32, "Nico", "Prometo jamás volver a estudiar una noche antes."),
                                new StoryMessage(39, "Credi", "Eso dijiste el semestre pasado.")
                        }
                ),
                new StoryLevel(
                        2,
                        "El microbús dimensional",
                        "El transporte público no va a ESCOM: va hacia donde el destino lo permita.",
                        60_000,
                        "FACIL",
                        R.drawable.story_bg_bus,
                        new int[]{R.drawable.story_obstacle_bache, R.drawable.story_obstacle_cono, R.drawable.story_obstacle_micro},
                        new DialogueLine[]{
                                narrator("Nico llegó a la avenida justo cuando un microbús apareció envuelto en humo y música de cumbia interdimensional."),
                                nico("¿Este micro sí pasa por ESCOM?"),
                                new DialogueLine("Chofer", "Pasa por ESCOM, por el metro, por tu infancia y a veces por otra realidad.", R.drawable.story_micro_driver),
                                credi("No me gusta esa respuesta, pero tampoco tenemos otra opción."),
                                nico("¿Cuánto falta para llegar?"),
                                new DialogueLine("Chofer", "Depende del tráfico, de los baches y de si el micro decide obedecer las leyes de la física.", R.drawable.story_micro_driver),
                                narrator("El motor rugió. Las ventanas vibraron. El pasillo del micro se estiró como si fuera un túnel infinito."),
                                credi("Sujétate. Este nivel no es transporte: es una prueba de reflejos."),
                                nico("Si sobrevivo, voy a poner esto como experiencia laboral.")
                        },
                        new DialogueLine[]{
                                nico("Sobreviví al micro. Creo que vi tres rutas, dos universos y una señora vendiendo gelatinas."),
                                credi("Y aún así llegamos más rápido que caminando."),
                                narrator("El micro se detuvo con un frenón épico. Nico salió disparado hacia la estación."),
                                new DialogueLine("Chofer", "¡Suerte, joven! Y si reprueba, mañana pasa otro micro al extraordinario.", R.drawable.story_micro_driver),
                                nico("Eso no fue motivador."),
                                credi("Fue transporte público. La motivación se cobra aparte.")
                        },
                        new StoryMessage[]{
                                new StoryMessage(9, "Credi", "¡Bache dimensional! Si caes, apareces en clase de las siete."),
                                new StoryMessage(22, "Nico", "¡El piso se está moviendo!"),
                                new StoryMessage(34, "Chofer", "¡Agárrense, ahí viene el tope legendario!"),
                                new StoryMessage(47, "Credi", "Ese cono no estaba ahí. El examen lo invocó."),
                                new StoryMessage(56, "Nico", "Nunca pensé que extrañaría caminar.")
                        }
                ),
                new StoryLevel(
                        3,
                        "La estación del caos",
                        "Torniquetes, tarjetas sin saldo y gente con prisa forman el primer jefe urbano.",
                        70_000,
                        "MEDIO",
                        R.drawable.story_bg_station,
                        new int[]{R.drawable.story_obstacle_torniquete, R.drawable.story_obstacle_tarjeta, R.drawable.story_obstacle_gente},
                        new DialogueLine[]{
                                narrator("La estación estaba más llena que un laboratorio antes de entregar práctica."),
                                nico("Solo tengo que pasar el torniquete. Fácil."),
                                new DialogueLine("Torniquete", "Acceso denegado. Saldo emocional insuficiente.", R.drawable.story_obstacle_torniquete),
                                nico("¿Ahora los torniquetes también hablan?"),
                                credi("Hoy todo lo que puede retrasarte desarrolló personalidad."),
                                new DialogueLine("Torniquete", "Para pasar, demuestre velocidad, paciencia y una relación sana con el transporte público.", R.drawable.story_obstacle_torniquete),
                                nico("No tengo ninguna de esas."),
                                credi("Entonces improvisa. La multitud se mueve en patrones: observa, respira y corre."),
                                narrator("Nico guardó la credencial en la mochila. Credi vibró como radar académico.")
                        },
                        new DialogueLine[]{
                                nico("Ya pasé la estación. Perdí dignidad, pero gané tiempo."),
                                credi("También ganaste tres créditos imaginarios."),
                                nico("¿Sirven para reinscripción?"),
                                credi("Sirven para que yo te admire un poquito."),
                                narrator("Al salir, el cielo se oscureció. No era lluvia normal: eran tareas pendientes acumulándose en las nubes."),
                                nico("Eso se ve mal."),
                                credi("Eso se ve como tu historial de pendientes.")
                        },
                        new StoryMessage[]{
                                new StoryMessage(10, "Torniquete", "Acceso condicionado a reflejos superiores."),
                                new StoryMessage(24, "Credi", "La gente se mueve como algoritmo sin ordenar."),
                                new StoryMessage(38, "Nico", "¡Me empujaron hacia el carril incorrecto!"),
                                new StoryMessage(54, "Credi", "Si dudas, salta. Si no sabes, también salta."),
                                new StoryMessage(65, "Torniquete", "Interesante. El usuario aún no colapsa.")
                        }
                ),
                new StoryLevel(
                        4,
                        "Lluvia de tareas",
                        "Los pendientes de Nico toman forma física y caen desde el cielo.",
                        75_000,
                        "MEDIO",
                        R.drawable.story_bg_rain,
                        new int[]{R.drawable.story_obstacle_pdf, R.drawable.story_obstacle_tarea, R.drawable.story_obstacle_charco},
                        new DialogueLine[]{
                                narrator("A unas calles de ESCOM, el cielo comenzó a abrirse como una carpeta de Drive compartida a destiempo."),
                                nico("¿Por qué están cayendo tareas del cielo?"),
                                credi("Son todas las que marcaste como 'lo hago al rato'."),
                                nico("Pero algunas son de semestres pasados."),
                                credi("El karma académico no prescribe."),
                                narrator("Cada hoja tenía manchas de café, fechas vencidas y una vibra de 'entrega antes de medianoche'."),
                                nico("Si una tarea me toca, ¿qué pasa?"),
                                credi("Tendrás flashbacks de reportes sin conclusión."),
                                nico("Entonces mejor corro.")
                        },
                        new DialogueLine[]{
                                nico("Escapé de la lluvia de tareas. Estoy mojado, cansado y ligeramente más responsable."),
                                credi("No exageres. Sigues debiendo dos reportes."),
                                narrator("La tormenta se disipó, pero dejó papeles pegados en las paredes como advertencia."),
                                credi("Necesitamos una guía de examen. Hay una zona de copias cerca."),
                                nico("¿Copias? Por fin algo normal."),
                                credi("Nunca digas eso antes de entrar a una papelería en modo historia.")
                        },
                        new StoryMessage[]{
                                new StoryMessage(8, "Credi", "PDF gigante a la izquierda. Ese pesa más que tu laptop."),
                                new StoryMessage(22, "Nico", "¡Una tarea me está persiguiendo!"),
                                new StoryMessage(39, "Credi", "Si ves portada, índice y bibliografía, corre doble."),
                                new StoryMessage(57, "Nico", "¡Ese charco tiene forma de parcial sorpresa!"),
                                new StoryMessage(69, "Credi", "Últimos metros. No dejes que te alcance la procrastinación.")
                        }
                ),
                new StoryLevel(
                        5,
                        "La copiadora infinita",
                        "Una papelería común se transforma en laberinto de hojas, tóner y filas eternas.",
                        80_000,
                        "MEDIO",
                        R.drawable.story_bg_copyshop,
                        new int[]{R.drawable.story_obstacle_copias, R.drawable.story_obstacle_toner, R.drawable.story_obstacle_grapa},
                        new DialogueLine[]{
                                narrator("La papelería parecía pequeña desde afuera. Por dentro era un reino de hojas, grapas y desesperación estudiantil."),
                                nico("Solo necesito imprimir una guía de dos páginas."),
                                new DialogueLine("Copias", "Joven, aquí todo documento se convierte en trámite épico.", R.drawable.story_copies_man),
                                nico("¿Cuánto tarda?"),
                                new DialogueLine("Copias", "Si la máquina coopera, veinte minutos. Si huele miedo, cuarenta.", R.drawable.story_copies_man),
                                credi("La copiadora detectó que vienes tarde. Está cargando modo laberinto."),
                                narrator("Las hojas comenzaron a salir solas. El tóner burbujeó como criatura oscura."),
                                nico("Solo quería estudiar cinco minutos antes del examen."),
                                credi("Ese es el pensamiento más peligroso de todos.")
                        },
                        new DialogueLine[]{
                                nico("Conseguí la guía."),
                                credi("La mitad está chueca y la otra mitad borrosa, pero parece útil."),
                                new DialogueLine("Copias", "Vuelva pronto, joven. Aquí también encuadernamos crisis existenciales.", R.drawable.story_copies_man),
                                narrator("Nico guardó la guía como si fuera un mapa del tesoro."),
                                credi("Siguiente parada: cafetería. Necesitas energía o vas a entrar al examen en modo ahorro de batería."),
                                nico("Que sea café grande. Tamaño desesperación.")
                        },
                        new StoryMessage[]{
                                new StoryMessage(11, "Credi", "Hojas por todos lados. No confíes en el papel tamaño oficio."),
                                new StoryMessage(27, "Nico", "¡El tóner se está moviendo!"),
                                new StoryMessage(42, "Copias", "La máquina no falla. Solo evalúa tu paciencia."),
                                new StoryMessage(61, "Credi", "Cuidado con la hoja administrativa. Es más peligrosa que parece."),
                                new StoryMessage(74, "Nico", "¡Ya casi salgo de esta fila infinita!")
                        }
                ),
                new StoryLevel(
                        6,
                        "Cafetería de los sobrevivientes",
                        "Antes de entrar a ESCOM, Nico busca el café que separa al estudiante del modo zombie.",
                        80_000,
                        "MEDIO",
                        R.drawable.story_bg_cafeteria,
                        new int[]{R.drawable.story_obstacle_torta, R.drawable.story_obstacle_vaso, R.drawable.story_obstacle_mesa},
                        new DialogueLine[]{
                                narrator("La cafetería olía a café, pan, salsa y decisiones cuestionables tomadas con sueño."),
                                nico("Necesito café o mi cerebro va a compilar en modo seguro."),
                                new DialogueLine("Cafetera", "Solo los dignos obtienen cafeína.", R.drawable.story_coffee_machine),
                                nico("No tengo tiempo para una prueba espiritual."),
                                credi("Toda cafetería escolar tiene rituales. Respétalos."),
                                new DialogueLine("Cafetera", "Para beber del café legendario, esquiva la torta del destino, el vaso derramado y la mesa que aparece de la nada.", R.drawable.story_coffee_machine),
                                nico("¿La mesa también es parte de la prueba?"),
                                credi("La mesa siempre fue parte de la prueba."),
                                narrator("Nico apretó la guía contra el pecho y corrió hacia el mostrador.")
                        },
                        new DialogueLine[]{
                                nico("Café conseguido. Siento que puedo ver el código fuente del universo."),
                                credi("Eso es cafeína y ansiedad. Úsalas sabiamente."),
                                new DialogueLine("Cafetera", "Has sido aceptado por la bebida sagrada.", R.drawable.story_coffee_machine),
                                narrator("Con energía renovada, Nico cruzó la entrada de ESCOM."),
                                credi("Ya estamos dentro. Ahora comienza la parte rara."),
                                nico("¿Más rara que una cafetera mística?"),
                                credi("Mucho más.")
                        },
                        new StoryMessage[]{
                                new StoryMessage(10, "Cafetera", "Demuestra que mereces cafeína."),
                                new StoryMessage(24, "Credi", "El café te ayuda, pero no te vuelve invencible."),
                                new StoryMessage(39, "Nico", "¡Esa torta tiene mirada de jefe final!"),
                                new StoryMessage(58, "Credi", "La mesa apareció sin renderizar. Esquiva."),
                                new StoryMessage(72, "Cafetera", "El estudiante avanza. Interesante aroma a pánico.")
                        }
                ),
                new StoryLevel(
                        7,
                        "El pasillo infinito de ESCOM",
                        "Dentro de la escuela, los pasillos se estiran y los salones cambian de lugar.",
                        90_000,
                        "DIFICIL",
                        R.drawable.bg_escom,
                        new int[]{R.drawable.obstacle_bug, R.drawable.obstacle_exam, R.drawable.obstacle_homework},
                        new DialogueLine[]{
                                narrator("Nico entró a ESCOM esperando encontrar su salón. En cambio, encontró un pasillo que no respetaba la geometría."),
                                nico("Ya estoy en la escuela. ¿Dónde está el salón?"),
                                credi("Según el mapa, deberíamos estar en planta baja, pero también en tercer piso y posiblemente en 2019."),
                                nico("Eso no tiene sentido."),
                                credi("Exacto. Es el Pasillo Infinito."),
                                narrator("Las luces parpadearon. Al fondo aparecieron tareas, exámenes y bugs como si el semestre entero hubiera cobrado forma."),
                                nico("¿Por qué un bug está caminando por el pasillo?"),
                                credi("Tal vez escapó de tu práctica. No hagas contacto visual."),
                                nico("Solo necesito encontrar mi aula."),
                                credi("Entonces corre como si cada puerta fuera una oportunidad de no reprobar.")
                        },
                        new DialogueLine[]{
                                nico("Ese pasillo no terminaba nunca."),
                                credi("Bienvenido a ESCOM: hasta caminar puede tener complejidad O(n²)."),
                                narrator("Una puerta de laboratorio se abrió sola. Desde dentro salía luz azul y olor a computadora caliente."),
                                nico("Tal vez ahí pueda revisar mis apuntes."),
                                credi("O tal vez ahí vive el bug que te odia. Pero adelante."),
                                narrator("Nico entró al laboratorio sin saber que el compilador ya lo esperaba.")
                        },
                        new StoryMessage[]{
                                new StoryMessage(12, "Credi", "Los carriles son tu única realidad estable."),
                                new StoryMessage(28, "Nico", "¡Ese examen casi me pega!"),
                                new StoryMessage(44, "Credi", "Bug rojo al frente. Se ve personal."),
                                new StoryMessage(66, "Nico", "Creo que pasé por el mismo salón tres veces."),
                                new StoryMessage(82, "Credi", "El pasillo se está cerrando. Sigue corriendo.")
                        }
                ),
                new StoryLevel(
                        8,
                        "Laboratorio sin compilación",
                        "El código de Nico abre un portal de errores y libera bugs académicos.",
                        95_000,
                        "DIFICIL",
                        R.drawable.story_bg_lab,
                        new int[]{R.drawable.obstacle_bug, R.drawable.story_obstacle_monitor, R.drawable.story_obstacle_cable},
                        new DialogueLine[]{
                                narrator("El laboratorio estaba vacío, pero todas las computadoras estaban encendidas."),
                                nico("Qué raro. Nadie dejó sesión abierta."),
                                new DialogueLine("Computadora", "Error: conocimiento no encontrado.", R.drawable.story_computer_error),
                                nico("Eso fue muy personal."),
                                credi("La computadora leyó tu carpeta de prácticas."),
                                narrator("En la pantalla apareció una ventana negra. Luego otra. Luego cien. Todas tenían símbolos rojos y un sonido de compilación fallida."),
                                new DialogueLine("Computadora", "Compilando miedo... Generando bug rojo...", R.drawable.story_computer_error),
                                nico("¿Se puede cancelar?"),
                                credi("No. Solo puedes correr."),
                                narrator("Del monitor salió una sombra con patas: el Bug Rojo, alimentado por errores no corregidos.")
                        },
                        new DialogueLine[]{
                                nico("Creo que dejé atrás al bug."),
                                credi("No lo dejaste. Solo se fue a actualizar."),
                                new DialogueLine("Computadora", "Advertencia: el Profesor Null ha detectado movimiento hacia el aula.", R.drawable.story_computer_error),
                                nico("¿El profesor también controla las computadoras?"),
                                credi("El Profesor Null controla todo lo que se bloquea cinco minutos antes de entregar."),
                                narrator("La salida del laboratorio condujo al pasillo de profesores, donde el silencio pesaba más que un examen sorpresa.")
                        },
                        new StoryMessage[]{
                                new StoryMessage(13, "Computadora", "Error crítico: estudiante sin estudiar."),
                                new StoryMessage(29, "Credi", "Cables en el suelo. No tropieces con tu propio destino."),
                                new StoryMessage(45, "Nico", "¡El monitor azul me está siguiendo!"),
                                new StoryMessage(63, "Credi", "El bug aprende tus movimientos. Cambia de carril."),
                                new StoryMessage(84, "Computadora", "Compilación fallida. Persistencia del estudiante: alta.")
                        }
                ),
                new StoryLevel(
                        9,
                        "El profesor fantasma",
                        "Antes del examen, el Profesor Null exige una última prueba en el pasillo de firmas.",
                        100_000,
                        "DIFICIL",
                        R.drawable.story_bg_teacher_hall,
                        new int[]{R.drawable.story_obstacle_sello, R.drawable.story_obstacle_hoja, R.drawable.story_obstacle_reloj},
                        new DialogueLine[]{
                                narrator("El pasillo de profesores estaba quieto. Demasiado quieto. Cada puerta parecía observar a Nico."),
                                nullTeacher("Llegas tarde, Nico Byte."),
                                nico("¿Usted es el Profesor Null?"),
                                nullTeacher("Soy el eco de cada parcial no estudiado, cada práctica entregada a las 11:59 y cada 'sí le entendí' falso."),
                                credi("Traducción: sí, es él."),
                                nullTeacher("Para entrar al aula 404, necesitas una firma simbólica."),
                                nico("¿Y cómo se consigue?"),
                                nullTeacher("Responde: ¿qué es la vida?"),
                                nico("¿Eso viene en el temario?"),
                                nullTeacher("Todo viene en el temario cuando no estudiaste."),
                                credi("Corre. Es la única respuesta válida.")
                        },
                        new DialogueLine[]{
                                nullTeacher("Interesante. Aún no repruebas."),
                                nico("Eso sonó como una amenaza."),
                                credi("Porque fue una amenaza con rúbrica."),
                                nullTeacher("Te concedo el paso al aula 404. Pero recuerda: el examen no se contesta solo."),
                                narrator("La puerta final apareció al fondo del pasillo, iluminada por un brillo rojo."),
                                nico("Entonces este es el final."),
                                credi("No. Este es el examen. El final depende de tus reflejos.")
                        },
                        new StoryMessage[]{
                                new StoryMessage(14, "Profesor Null", "Define recursividad sin usar recursividad."),
                                new StoryMessage(30, "Nico", "¡Eso ni siquiera es justo!"),
                                new StoryMessage(48, "Credi", "Los sellos son trampas administrativas. Evítalos."),
                                new StoryMessage(67, "Profesor Null", "El tiempo es un recurso no renovable."),
                                new StoryMessage(88, "Credi", "Última firma. No mires al reloj.")
                        }
                ),
                new StoryLevel(
                        10,
                        "Aula 404: Examen no encontrado",
                        "El examen final cobra vida y convierte el salón en la última carrera.",
                        120_000,
                        "EXTRAORDINARIO",
                        R.drawable.story_bg_exam_room,
                        new int[]{R.drawable.story_obstacle_examen_final, R.drawable.story_obstacle_formula, R.drawable.story_obstacle_cero},
                        new DialogueLine[]{
                                narrator("Nico abrió la puerta del aula 404. Adentro no había alumnos. Solo pupitres, silencio y un examen flotando en el centro."),
                                nico("Llegué... al fin."),
                                nullTeacher("No llegaste al examen. El examen llegó a ti."),
                                credi("Nico, esto ya no es una prueba normal. Es el Extraordinario del Destino."),
                                nico("Pero yo venía a ordinario."),
                                nullTeacher("La puntualidad decidió lo contrario."),
                                narrator("Las hojas comenzaron a girar. Fórmulas, relojes y calificaciones imposibles llenaron el salón."),
                                nullTeacher("Pregunta uno: explique todo el semestre."),
                                nico("¡No hay espacio en la hoja!"),
                                credi("No respondas con teoría. Responde con movimiento."),
                                nico("¿Correr cuenta como procedimiento?"),
                                credi("Hoy sí.")
                        },
                        new DialogueLine[]{
                                narrator("El examen se deshizo en una lluvia de hojas brillantes. El aula volvió a ser normal."),
                                nullTeacher("Has sobrevivido al Examen del Destino."),
                                nico("¿Entonces pasé?"),
                                nullTeacher("Sacaste ocho. Pero por estilo dramático."),
                                credi("Eso es más de lo que calculé con tus horas de sueño."),
                                nico("¿Y ahora?"),
                                credi("Ahora duermes, respaldas tus prácticas y jamás vuelves a decir 'lo hago al rato'."),
                                narrator("Nico salió del aula con la guía arrugada, la credencial brillante y una nueva leyenda para contar en los pasillos de ESCOM."),
                                narrator("Fin del Modo Historia: El Extraordinario del Destino.")
                        },
                        new StoryMessage[]{
                                new StoryMessage(12, "Profesor Null", "Pregunta uno: explique todo el semestre."),
                                new StoryMessage(28, "Credi", "No pelees con las fórmulas. Esquívalas."),
                                new StoryMessage(46, "Nico", "¡El examen está cambiando de forma!"),
                                new StoryMessage(66, "Profesor Null", "Tu promedio tiembla."),
                                new StoryMessage(84, "Credi", "Concéntrate. Cada segundo cuenta."),
                                new StoryMessage(104, "Nico", "¡Ya casi! ¡No voy a reprobar por una hoja gigante!"),
                                new StoryMessage(114, "Credi", "Última recta, ESCOMita. Corre como leyenda.")
                        }
                )
        };
    }
}
