# Capítulo 06 — Login: arquitectura y estado

## Objetivo

Escribir y enseñar el capítulo 06 de la guía: la primera funcionalidad completa
(login local) construida con Clean Architecture + MVVM y el patrón State/Intent,
según el enunciado del TP (§3.1, §8, §10, §12, §13, §18).

## Problema / Por qué

El TP exige MVVM + Clean Architecture con State/Intent, pero la guía (ch00-05)
nunca enseñó los conceptos de Kotlin que esos patrones usan: `interface`,
`object`/`data object`, `sealed class`, ni `ViewModel`/`StateFlow`.
Decisión (auditoría 2026-09-21): enseñarlos justo antes de usarse, dentro del
capítulo 06; corrutinas/Flow completo se difiere al capítulo 08 (Retrofit).

## Alcance

- Guía: `guia/src/content/docs/capitulos/06-login-arquitectura.md` (nuevo).
- App: pantalla de login con ViewModel, use case y repositorio local fake,
  en capas `presentation/domain/data`.
- Roadmap (`index.mdx`) y footer de ch05 actualizados.
- Fuera de alcance: navegación real (ch07), Hilt (ch09), backend.

## Restricciones

- Español neutro en la guía; sin jerga regional (regla permanente del usuario).
- Toda API nueva se explica con anatomía completa e imports exactos ANTES de
  los ejercicios; enseñanza en chat por micro-piezas con pregunta de control.
- Toda duda resuelta en chat se retroalimenta a la guía.
- TDD: no aplica (proyecto de guía docente; sin runner configurado para docs).

## Tareas

- [x] T1. Roadmap actualizado (ch06/ch08) + scaffold del capítulo 06 con
  lección 1 "Kotlin para arquitectura" (`interface`, `object`, `sealed class`)
  y ejercicio 1. Footer de ch05 enlaza ch06. — ruta: inline (archivos: 1 no
  trivial + 2 ediciones mecánicas). Commit `dbd0a75`; astro build verde
  (11 páginas); RDD assess: passive, review no debida, frontera avanza.
- [x] T1b. Lección 1 enseñada en chat por micro-piezas tras feedback de
  densidad (Mario se perdió desde `override`); guía reescrita gradual
  (commits `92dc75f`, `0c1d89c`). Ejercicio 1 resuelto por Mario en 3
  iteraciones; `LoginIntent.kt` real en `presentation/login` + solución con
  errores de batalla en la guía (commit `d7252b6`). Assess: medium
  (executable), `review_due=false` (`under_budget`) — slice medium pendiente
  hasta alcanzar presupuesto; frontera revisada sigue en la base del slice.
- [x] T2. Lección 2: Clean Architecture + MVVM enseñada en chat por
  micro-piezas (3 piezas: capas/restaurante, MVVM/mozo, viaje completo);
  Mario respondió bien las 3 preguntas de control (una necesitó replanteo más
  simple). Backport a la guía: secciones 3 y 4 + checkpoint (commit
  `c0c5d7a`; astro build verde, 11 páginas). — ruta: inline (1 archivo no
  trivial). RDD assess (base `dbd0a75`, committed-only): medium, 343 líneas,
  `review_due=false` (`under_budget`) — slice sigue pendiente.
