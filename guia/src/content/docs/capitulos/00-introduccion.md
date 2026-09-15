---
title: 00 — Introducción
description: Qué vamos a construir, con qué tecnologías y cómo encajan entre sí.
---

**Al terminar este capítulo vas a saber**: qué app vamos a construir, con qué tecnologías,
y cómo encajan entre sí — sin haber escrito una línea de código todavía.

---

## Qué vamos a construir

Una **Pokédex**: una app Android que consume la API pública [PokéAPI](https://pokeapi.co/)
y permite:

1. Iniciar sesión (login local simple).
2. Ver un listado de Pokémon con imagen, nombre e ID.
3. Ver el detalle de un Pokémon (tipos, altura, peso, estadísticas...).
4. Marcar y desmarcar favoritos.
5. Ver la lista de favoritos, que **sobrevive al cierre de la app** (persistencia local).

El flujo completo:

```text
Login → Home (barra inferior con 2 pestañas)
         ├── Pokémon    → Detalle
         └── Favoritos  → Detalle
```

## Las tecnologías, y por qué cada una

Una app Android moderna no es un solo lenguaje y listo — es un conjunto de piezas,
cada una con un rol. Estas son las de este proyecto:

| Pieza | Rol | Si venís de la web, es como... |
|---|---|---|
| **Kotlin** | El lenguaje de programación | JavaScript (pero con tipos, como TypeScript) |
| **Jetpack Compose** | Construir la interfaz con código | React: componentes + estado, nada de HTML |
| **Gradle** | Gestionar dependencias y compilar | `npm` + Vite en una sola herramienta |
| **Retrofit** | Llamar a la API REST | `fetch()`, pero declarativo |
| **Room** | Base de datos local (SQLite) | `localStorage`/IndexedDB con esteroides |
| **Navigation Compose** | Moverse entre pantallas | React Router |
| **Hilt** | Inyección de dependencias | No hay equivalente directo — se explica en su capítulo |
| **Corrutinas / Flow** | Código asíncrono | Promesas / `async-await` / observables |

No hace falta memorizar esta tabla. Cada pieza tiene su capítulo, y vas a volver acá
cada vez que necesites ubicarte.

## La arquitectura a vuelo de pájaro

La regla de oro del proyecto: **cada pieza tiene UNA responsabilidad, y las capas
no se saltean**. La UI nunca habla directo con la API ni con la base de datos.

```text
┌─ PRESENTATION ─────────────────────────────┐
│  Pantallas (Compose) ←→ ViewModel          │   lo que se ve y el estado
└──────────────────┬─────────────────────────┘
                   │
┌─ DOMAIN ─────────▼─────────────────────────┐
│  UseCases + contratos (interfaces)         │   las reglas del negocio
└──────────────────┬─────────────────────────┘
                   │
┌─ DATA ───────────▼─────────────────────────┐
│  Repositorios → Retrofit (API)             │   de dónde salen los datos
│               → Room (base local)          │
└────────────────────────────────────────────┘
```

¿Por qué tanta ceremonia para una app chica? Porque el objetivo del trabajo práctico
es **aprender la arquitectura**, no solo que la app funcione. En una app real, esta
separación es lo que permite cambiar la API, la base de datos o la UI sin romper
el resto. Se entiende de verdad recién cuando se construye — y eso haremos.

## Checkpoint

- [ ] Puedo nombrar las 5 funcionalidades de la app.
- [ ] Entiendo que Kotlin es el lenguaje y Compose la herramienta de UI (son cosas distintas).
- [ ] Entiendo la idea general: la UI no habla directo con la API ni con la base de datos.

**Siguiente**: [01 — El entorno](/capitulos/01-entorno/)
