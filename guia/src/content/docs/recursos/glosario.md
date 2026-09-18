---
title: Glosario
description: Todos los términos de la guía, con su equivalente del mundo web cuando existe.
---

Todos los términos de la guía, en orden alfabético, con su equivalente del mundo web
cuando existe. Se amplía capítulo a capítulo.

| Término | Qué es |
|---|---|
| **ADB (Android Debug Bridge)** | El puente de comunicación entre la PC y el dispositivo (físico o emulado): instala la APK, transmite logs, ejecuta comandos. |
| **Activity** | Una "ventana" de la app a nivel sistema operativo. En apps Compose modernas hay una sola (`MainActivity`) y las pantallas se manejan con navegación interna. |
| **AndroidManifest.xml** | La "cédula de identidad" de la app: declara ante Android sus Activities, permisos, ícono y nombre. |
| **APK** | El paquete instalable de Android — el "ejecutable" final que produce el build. |
| **@Composable** | Anotación que marca una función que dibuja UI. El equivalente de un componente de React: recibe datos, devuelve interfaz. |
| **build.gradle.kts** | Equivalente al `package.json`: declara dependencias y configuración. Hay uno por proyecto y uno por módulo (`app/`). |
| **Card** | Contenedor Material con fondo, bordes redondeados y elevación (sombra). Un `div` con `border-radius` + `box-shadow`, pero integrado al tema. |
| **dp** | La unidad de medida de la UI en Android (*density-independent pixels*): como el `px` de CSS, pero se adapta a la densidad de cada pantalla para verse del mismo tamaño físico en cualquier dispositivo. |
| **Emulador** | Un Android virtual corriendo en tu PC para probar la app sin un teléfono físico. |
| **Estado** | Datos que cambian mientras la app corre y que la UI debe reflejar (un contador, un favorito, un texto de búsqueda). En Compose vive en cajas observables creadas con `mutableStateOf`. |
| **Gradle** | El sistema de build de Android: gestiona dependencias (como `npm`) Y compila/empaqueta la app en una APK (como un bundler tipo Vite, pero obligatorio). |
| **Gradle sync** | Equivalente a `npm install` + indexado: descarga dependencias y prepara el proyecto para el IDE. Corre al crear el proyecto o al tocar un `build.gradle.kts`. |
| **Gradle wrapper (`gradlew`)** | Script que descarga y usa la versión exacta de Gradle que el proyecto declara. El build es igual en cualquier máquina (como fijar la versión de node). |
| **Icon** | Composable que dibuja un icono vectorial. Recibe `imageVector` (qué dibujo, del catálogo `Icons.Filled.*` — como un SVG) y `contentDescription` (el texto para lectores de pantalla — el `alt` de HTML). |
| **IconButton** | Botón circular pensado para contener un `Icon`, con efecto de toque incluido. `onClick` recibe el comportamiento; su trailing lambda, el icono a dibujar. |
| **IDE** | Entorno de desarrollo integrado. Para Android: Android Studio. |
| **KVM** | Virtualización por hardware en Linux. Sin esto, el emulador es inusablemente lento. |
| **Lambda** | Función sin nombre usada como valor: `{ clicks++ }` o `{ pokemon -> ... }`. La *arrow function* de JavaScript con llaves en lugar de flecha. Sirve para pasar comportamiento como argumento (`onClick = { ... }`). |
| **LazyColumn** | Lista vertical con scroll que compone solo los elementos visibles (virtualización, como `react-window` en React). Sus elementos se describen con `items(lista) { ... }`. Las tarjetas que salen de pantalla **dejan de existir** — se destruyen junto con su estado local. |
| **Material Design** | El sistema de diseño de Google: componentes con apariencia y comportamiento ya resueltos — el rol de Bootstrap/MUI en la web. En Compose vive en `androidx.compose.material3`. |
| **Modifier** | El "atributo `style`" de Compose: ajusta apariencia, tamaño y posición encadenando llamadas (`Modifier.fillMaxSize().padding(16.dp)`). Los componentes lo reciben por parámetro para poder ser ajustados desde afuera, como `className` en React. |
| **Módulo** | Unidad compilable de un proyecto Gradle. En este proyecto hay uno solo: `app/`. Todo el código va adentro. |
| **mutableStateOf** | Crea una caja observable con un valor inicial. Cuando se le asigna un valor nuevo, Compose se entera y recompone lo que la lee. La mitad del `useState` de React. |
| **@Preview** | Anotación que le pide a Android Studio renderizar un composable en el editor, sin emulador. El Storybook incorporado de Compose. |
| **Recomposición** | El redibujado inteligente de Compose: cuando un estado cambia, se vuelven a ejecutar **solo** los composables que lo leen. El re-render de React. |
| **remember** | Conserva un valor entre recomposiciones: la primera vez ejecuta su lambda y guarda el resultado; después devuelve siempre lo guardado. Sin él, cada recomposición reiniciaría el estado. |
| **res/** | Carpeta de recursos no-código: íconos, textos, colores. Android elige automáticamente la versión correcta según el dispositivo. |
| **Scaffold** | El "andamio" de una pantalla Material: esqueleto con huecos para barra superior, inferior y botón flotante, que además entrega el `innerPadding` que ocupan las barras del sistema. Como el template base de un sitio web (header/main/footer). |
| **SDK (Android SDK)** | El kit de desarrollo: librerías, compiladores y herramientas para construir apps Android. Lo instala el Setup Wizard. |
| **Set** | Colección sin elementos repetidos (el `Set` de JavaScript). `numero in favoritos` pregunta si está; `favoritos + numero` y `favoritos - numero` devuelven un conjunto **nuevo** con el elemento agregado o quitado. |
| **sp** | Unidad para tamaños de texto: como `dp`, pero además escala con el tamaño de fuente que el usuario configuró en su teléfono (accesibilidad). |
| **State hoisting** | Subir el estado desde un componente hijo hacia un ancestro que viva más y que todos compartan. El hijo queda sin estado: recibe el valor y avisa eventos por callback. "El estado baja, los eventos suben" — el *lift state up* de React. |
| **Trailing lambda** | Regla de Kotlin: si el último parámetro de una función es una lambda, puede escribirse fuera del paréntesis. `Column { ... }` es `Column(content = { ... })` — el `content` es los *children* de React. |
| **Unit** | El tipo que significa "no devuelvo nada útil" — el `void` de otros lenguajes o el `undefined` implícito de JS. `() -> Unit` describe una función sin parámetros ni retorno: un callback. |
