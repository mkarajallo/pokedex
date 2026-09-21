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
  trivial). RDD assess pendiente de registrar tras el commit.
- [ ] T4. Lección 4: `ViewModel` + `StateFlow` mínimo (`collectAsState`).
- [ ] T5. Lección 5: construir el login en la app por capas (domain: use case +
  repository interface; data: repo fake; presentation: screen + viewmodel).
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
