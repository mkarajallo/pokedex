---
title: 01 — El entorno
description: Instalar Android Studio, crear el proyecto Pokedex y entender qué generó.
---

**Al terminar este capítulo vas a tener**: Android Studio instalado, el proyecto
`Pokedex` creado, y la app de plantilla corriendo en un emulador. Además vas a
entender qué es cada archivo que el proyecto trae generado.

---

## 1. Instalar Android Studio (Ubuntu/Linux)

Android Studio es el IDE oficial de Android (basado en IntelliJ). Se instala con el
**tarball oficial**, extraído en el home del usuario:

```bash
# 1. Descargar el .tar.gz desde https://developer.android.com/studio

# 2. Extraer en el home (ajustar el nombre del archivo)
mkdir -p ~/Apps
tar -xzf ~/Descargas/android-studio-*.tar.gz -C ~/Apps

# 3. Lanzar
~/Apps/android-studio/bin/studio.sh
```

**¿Por qué el tarball y no snap o apt?** Es el método que documenta Google, y al
vivir en el home se actualiza solo, sin sudo. El snap lo mantiene la comunidad y
suele traer problemas de permisos.

**¿Por qué no hace falta "instalarlo" en el sistema?** En Linux, el acceso directo
del menú es un archivo `.desktop` que apunta al ejecutable, esté donde esté. Android
Studio lo crea por vos: **Tools → Create Desktop Entry**.

### El Setup Wizard

Al abrirlo por primera vez corre un asistente:

1. Elegir instalación **Standard** — descarga el **SDK** de Android (las librerías
   y herramientas para compilar apps), el **emulador** y las build tools. Son varios GB.
2. Aceptar las licencias.

> **Requisito para que el emulador vuele**: la CPU debe soportar virtualización y
> el sistema tener **KVM** disponible. Se verifica con: `[ -e /dev/kvm ] && echo OK`

## 2. Crear el proyecto

En la pantalla de bienvenida: **New Project → Empty Activity** (la plantilla de
Jetpack Compose).

| Campo | Valor | Por qué |
|---|---|---|
| Template | **Empty Activity** | Es la plantilla Compose. ⚠️ NO confundir con "Empty **Views** Activity", que usa el sistema viejo de XML. |
| Name | `Pokedex` | El nombre visible de la app. |
| Package name | `com.mkarajallo.pokedex` | Identificador único de la app (convención: dominio invertido). Como el `name` de `package.json`, pero global. |
| Minimum SDK | **API 26** | La versión más vieja de Android soportada. API 26 cubre ~98% de los dispositivos. |
| Build configuration | **Kotlin DSL** | Los archivos de build se escriben en Kotlin (`.kts`) en vez del viejo Groovy. |

Al darle **Finish**, Gradle hace su **primera sincronización**: descarga el propio
Gradle, los plugins y todas las dependencias. La primera vez tarda MUCHO (como el
primer `npm install` de un monorepo gigante). Las siguientes usan caché y son rápidas.

## 3. Qué generó la plantilla

Lo esencial del proyecto generado (omitiendo carpetas de caché y builds):

```text
pokedex/
├── settings.gradle.kts        ← qué módulos componen el proyecto
├── build.gradle.kts           ← configuración de build a nivel proyecto
├── gradle/
│   └── libs.versions.toml     ← catálogo de versiones de dependencias
├── gradlew                    ← el Gradle wrapper (build sin instalar nada)
└── app/                       ← EL MÓDULO DE LA APP: acá vive todo tu código
    ├── build.gradle.kts       ← dependencias y configuración DE LA APP
    └── src/main/
        ├── AndroidManifest.xml            ← la "cédula de identidad" de la app
        ├── java/com/mkarajallo/pokedex/   ← el código Kotlin
        │   ├── MainActivity.kt            ← el punto de entrada
        │   └── ui/theme/                  ← colores, tipografía, tema
        └── res/                           ← recursos: íconos, strings, etc.
```

Las piezas que importan hoy:

