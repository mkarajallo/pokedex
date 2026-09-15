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
| **Emulador** | Un Android virtual corriendo en tu PC para probar la app sin un teléfono físico. |
| **Gradle** | El sistema de build de Android: gestiona dependencias (como `npm`) Y compila/empaqueta la app en una APK (como un bundler tipo Vite, pero obligatorio). |
| **Gradle sync** | Equivalente a `npm install` + indexado: descarga dependencias y prepara el proyecto para el IDE. Corre al crear el proyecto o al tocar un `build.gradle.kts`. |
| **Gradle wrapper (`gradlew`)** | Script que descarga y usa la versión exacta de Gradle que el proyecto declara. El build es igual en cualquier máquina (como fijar la versión de node). |
| **IDE** | Entorno de desarrollo integrado. Para Android: Android Studio. |
| **KVM** | Virtualización por hardware en Linux. Sin esto, el emulador es inusablemente lento. |
| **Módulo** | Unidad compilable de un proyecto Gradle. En este proyecto hay uno solo: `app/`. Todo el código va adentro. |
| **res/** | Carpeta de recursos no-código: íconos, textos, colores. Android elige automáticamente la versión correcta según el dispositivo. |
| **SDK (Android SDK)** | El kit de desarrollo: librerías, compiladores y herramientas para construir apps Android. Lo instala el Setup Wizard. |
