---
title: 06 — Login, arquitectura y estado
description: La primera funcionalidad completa - Clean Architecture, MVVM y el patrón State/Intent, con el Kotlin que esos patrones necesitan.
---

**Al terminar este capítulo sabrás**: qué son `interface`, `object` y
`sealed class` en Kotlin, qué problema resuelven Clean Architecture y MVVM,
cómo se comunican la UI y el ViewModel con el patrón **State/Intent**, y habrás
construido la primera funcionalidad real de la Pokédex: la pantalla de login.

> 🚧 **Capítulo en construcción** — se escribe a medida que avanzamos.

---

## 1. El problema: todo vive en la pantalla

Hasta el capítulo 05, toda la aplicación cabe en un archivo: los datos
(`pokebola`), el estado (los favoritos) y la UI (los composables) conviven en
`MainActivity.kt`. Para aprender Compose eso estuvo bien. Para una aplicación
real, no alcanza — y el enunciado del proyecto lo dice explícitamente:

> La validación deberá realizarse mediante la capa correspondiente de la
> arquitectura, evitando colocar lógica de negocio directamente dentro del
> `Composable`.

¿Por qué importa? Imagina que el login vive entero en el composable: la
comparación de usuario y contraseña, el mensaje de error, la decisión de
navegar. El día que la validación cambie (por ejemplo, contra un servidor),
habrá que operar a corazón abierto la UI. Y probar esa lógica exigirá levantar
una pantalla entera para verificar un `if`.

La solución tiene nombre: **separación de responsabilidades**. Cada pieza hace
una sola cosa, y las piezas se comunican por contratos. Este capítulo construye
esa estructura — pero antes necesitamos tres herramientas de Kotlin que aún no
vimos.

---

## 2. Kotlin para arquitectura

Tres construcciones del lenguaje sostienen todo lo que viene: `interface`,
`object` y `sealed class`. Las tres son palabras clave del lenguaje: **no
requieren ningún import**.

### 2.1. `interface`: el contrato

Una interfaz declara **qué** se puede hacer, sin decir **cómo**:

```kotlin
interface RepositorioSesion {
    fun validar(usuario: String, contrasena: String): Boolean
}
```

Esto se lee: "cualquier cosa que sea un `RepositorioSesion` sabe validar un
usuario y una contraseña, y responde con un `Boolean`". Nota que `validar` no
tiene cuerpo — la interfaz solo declara la firma.

Quien cumple el contrato es una **clase que la implementa**, usando `:` (los
dos puntos que en Kotlin significan "es un") y la palabra clave `override`:

```kotlin
class RepositorioSesionLocal : RepositorioSesion {
    override fun validar(usuario: String, contrasena: String): Boolean {
        return usuario == "admin" && contrasena == "123456"
    }
}
```

- `: RepositorioSesion` — esta clase **es un** `RepositorioSesion` y promete
  cumplir su contrato completo.
- `override` — marca que esta función implementa una declarada en la interfaz.
  Es obligatoria: si se omite, el código no compila.

**El equivalente web**: si conoces TypeScript, es su `interface` casi calcada.
La diferencia de fondo no es sintáctica sino de uso: aquí las interfaces son la
frontera entre capas de la arquitectura. El que **usa** un `RepositorioSesion`
no sabe (ni le importa) si detrás hay una comparación fija, una base de datos o
un servidor: solo conoce el contrato. Mañana se cambia la implementación y el
resto de la aplicación ni se entera. Ese es el truco que hace todo lo demás
posible.

### 2.2. `object`: la instancia única

En el capítulo 02 vimos que una clase es un molde: con `Pokemon(...)` se crean
tantas instancias como se quiera. A veces se necesita lo contrario: algo que
exista **una sola vez** en toda la aplicación. Para eso Kotlin tiene una
palabra clave propia:

```kotlin
object Sesion {
    var usuarioActual: String? = null
}
```

