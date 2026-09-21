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

Piensa en una interfaz como un **aviso de trabajo**:

```kotlin
interface RepositorioSesion {
    fun validar(usuario: String, contrasena: String): Boolean
}
```

Esto es el aviso: "se busca alguien que sepa `validar`: recibe usuario y
contraseña, responde un `Boolean`". Por eso `validar` **no tiene cuerpo** — el
aviso no hace el trabajo, solo lo describe.

Ahora, alguien toma el trabajo:

```kotlin
class RepositorioSesionLocal : RepositorioSesion {
    override fun validar(usuario: String, contrasena: String): Boolean {
        return usuario == "admin" && contrasena == "123456"
    }
}
```

Tres pasos para leerlo:

1. **`: RepositorioSesion` — la firma del contrato.** La clase dice "yo tomo
   ese trabajo". Desde ese momento el compilador se pone estricto: firmaste,
   ahora estás **obligado** a saber hacer todo lo que el aviso pedía.
2. **`override` — el cumplimiento.** Significa: "esta función no es un invento
   mío, es mi cumplimiento de lo que el contrato pedía". La firma es idéntica a
   la del aviso (mismo nombre, mismos parámetros, mismo `Boolean`), pero ahora
   sí tiene cuerpo: el trabajo hecho de verdad.
3. **¿Y si la clase firma pero no escribe `validar`?** No compila. El que se
   queja es el **compilador** — subrayado rojo en Android Studio — nunca el
   usuario con la app corriendo. Es el mismo patrón de null safety: Kotlin
   convierte errores de ejecución en errores de compilación.

¿Por qué escribir la palabra `override` es obligatorio? Doble seguro: quien lee
el código sabe al instante que esa función viene de un contrato, y el
compilador verifica que la firma coincida con el aviso — un error de una letra
en el nombre se detecta en el acto.

**La recompensa: quien usa el repositorio pide el contrato, no la clase.**
Cuando construyamos el ViewModel del login, va a pedir esto:

```kotlin
class LoginViewModel(
    private val repositorio: RepositorioSesion  // pide el CONTRATO, no la clase
)
```

El tipo del parámetro es `RepositorioSesion` — el aviso, no
`RepositorioSesionLocal`. El ViewModel dice: "dame cualquier cosa que sepa
validar; no me importa quién sea ni cómo lo haga". Como al empleador del aviso:
le importa que el trabajo se **haga**, no quién lo hace.

Ese es el truco que sostiene toda la arquitectura. Hoy el repositorio será una
comparación fija (`admin` / `123456`); en el capítulo 08 será uno que consulta
la API real — y el `LoginViewModel` no cambiará **ni una línea**, porque
depende del contrato, y el contrato no cambia. Cambia el empleado, no el aviso.

> 💡 Si conoces TypeScript: es su `interface`, casi calcada. Si solo conoces
> JavaScript, no busques el equivalente — no existe, y esa es justamente la
> novedad: un contrato que el compilador hace cumplir.

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

**El equivalente web**: es como exportar un objeto literal desde un módulo de
JavaScript (`export const sesion = { usuarioActual: null }`) — todos los que
lo importan tocan el mismo objeto. Aquí es palabra clave del lenguaje.

La pregunta para decidir entre `class` y `object` es una sola: **¿necesito
varios, o necesito exactamente uno?** Varios Pokémon → `class`. Una sola
sesión en toda la aplicación → `object`.

Existe también la variante `data object`: igual que `object`, pero con la
impresión legible de las `data class` (al registrarlo en un log se ve `Sesion`
en lugar de `Sesion@4f2b`). En la próxima sección aparecerá su uso estrella.

### 2.3. `sealed class`: las alternativas cerradas

El tercer concepto es el más importante del capítulo. Piensa en el resultado
de un login: solo hay dos posibilidades — funcionó, o falló con un motivo. No
hay una tercera. En JavaScript esto se modela a mano, con un campo `tipo`:

```js
{ tipo: "exito" }
{ tipo: "error", mensaje: "Contraseña incorrecta" }
```

Funciona... pero nada impide escribir `tipo: "exitoo"` con un error de tipeo,
ni olvidar manejar un caso. Una `sealed class` (clase **sellada**) permite
declarar el conjunto completo de posibilidades como algo que el compilador
**conoce**:

```kotlin
sealed class ResultadoLogin {
    data object Exito : ResultadoLogin()
    data class Error(val mensaje: String) : ResultadoLogin()
}
```

Léelo con lo que ya sabes — todas las piezas son conocidas:

- `sealed class ResultadoLogin` — declara la familia. "Sellada" significa que
  estas variantes son **todas**: nadie puede agregar otra desde afuera.
- Cada variante **firma con `:`** que pertenece a la familia — el mismo `:` de
  "es un" que viste con la interfaz. `Exito` es un `ResultadoLogin`; `Error`
  es un `ResultadoLogin`.
