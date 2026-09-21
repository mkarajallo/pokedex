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
val resultado: ResultadoLogin = validar(usuario, contrasena)

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
or 'else' branch instead
```

Como la familia está sellada, el compilador tiene la lista completa: cuenta
las ramas del `when`, ve que falta una, y se niega a continuar hasta que se
maneje. Nota que el mensaje ofrece una salida de emergencia: una rama `else`.
Evítala en familias selladas — un `else` "atrapa todo" silencia justamente
esta detección, y la próxima variante olvidada pasará sin aviso. En JavaScript, el `switch` con el caso olvidado corre feliz — y el día
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

## 3. Clean Architecture: el mapa de capas

Con las tres herramientas de Kotlin en la mano, veamos la estructura que el
enunciado exige. Hasta ahora todo el código vive en `MainActivity.kt`: datos,
estado y UI juntos. Clean Architecture corta eso en **tres capas**, y la mejor
forma de entenderlas es un restaurante:

- **`presentation` (el salón)**: lo que el cliente ve y toca — las mesas, el
  menú, el mozo que toma el pedido. En la aplicación: los composables y todo
  lo que rodea a la pantalla. Aquí vive el `LoginIntent` del ejercicio 1 — por
  eso su paquete se llama `presentation.login`.
- **`domain` (la cocina)**: las **recetas** — las reglas del negocio. "Un
  login es válido si..." es una receta. La cocina no sabe cómo es el salón ni
  de dónde vienen los ingredientes: solo sabe cocinar.
- **`data` (el proveedor)**: de dónde salen los ingredientes. Hoy, una
  comparación fija contra `admin` / `123456` — una alacena casera. En el
  capítulo 08, un servidor real.

### La regla de oro

**El salón nunca entra al depósito.** El cliente le pide al mozo, el mozo a la
cocina, la cocina al proveedor. Cada capa habla **solamente con la siguiente**,
y siempre a través de un **contrato** — la `interface` de la sección 2.1.

¿Por qué tanta ceremonia? Repite el experimento mental de la sección 2.1: hoy
las credenciales se comparan contra texto fijo; en el capítulo 08 se
consultarán a un servidor. Con este mapa, ese cambio toca **una sola capa** — `data`. Ni la
cocina ni el salón se enteran, porque la cocina pidió el contrato ("alguien
que sepa `validar`"), y no le importa si quien responde es la alacena casera o
el servidor. Cambia el empleado, no el aviso.

Si en cambio la View le hablara directo a `data`, la lógica de negocio se
filtraría a la UI — exactamente lo que el enunciado prohíbe — y cada cambio de
proveedor sería una cirugía a corazón abierto en la pantalla.

---

## 4. MVVM: quiénes viven en el salón

La capa `presentation` no es una sola pieza: adentro viven **dos**, y ese
reparto tiene nombre propio — **MVVM** (*Model-View-ViewModel*):

- **La View (la mesa y el menú)**: tus composables. Son **tontos a
  propósito**: dibujan lo que les dicen que dibujen, y cuando el usuario toca
  algo no deciden nada — solo **avisan**. ¿Y cómo avisan? Con el
  `LoginIntent`: cada intent es un pedido anotado — "cambió el usuario",
  "cambió la contraseña", "se presionó el botón".
- **El ViewModel (el mozo)**: recibe esos pedidos, decide qué hacer con cada
  uno (si hace falta, va a la cocina), y mantiene el **estado** de la
  pantalla: qué hay escrito en cada campo, si hay que mostrar un error.

En una frase: **la View muestra y avisa; el ViewModel recibe y decide.** Nunca
al revés. Si en la pantalla aparece el texto rojo "Contraseña incorrecta", la
View lo **dibujó**, pero el ViewModel **decidió** que había que mostrarlo.

### El viaje completo de un toque

Todo el mapa, unido. El usuario toca "Iniciar sesión":

1. **View**: no decide nada, solo avisa → entrega `LoginIntent.Enviar` al
   ViewModel.
2. **ViewModel** (mozo): recibe el pedido y se lo lleva a la cocina →
   "valídame este usuario y contraseña".
3. **Domain** (cocina): aplica la receta, y para los ingredientes usa el
   **contrato** → "proveedor, ¿estas credenciales son válidas?".
4. **Data** (proveedor): responde `true` o `false`. La respuesta vuelve por el
   mismo camino hasta el ViewModel.
5. **ViewModel**: con la respuesta, **actualiza el estado** — "hay error,
   muestra tal mensaje".
6. **View**: el estado cambió → la pantalla se **redibuja** sola.

¿El paso 6 suena conocido? Es la **recomposición** del capítulo 05: estado
cambia → Compose redibuja. Todo lo aprendido se conecta en un solo viaje.

### Checkpoint

Tres preguntas para verificar el mapa antes de seguir:

<details>
<summary>1. En el capítulo 08 el login pasará de comparar texto fijo a
consultar un servidor. ¿Qué capas cambian?</summary>

Solo **`data`**. `domain` pidió el contrato (`interface`) y no le importa
quién lo cumpla; `presentation` ni se entera. Cambia el empleado, no el aviso.

</details>

<details>
<summary>2. Aparece en pantalla el error "Contraseña incorrecta". ¿Quién
decidió mostrarlo y quién lo dibujó?</summary>

El **ViewModel decidió** (recibió la respuesta de la cocina y actualizó el
estado); la **View dibujó** (el estado cambió y se recompuso). Cada pieza en
su rol.

</details>

<details>
<summary>3. ¿Puede un composable preguntarle directamente al repositorio de
`data` si el login fue exitoso?</summary>

No. La regla de oro: cada capa habla solo con la siguiente. Si la View
salteara al ViewModel, la lógica de negocio viviría en la UI — lo que el
enunciado prohíbe — y probar o cambiar esa lógica exigiría levantar la
pantalla entera.

</details>

---

## 5. El patrón State + Intent

El ejercicio 1 dejó lista una mitad del patrón: `LoginIntent` modela todo lo
que el usuario puede **hacer** en la pantalla. Esta sección construye la otra
mitad — todo lo que la pantalla puede **mostrar** — y la firma que une ambas.

### 5.1. El estado: una `data class`

¿Qué necesita saber la pantalla de login para dibujarse? Exactamente tres
cosas: qué hay escrito en el campo usuario, qué hay escrito en el campo
contraseña, y si hay un mensaje de error para mostrar... o no. El patrón
State dice: **esas tres cosas viajan juntas, en una sola `data class`** — la
misma `data class` de los Pokémon del capítulo 02:

```kotlin
data class LoginState(
    val usuario: String = "",
    val contrasena: String = "",
    val error: String? = null
)
```

Dos detalles nuevos, con su anatomía:

- **`= ""` — valor por defecto**: si al crear el objeto no pasas ese dato,
  arranca con ese valor. `LoginState()` a secas crea el estado inicial:
  campos vacíos, sin error. Es como los *default parameters* de JavaScript:
  `function f(x = "")`.
- **`String?` vs `String`**: `error` puede ser un texto **o** `null`, y
  `null` significa "no hay error que mostrar" — la ausencia es un estado
  válido, y el `?` la vuelve explícita. `usuario` nunca necesita `null`:
  en el peor caso vale texto vacío `""`.

Un `LoginState` es **un objeto con los valores que la pantalla muestra en
este momento** — una captura del instante. Si el campo de usuario muestra
`adm`, es porque el estado actual es:

```kotlin
LoginState(usuario = "adm", contrasena = "", error = null)
```

### 5.2. La firma que une las dos mitades

El patrón conecta `LoginState` y `LoginIntent` en la **firma de la
pantalla**:

```kotlin
@Composable
fun LoginScreen(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit
)
```

`LoginScreen` recibe dos parámetros:

1. `state` — el objeto con los valores a mostrar.
2. `onIntent` — **una función que le prestan**. Su tipo, `(LoginIntent) ->
   Unit`, se lee: "una función que recibe un `LoginIntent` y no devuelve
   nada". `LoginScreen` no la escribe: se la entrega quien la llama, igual
   que le entrega el `state`.

¿Una función prestada por parámetro? Ya la usaste en el capítulo 05: la
tarjeta con favorito recibía una función del padre, y al tocar el corazón la
tarjeta no decidía nada — llamaba la función que le prestaron, y el padre
cambiaba el estado. `onIntent` es exactamente eso, con un agregado: al
llamarla le pasas **qué pasó** (el intent adentro).

Las dos flechas del patrón:

- **El estado baja** ⬇️: el ViewModel entrega el `state` y la View lo dibuja.
- **Los intents suben** ⬆️: el usuario toca algo y la View llama
  `onIntent(...)` con el aviso. No modifica nada — avisa.

`LoginScreen` no conoce al ViewModel: solo recibe valores para dibujar y una
función para avisar. Eso la hace tonta a propósito — y facilísima de
previsualizar y probar.

### 5.3. La escena en cámara lenta

El campo de usuario muestra `adm`. El usuario tipea la letra `i`. Paso a
paso:

**Paso 1 — La View no puede editar el estado.** Todos los campos de
`LoginState` son `val` — solo lectura, como ya sabes:

```kotlin
state.usuario = "admi"   // ❌ NO COMPILA: usuario es val
```

Android Studio lo subraya en rojo: el lenguaje mismo le prohíbe a la View
modificar el estado. Los estados nuevos los fabrica el ViewModel.

**Paso 2 — Entonces la View solo avisa**, con la variante correspondiente:

```kotlin
onIntent(LoginIntent.CambioUsuario("admi"))
//  ↑         ↑
//  |         el aviso: "cambió el usuario, ahora dice admi"
//  llama a la función prestada y se lo entrega
```

Nota que viaja el **texto completo** (`"admi"`), no la letra tipeada.

**Paso 3 — El ViewModel fabricará el estado nuevo** (con
`usuario = "admi"`) y la pantalla se redibujará. Ese cableado es la próxima
lección; hoy alcanza con el aviso.

### 5.4. La tabla completa: acción → aviso

Cada cosa que el usuario hace en la pantalla tiene su aviso con nombre:

| El usuario... | La View avisa con... |
|---|---|
| tipea en el campo usuario | `LoginIntent.CambioUsuario(el texto nuevo)` |
| tipea en el campo contraseña | `LoginIntent.CambioContrasena(el texto nuevo)` |
| presiona "Iniciar sesión" | `LoginIntent.Enviar` |

El botón usa el `data object`: no lleva datos, solo "ocurrió".

Todo el patrón, en dos líneas:

- **`LoginState`**: los 3 valores que la pantalla **muestra**.
- **`LoginIntent`**: los 3 avisos de lo que el usuario **hizo**.

### Checkpoint

<details>
<summary>1. ¿Por qué <code>error</code> es <code>String?</code> pero
<code>usuario</code> es <code>String</code>?</summary>

Porque "no hay error" es una situación real y válida: `null` la representa.
`usuario` siempre tiene un valor — en el peor caso, `""`.

</details>

<details>
<summary>2. ¿De dónde sale <code>onIntent</code> dentro de
<code>LoginScreen</code>?</summary>

Es un **parámetro**: una función que le entrega quien llama a la pantalla,
igual que el `state`. La View solo la llama; nunca la define ella misma.

</details>

<details>
<summary>3. ¿Cuál de las dos piezas lee la View para dibujarse, y quién lee
los intents?</summary>

La View **lee `LoginState`** para dibujarse. Los `LoginIntent` viajan hacia
el **ViewModel**, que los lee y decide qué hacer con cada uno.

</details>

---

**Anterior**: [05 — Estado y recomposición](/pokedex/capitulos/05-estado/) · **Siguiente**: 07 *(próximamente)*
