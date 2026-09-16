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
| **Gradle** | El sistema de build de Android: gestiona dependencias (como `npm`) Y compila/empaqueta la app en una APK (como un bundler tipo Vite, pero obligatorio). |
| **Gradle sync** | Equivalente a `npm install` + indexado: descarga dependencias y prepara el proyecto para el IDE. Corre al crear el proyecto o al tocar un `build.gradle.kts`. |
| **Gradle wrapper (`gradlew`)** | Script que descarga y usa la versión exacta de Gradle que el proyecto declara. El build es igual en cualquier máquina (como fijar la versión de node). |
| **IDE** | Entorno de desarrollo integrado. Para Android: Android Studio. |
| **KVM** | Virtualización por hardware en Linux. Sin esto, el emulador es inusablemente lento. |
| **LazyColumn** | Lista vertical con scroll que compone solo los elementos visibles (virtualización, como `react-window` en React). Sus elementos se describen con `items(lista) { ... }`. |
| **Material Design** | El sistema de diseño de Google: componentes con apariencia y comportamiento ya resueltos — el rol de Bootstrap/MUI en la web. En Compose vive en `androidx.compose.material3`. |
| **Modifier** | El "atributo `style`" de Compose: ajusta apariencia, tamaño y posición encadenando llamadas (`Modifier.fillMaxSize().padding(16.dp)`). Los componentes lo reciben por parámetro para poder ser ajustados desde afuera, como `className` en React. |
| **Módulo** | Unidad compilable de un proyecto Gradle. En este proyecto hay uno solo: `app/`. Todo el código va adentro. |
| **@Preview** | Anotación que le pide a Android Studio renderizar un composable en el editor, sin emulador. El Storybook incorporado de Compose. |
| **res/** | Carpeta de recursos no-código: íconos, textos, colores. Android elige automáticamente la versión correcta según el dispositivo. |
| **Scaffold** | El "andamio" de una pantalla Material: esqueleto con huecos para barra superior, inferior y botón flotante, que además entrega el `innerPadding` que ocupan las barras del sistema. Como el template base de un sitio web (header/main/footer). |
| **SDK (Android SDK)** | El kit de desarrollo: librerías, compiladores y herramientas para construir apps Android. Lo instala el Setup Wizard. |
| **sp** | Unidad para tamaños de texto: como `dp`, pero además escala con el tamaño de fuente que el usuario configuró en su teléfono (accesibilidad). |