- Para cada variante, la regla de la sección anterior, refinada: **¿la
  variante lleva datos que cambian?** Todos los éxitos son idénticos — no
  llevan datos — así que alcanza con una única instancia: `data object`. Cada
  error es distinto — lleva su mensaje — así que hay que fabricar instancias:
  `data class`.

#### La recompensa: `when` exhaustivo

El conjunto cerrado se consume con `when` (el pariente del `switch` de
JavaScript, visto en el capítulo 02):

```kotlin
when (resultado) {
    is ResultadoLogin.Exito -> irAPantallaPrincipal()
    is ResultadoLogin.Error -> mostrarMensaje(resultado.mensaje)
}
```

Ahora, la situación que muestra por qué esto vale la pena. Mañana la familia
gana una variante nueva:

```kotlin
    data object SesionExpirada : ResultadoLogin()
```

...y nadie se acuerda de tocar aquel `when`. ¿Qué pasa al compilar? **El
proyecto deja de compilar.** Android Studio subraya el `when` en rojo:

```text
'when' expression must be exhaustive, add necessary 'is SesionExpirada' branch
```

Como la familia está sellada, el compilador tiene la lista completa: cuenta
las ramas del `when`, ve que falta una, y se niega a continuar hasta que se
maneje. En JavaScript, el `switch` con el caso olvidado corre feliz — y el día
que llega el caso nuevo, la pantalla no hace nada o explota, en producción.

El segundo regalo es el **cast inteligente**: dentro de la rama
`is ResultadoLogin.Error`, Kotlin ya sabe que `resultado` es un `Error`, así
que `resultado.mensaje` funciona directo, sin conversión manual.

> 💡 **El patrón que se repite — y qué es "compilar"**
>
> JavaScript no tiene paso de compilación: el navegador lee el código y lo va
> ejecutando; los errores aparecen en vivo, con la aplicación corriendo.
> Kotlin primero **traduce todo el código** a algo que el teléfono entiende —
> eso es compilar — y durante esa traducción **analiza todo**: tipos, nulls,
> contratos, casos del `when`. Si algo no cierra, se niega a producir la
> aplicación.
>
> Por eso el mismo patrón aparece una y otra vez:
>
> - Null safety → el null olvidado lo encuentra el compilador.
> - Interfaz → el contrato incumplido lo encuentra el compilador.
> - Sealed + `when` → el caso olvidado lo encuentra el compilador.
>
> Kotlin convierte errores **de ejecución** (los sufre el usuario) en errores
> **de compilación** (los ves tú, antes de que la aplicación exista).
>
> Si conoces TypeScript: la `sealed class` es su *discriminated union* con
> soporte total del compilador.

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

**Tu tarea**: crea el paquete `presentation.login` (clic derecho sobre
`com.mkarajallo.pokedex` → New → Package), dentro un archivo `LoginIntent.kt`,
y modela una `sealed class LoginIntent` con una variante por acción, eligiendo
para cada una entre `data class` y `data object` según lleve datos o no. Este
código será, sin cambios, la primera pieza real del login de la aplicación.

<details>
<summary>Solución (intenta resolverlo antes de abrir)</summary>

```kotlin
package com.mkarajallo.pokedex.presentation.login

sealed class LoginIntent {
    data class CambioUsuario(val valor: String) : LoginIntent()
    data class CambioContrasena(val valor: String) : LoginIntent()
    data object Enviar : LoginIntent()
}
```

Errores frecuentes en este ejercicio, vistos en batalla:

- **Firmar con una familia inventada** (`data object User : Usuario()`): a la
  derecha del `:` va siempre el nombre de la **familia** — todas las variantes
  terminan en `: LoginIntent()`, porque todas *son* acciones del login. A la
  izquierda va el nombre de la variante.
- **Invertir la regla de los datos**: los campos de texto entregan el texto
  nuevo → llevan datos → `data class(val valor: String)`. El botón solo
  "ocurrió" → sin datos → `data object`.
- **`data class` sin parámetros** (`data class CambioUsuario :
  LoginIntent()`): una `data class` exige al menos un parámetro — sin él, el
  texto del usuario no tendría por dónde viajar. Error literal del compilador:
  `Data class must have at least one primary constructor parameter`.
- **`data object` con parámetros** (`data object Enviar(val mensaje: String)`):
  un `object` es la instancia única que ya nació — no tiene constructor, así
  que no puede llevar paréntesis. No compila.
- **Nombres que describen el widget o una orden** (`addUser`, `Buttom`): las
  variantes se nombran en PascalCase y describen **lo que pasó**: cambió el
  usuario, cambió la contraseña, se envió.

</details>

---

**Anterior**: [05 — Estado y recomposición](/pokedex/capitulos/05-estado/) · **Siguiente**: 07 *(próximamente)*