| Archivo | Qué es |
|---|---|
| `app/` | Un **módulo**: una unidad compilable. Los proyectos chicos tienen uno solo. Tu código SIEMPRE va acá adentro. |
| `app/build.gradle.kts` | El `package.json` de la app: acá vamos a agregar Retrofit, Room, Hilt... |
| `gradle/libs.versions.toml` | **Catálogo de versiones**: todas las versiones de dependencias en un solo lugar, para no repetirlas por el proyecto. |
| `AndroidManifest.xml` | Declara ante Android qué contiene la app: sus pantallas (Activities), permisos (internet, cámara...), ícono y nombre. Sin declaración acá, no existe para el sistema. |
| `MainActivity.kt` | El punto de entrada. Una **Activity** es una "ventana" de la app; en apps Compose modernas hay UNA sola, y las "pantallas" se manejan por navegación adentro. |
| `res/` | Recursos no-código: íconos en varias resoluciones (`mipmap-*`), textos (`values/strings.xml`), etc. |

### El código que ya corre: `MainActivity.kt`

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()              // ← la app puede dibujar hasta los bordes de la pantalla
        setContent {                    // ← "montá esta UI" (como el render de React)
            PokedexTheme {              // ← el tema envuelve todo
                Scaffold { innerPadding ->
                    Greeting("Android", Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable                             // ← marca una función que DIBUJA UI
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
```

Si venís de React, esto te va a sonar: `Greeting` es un **componente** — una función
que recibe datos y devuelve UI. En Compose se llaman **composables** y se marcan con
`@Composable`. `setContent { }` es el equivalente a `root.render(<App/>)`.
Los detalles del lenguaje (qué es `class`, `override`, `fun`) son el tema del capítulo 02.

## 4. Correr la app por primera vez

Hay dos formas de ejecutar la app: un **emulador** (teléfono virtual) o un
**dispositivo físico** por USB. Conviene conocer ambas.

### Opción A — Emulador

1. **Device Manager** (Tools → Device Manager) → **＋ Create Virtual Device**.
2. Elegir un modelo (por ejemplo Pixel 8) y una **imagen del sistema** (la versión
   de Android que correrá el virtual) — la recomendada más reciente.
3. Finish → seleccionar el emulador en la barra superior → **▶ Run**.

Notas:

- El **primer boot es el más lento**; los siguientes arrancan desde un snapshot.
- Consume bastante RAM y CPU — es un teléfono entero virtualizado.
- Por defecto corre incrustado en el IDE; para ventana propia:
  Settings → Tools → Emulator → destildar *"Launch in the Running Devices tool window"*.

### Opción B — Teléfono físico por USB (recomendada para el día a día)

1. En el teléfono: **Ajustes → Información del teléfono** → tocar
   **"Número de compilación" 7 veces** → se desbloquean las Opciones de desarrollador.
2. **Opciones de desarrollador → Depuración por USB: ON**.
3. Conectar por cable → aceptar el diálogo *"¿Permitir depuración USB?"*.
4. El teléfono aparece en el selector de dispositivos de Android Studio → **▶ Run**.

¿Por qué es mejor para desarrollar? No consume recursos de la PC, la app corre a
velocidad real y se prueba sobre hardware de verdad. El emulador queda para probar
versiones de Android o pantallas que no se tienen físicamente.

### Qué pasa cuando apretás Run

Gradle compila el código Kotlin → lo empaqueta en una **APK** → la instala en el
dispositivo (vía **ADB**, el puente de comunicación entre la PC y el teléfono) →
lanza la `MainActivity`. Ese `Hello Android!` en pantalla es el checkpoint final
del capítulo.

## Checkpoint

- [ ] Android Studio abre desde el menú de aplicaciones.
- [ ] El proyecto `Pokedex` existe y Gradle sincronizó sin errores.
- [ ] Sé qué es el SDK, qué es Gradle y qué hace el Gradle sync.
- [ ] La app de plantilla corre en el emulador o en un teléfono físico.

**Anterior**: [00 — Introducción](/pokedex/capitulos/00-introduccion/) · **Siguiente**: [02 — Kotlin esencial](/pokedex/capitulos/02-kotlin-esencial/)