- [x] T3. Lección 3: State + Intent enseñada en chat; Mario se trabó dos
  veces (metáfora "foto" sin código a la vista; origen de `onIntent`) y pidió
  desglose — destrabaron la escena en cámara lenta, la explicación de
  `onIntent` como parámetro (callback del ch05) y la tabla acción→aviso.
  Respondió bien las 3 preguntas finales (Enviar razonado por "no lleva
  datos"). Backport a la guía: sección 5 completa con esos andamios (commit
  `6909c0f`; astro build verde, 11 páginas). — ruta: inline (1 archivo no
  trivial). RDD assess (base `dbd0a75`, committed-only): medium, 517 líneas,
  `review_due=true` (`slice_budget_reached`) → Mario otorgó consentimiento;
  revisión nativa medium, 1 lente (review-reliability), APROBADA y confirmada
  (lineage `review-7f723d358b70abb2`, autoridad consumida). Frontera revisada
  avanza a `cbf4c95`. 6 hallazgos informativos; los 4 accionables corregidos
  en `bd7480f` (declaración de `resultado` en el ejemplo de `when`, mensaje
  completo del compilador + advertencia sobre `else`, referencia adelantada
  al ch08 reescrita, newline final en `LoginIntent.kt`). El hallazgo sobre
  compilación Kotlin no verificada queda pendiente: se registrará como
  evidencia el próximo build de Mario en Android Studio.
- [x] T4. Lección 4: ViewModel + StateFlow enseñada en chat en 6 piezas
  (clase/superpoder, MutableStateFlow, private/_state+ventana, copy(),
  onIntent por capas, collectAsState + cadena de dominós). Tropiezos
  resueltos: misconception de copy() (creía que descartaba campos no
  nombrados), View aterrizada como "= composable" (FichaPokemon), cadena
  final por reconocimiento (ordenar B→A). Nueva regla permanente por
  feedback de Mario: citar sección de la guía + tema, nunca "lección N" a
  secas (numeración de chat ≠ guía). Backport: sección 6 completa con los
  andamios pedidos — lectura derecha-a-izquierda, versión estirada en 3
  pasos, bug del paso olvidado, "el campo muestra la caja, no el teclado"
  (commit `c3512b8`; astro build verde, 11 páginas). — ruta: inline (1
  archivo no trivial). Nota RDD: slice previo `bd7480f`+`6e915a0` sigue con
  revisión pendiente (lineage `review-4e5f912876b6d7d3`, captura rechazada
  2x por salvaguardas del proveedor; Mario decidió continuar).
- [ ] T5. Lección 5: construir el login en la app por capas (domain: use case +
  repository interface; data: repo fake; presentation: screen + viewmodel).
  EN CURSO — Mario escribió 3 archivos reales guiado pieza por pieza:
  `domain/RepositorioSesion.kt` (vio en vivo el error del contrato sin
  cumplir y lo leyó del IDE), `data/RepositorioSesionLocal.kt` (primer
  import de código propio), `presentation/login/LoginState.kt` (transcripta
  con lectura guiada; recall en frío falló por fatiga — dictado con
  significado por línea). Commit `7e46a85` + formato de MainActivity
  `883b385`. IDE sin errores (evidencia de compilación del editor; build
  completo de Android Studio aún pendiente de reportar). Falta: ensamblar
  `LoginViewModel` (con `Enviar` llamando al repositorio), `LoginScreen` +
  `PantallaLogin`, cablear en `MainActivity`, probar login real. Dos
  candidatos de workspace intermedios fueron omitidos por Mario (ejercicio a
  medio escribir) y expiraron por drift sin gastar consentimiento.
- [ ] T6. Ejercicios resueltos por Mario integrados con soluciones + errores
  vistos en batalla; checkpoint final; capítulo marcado ✅ en roadmap.

## Criterios de aceptación

- La guía compila (`astro build` con Node 22 vía fnm) y el capítulo aparece en
  el índice.
- El login funciona en la app: credenciales fijas correctas → pantalla
  principal; incorrectas → error visible; sin lógica de negocio en composables.
- Mario respondió correctamente las preguntas de control de cada lección.

## Verificación

- `cd guia && eval "$(fnm env)" && fnm use 22 && npm run build` (guía).
- Build de la app en Android Studio (la corre Mario; evidencia: su reporte).

## Progreso

- 2026-09-21: auditoría TP vs guía hecha; huecos identificados; plan aprobado
  por Mario. T1 en curso (ruta inline; trigger de escritor no disparado: un
  solo archivo no trivial).