`object` declara la clase **y** crea su única instancia en el mismo acto. No se
usa `Sesion()` — no hay constructor que llamar — sino directamente
`Sesion.usuarioActual`. Es el patrón *singleton* del que quizá oíste hablar,
convertido en palabra clave del lenguaje.

Existe también la variante `data object`: igual que `object`, pero con la
impresión legible de las `data class` (al registrarlo en un log se ve `Sesion`
en lugar de `Sesion@4f2b`). Se usa cuando el objeto representa un **valor** —
en la próxima sección aparecerá su uso estrella.

### 2.3. `sealed class`: las alternativas cerradas

El tercer concepto es el más nuevo y el más importante del capítulo. Piensa en
el resultado de un login: solo hay dos posibilidades — funcionó, o falló con un
motivo. Una `sealed class` (clase **sellada**) permite decirle eso al
compilador: "estas son TODAS las variantes que existen, no hay otras".

```kotlin
sealed class ResultadoLogin {
    data object Exito : ResultadoLogin()
    data class Error(val mensaje: String) : ResultadoLogin()
}
```

Leámoslo por partes:

- `sealed class ResultadoLogin` — declara la familia. "Sellada" significa que
  **nadie fuera de este archivo puede agregar variantes**.
- `data object Exito : ResultadoLogin()` — la variante éxito. Es un
  `data object` porque no lleva datos: el éxito es uno solo, no necesita
  instancias distintas.
- `data class Error(val mensaje: String) : ResultadoLogin()` — la variante
  error. Es una `data class` porque **sí** lleva datos: cada error tiene su
  mensaje.

La recompensa llega al consumir el valor con `when` (el pariente poderoso del
`switch` de JavaScript, visto en el capítulo 02):

```kotlin
val resultado: ResultadoLogin = repositorio.login(usuario, contrasena)

when (resultado) {
    is ResultadoLogin.Exito -> irAPantallaPrincipal()
    is ResultadoLogin.Error -> mostrarMensaje(resultado.mensaje)
}
```

Dos cosas notables:

- **`when` es exhaustivo**: como la familia está sellada, el compilador conoce
  todas las variantes. Si mañana se agrega `data object SesionExpirada` y algún
  `when` no la contempla, **ese código deja de compilar**. El compilador
  encuentra el olvido antes que el usuario.
- **Cast inteligente**: dentro de la rama `is ResultadoLogin.Error`, Kotlin ya
  sabe que `resultado` es un `Error`, así que `resultado.mensaje` funciona sin
  conversión manual.

**El equivalente web**: en TypeScript esto se modela con una *discriminated
union* (`{ tipo: "exito" } | { tipo: "error"; mensaje: string }`) y un `switch`
sobre `tipo`. La `sealed class` es esa misma idea con soporte total del
compilador. En JavaScript puro, el equivalente honesto es una cadena de `if`
sobre un campo `tipo`... y ninguna ayuda si olvidas un caso.

> 💡 **Por qué importa tanto**: el patrón State/Intent del proyecto modela
> **las acciones del usuario** como una `sealed class` — escribir en el campo
> usuario, escribir en la contraseña, presionar el botón. El conjunto de cosas
> que el usuario puede hacer en una pantalla es finito y conocido: exactamente
> lo que una clase sellada expresa.

---

## Ejercicio 1 — Modelar las acciones del login

La pantalla de login del proyecto permite exactamente tres acciones del
usuario:

1. Escribir en el campo de usuario (llega el texto nuevo).
2. Escribir en el campo de contraseña (llega el texto nuevo).
3. Presionar el botón de iniciar sesión (no lleva datos).

**Tu tarea**: modela una `sealed class LoginIntent` con una variante por
acción, eligiendo para cada una entre `data class` y `data object` según lleve
datos o no. Este código será, sin cambios, la primera pieza real del login de
la aplicación.

*(La solución se agregará aquí después de resolverlo.)*

---

**Anterior**: [05 — Estado y recomposición](/pokedex/capitulos/05-estado/) · **Siguiente**: 07 *(próximamente)*
