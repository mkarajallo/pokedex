---
title: 05 — Estado y recomposición
description: Cómo una pantalla de Compose reacciona a la interacción - remember, mutableStateOf y la recomposición.
---

**Al terminar este capítulo sabrás**: qué es el estado en Compose, por qué la
pantalla se redibuja sola cuando el estado cambia (**recomposición**), y cómo
hacer que la Pokédex reaccione a la interacción del usuario.

---

## 1. El problema: la pantalla no reacciona

Hasta ahora la Pokédex es una foto. Muy linda, con tarjetas Material y una
`LazyColumn`, pero una foto: se dibuja una vez con los datos de `pokebola` y
nada de lo que haga el usuario la cambia.

En el capítulo 03 quedó establecido que **UI = f(datos)**. La pregunta de este
capítulo es: ¿qué pasa cuando los datos **cambian mientras la app corre**?
Un contador que suma, un buscador que filtra, un favorito que se marca — todo
eso es la misma idea: datos que cambian y una UI que debe reflejarlos.

El primer instinto es usar una variable común. Veamos por qué **no funciona**:

```kotlin
@Composable
fun Contador(modifier: Modifier = Modifier) {
    var clicks = 0    // ← variable común: Compose no la observa

    Column(modifier = modifier) {
        Text("Clicks: $clicks")
        Button(onClick = { clicks++ }) {   // ← se ejecuta al tocar el botón
            Text("Sumar")
        }
    }
}
```

Dos piezas nuevas antes de ver el problema:

- **`Button`**: un composable de Material que recibe `onClick`, una lambda que
  se ejecuta cada vez que el usuario lo toca. Es el `onClick` de React con otro
  sombrero. El texto del botón va **adentro**, como hijo (`content`), no como
  parámetro.
- El botón **sí** ejecuta la lambda: `clicks` pasa a valer 1, 2, 3...

> 💡 **Recordatorio: la lambda, la pieza que sostiene todo esto**
>
> Una lambda es una **función sin nombre usada como valor** — la arrow
> function de JavaScript, con llaves en lugar de flecha:
> `{ clicks++ }` equivale a `() => clicks++`, y
> `{ pokemon -> ... }` equivale a `(pokemon) => ...`.
>
> Sirve para pasar **comportamiento** como argumento: `onClick = { clicks++ }`
> significa "cuando ocurra el toque, ejecuta esto" — nadie la ejecuta al
> escribirla.
>
> La regla clave es la **trailing lambda**: si el último parámetro de una
> función es una lambda, puede escribirse fuera del paréntesis. Por eso
> `Button(onClick = { ... }) { Text("Sumar") }` es exactamente
> `Button(onClick = { ... }, content = { Text("Sumar") })`: las llaves finales
> son el parámetro `content` — los *children* de React. `Column`, `Row`,
> `Card`, `Button` y `IconButton` declaran `content` y aceptan hijos;
> `Text` e `Icon` son **hojas**: no lo declaran, y darles una lambda de
> contenido no compila.
>
> Dos detalles más: el nombre del parámetro de **tu** lambda lo eliges tú
> (`{ innerPadding -> }` podría llamarse `{ padding -> }`), mientras que los
> nombres de parámetros de la función llamada (`onClick`, `content`) los fijó
> su autor. Y si la lambda tiene un solo parámetro sin declarar, Kotlin lo
> nombra `it` automáticamente.

Y sin embargo, en pantalla dice `Clicks: 0` para siempre. ¿Por qué? Porque
`Text("Clicks: $clicks")` se dibujó **una vez**, cuando la función se ejecutó.
Cambiar una variable común no le avisa nada a Compose: nadie vuelve a ejecutar
la función, nadie vuelve a dibujar el texto.

Si vienes de React, ya viviste esto: modificar una variable local en un
componente no re-renderiza nada. Para eso existe `useState`. Compose tiene su
equivalente exacto.

## 2. La solución: estado observable

```kotlin
@Composable
fun Contador(modifier: Modifier = Modifier) {
    var clicks by remember { mutableStateOf(0) }   // ← estado observable

    Column(modifier = modifier) {
        Text("Clicks: $clicks")
        Button(onClick = { clicks++ }) {
            Text("Sumar")
        }
    }
}
```

Una línea cambió. Ahora, al tocar el botón, el texto se actualiza. Esa línea
tiene tres piezas, y cada una responde una pregunta distinta:

| Pieza | Qué hace | Pregunta que responde |
|---|---|---|
| `mutableStateOf(0)` | Crea una **caja observable** con valor inicial `0`. Cuando su contenido cambia, Compose se entera. | ¿Cómo se entera Compose de un cambio? |
| `remember { }` | Conserva la caja entre redibujados: la crea la primera vez y **reutiliza la misma** después. | ¿Cómo evitamos que cada redibujado arranque de cero? |
| `by` | Azúcar sintáctica: permite leer y escribir `clicks` como variable normal (`clicks++`) en lugar de `clicks.value++`. | ¿Por qué no se ve la "caja" en el resto del código? |

> 💡 **Imports**: al escribir `by` con `mutableStateOf`, Android Studio pide
> dos imports extra (`getValue` y `setValue`). Alt+Enter sobre el error y
> listo, como siempre.

La comparación con React deja claro que es la misma idea con otra sintaxis:

```jsx
// React
const [clicks, setClicks] = useState(0)
setClicks(clicks + 1)   // avisar del cambio = llamar al setter
```

```kotlin
// Compose
var clicks by remember { mutableStateOf(0) }
clicks++                // avisar del cambio = asignar, sin setter
```

En Compose no hay setter separado: **la asignación misma es la notificación**,
porque la caja (`MutableState`) detecta la escritura.

## 3. Recomposición: el redibujado inteligente

¿Qué pasa exactamente al tocar el botón?

```text
clicks++ (la caja cambia de 0 a 1)
        │
        ▼
Compose detecta el cambio
        │
        ▼
Vuelve a ejecutar los composables que LEEN esa caja
        │
        ▼
Text("Clicks: 1") se dibuja con el valor nuevo
```

Ese "volver a ejecutar" tiene nombre: **recomposición**. Es el re-render de
React, con un detalle importante: Compose solo vuelve a ejecutar los
composables que **leen** el estado que cambió, no toda la pantalla. Por eso el
modelo declarativo escala: tú declaras qué se ve para cada estado, y Compose
calcula el mínimo trabajo para mantener la pantalla al día.

Y aquí se entiende el rol de `remember`: la recomposición **vuelve a ejecutar
la función**. Sin `remember`, esa ejecución crearía una caja nueva con `0`
otra vez, y el contador quedaría clavado en cero. `remember` es la memoria que
sobrevive entre ejecuciones — igual que React conserva el valor de `useState`
entre renders.

Con esto, la fórmula del capítulo 03 queda completa y en movimiento:

```text
UI = f(estado)      ← el estado cambia → la UI se recalcula sola
```

### Ejercicio 1

Antes de tocar la Pokédex, comprueba la recomposición con tus propias manos:

1. En `MainActivity.kt`, crea el composable `Contador` de la sección 2
   (con `remember` y `mutableStateOf`).
2. Escríbele su `@Preview`.
3. En `setContent`, comenta **temporalmente** la llamada a `ListaPokemon`
   (con `/* */`, como en JavaScript) y coloca en su lugar
   `Contador(modifier = Modifier.padding(innerPadding))` — la lista se
   descomenta en el próximo ejercicio.
4. Ejecuta la app y toca el botón varias veces: el número debe subir.
5. **El experimento**: elimina `remember { }` (deja
   `var clicks by mutableStateOf(0)`) y vuelve a probar. Observa qué pasa con
   el número y explica por qué, con lo que leíste en la sección 3.

Cuando lo tengas funcionando —y tengas una teoría sobre el experimento del
paso 5—, comparte tu código y tu explicación.

<details>
<summary>Ver la solución y el experimento explicado</summary>

```kotlin
@Composable
fun Contador(modifier: Modifier = Modifier) {
    var clicks by remember { mutableStateOf(0) }

    Column(modifier = modifier) {
        Text("Clicks: $clicks")
        Button(onClick = { clicks++ }) {
            Text("Sumar")
        }
    }
}
```

**El experimento**: sin `remember`, el contador queda fijo en `0`. La cadena
completa:

1. `clicks++` escribe en la caja → Compose detecta el cambio y recompone.
2. La recomposición **vuelve a ejecutar `Contador`** desde el principio.
3. Esa nueva ejecución vuelve a evaluar `mutableStateOf(0)`: crea una **caja
   nueva** con `0`, y el valor anterior se pierde.

`remember` rompe ese ciclo: la primera vez ejecuta la lambda y guarda el
resultado; en cada recomposición devuelve **la misma caja guardada** en lugar
de crear otra. Por eso el valor sobrevive.

> ⚠️ **Gotcha de build**: si el código no compila (por ejemplo, un
> `var clicks` sin inicializar), Run falla y el dispositivo sigue mostrando
> **la última versión que compiló bien**. Si un cambio "no hace nada",
> conviene revisar primero la pestaña **Build**: puede que la app que se está
> probando sea la anterior.

</details>

## 4. Estado en la Pokédex: favoritos

El contador ya cumplió su función; es hora de volver a la Pokédex. El
enunciado del trabajo práctico pide **marcar y desmarcar favoritos**, y ahora
existen las herramientas para una primera versión: cada tarjeta puede tener su
propio estado.

Hacen falta dos composables nuevos de Material. Como siempre que aparece una
función nueva, primero su anatomía completa.

**`Icon`** dibuja un icono vectorial. Sus dos parámetros importantes:

| Parámetro | Qué es | Equivalente web |
|---|---|---|
| `imageVector` | Qué dibujo mostrar. Un `ImageVector` es un dibujo vectorial — el SVG del mundo Android. Material incluye un catálogo ya hecho en `Icons.Filled.*` (corazones, estrellas, lupas...): `Icons.Filled.Favorite` es el corazón lleno, `Icons.Filled.FavoriteBorder` el contorneado. | `<img src="icono.svg">` |
| `contentDescription` | El texto que un lector de pantalla lee en voz alta para una persona que no ve el icono. Describe la **acción**, no el dibujo ("Agregar a favoritos", no "corazón"). | El atributo `alt` de `<img>` |

**`IconButton`** es un botón circular pensado para contener un icono, con su
efecto de toque incluido. Sus dos lambdas responden preguntas distintas:
`onClick` — *¿qué pasa cuando lo tocan?* — recibe comportamiento, y
`content` (la trailing lambda) — *¿qué se dibuja adentro?* — recibe el `Icon`.

> ⚠️ **El catálogo de iconos es una dependencia aparte** — el proyecto de
> plantilla no la incluye, y sin ella `Icons` da *unresolved reference*.
> Agregarla lleva dos pasos (más el Sync), respetando el catálogo de versiones
> del capítulo 01:
>
> 1. En `gradle/libs.versions.toml`, junto a las demás librerías:
>
>    ```toml
>    androidx-compose-material-icons-core = { group = "androidx.compose.material", name = "material-icons-core" }
>    ```
>
>    Sin versión: el **BOM** de Compose elige la versión compatible.
>
> 2. En `app/build.gradle.kts`, junto a las demás `implementation`:
>
>    ```kotlin
>    implementation(libs.androidx.compose.material.icons.core)
>    ```
>
>    Los guiones del nombre en el catálogo se convierten en puntos.
>
> 3. **Sync Now** — el `npm install` de Gradle: descarga la librería y los
>    imports dejan de estar en rojo.
>
> La variante `material-icons-extended` trae el catálogo completo de Material,
> pero pesa mucho más; el set básico (`core`) incluye los corazones y alcanza
> para este capítulo.

Los imports necesarios, por si Alt+Enter no los ofrece todos:

```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
```

Combinados con el estado de este capítulo:

```kotlin
var esFavorito by remember { mutableStateOf(false) }

IconButton(onClick = { esFavorito = !esFavorito }) {
    Icon(
        imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
        contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
    )
}
```

Tres detalles para leer con atención:

- **`esFavorito = !esFavorito`**: el toggle clásico — asignar la negación.
  Igual que `setOn(!on)` en React, sin setter.
- **El `if` como expresión elige el icono**: no hay ninguna instrucción del
  estilo "cambia el icono". Se declara qué icono corresponde a cada estado
  (`Favorite` lleno o `FavoriteBorder` contorneado), y la recomposición hace
  el resto. Esto es el modelo declarativo aplicado: **UI = f(estado)** también
  para un simple corazón.
- **`contentDescription`**: el texto que leen los lectores de pantalla — el
  `alt` de una imagen en HTML. Describe la **acción**, y por eso también
  cambia con el estado.

Una pregunta que conviene hacerse antes del ejercicio: si hay varias fichas en
pantalla, ¿cuántas cajas de estado habrá? **Una por cada ficha visible,
independientes** — cada llamada a un composable es una instancia con su propia
memoria, igual que cada `<Ficha />` de React tiene su propio `useState`.
(¿Por qué "visible" y no "quince"? El experimento del paso 6 lo revela.)

### Ejercicio 2

Favoritos, primera versión:

1. En `setContent`, restaura `ListaPokemon` (descoméntala) y elimina la
   llamada a `Contador`. El composable `Contador` y su preview ya cumplieron:
   puedes borrarlos.
2. En `FichaPokemon`, declara el estado `esFavorito` como en la lección.
3. Agrega el `IconButton` con su `Icon` condicional en la fila superior de la
   tarjeta. Sugerencia: agrupa el número y el botón en una `Row` interna con
   `verticalAlignment = Alignment.CenterVertically`, para que el
   `SpaceBetween` de la fila principal siga separando nombre a la izquierda y
   ese grupo a la derecha.
4. Los imports de siempre con Alt+Enter (`Icons`, `Favorite`,
   `FavoriteBorder`, `Icon`, `IconButton`).
5. Ejecuta la app y marca varios favoritos: cada tarjeta debe recordar el suyo,
   independiente de las demás.
6. **El experimento**: marca como favorito al primer Pokémon de la lista,
   desplázate hasta el final, y vuelve arriba. Observa el corazón de ese
   Pokémon y formula una teoría: ¿qué pasó, y qué tiene que ver la sección 3
   de este capítulo con lo que `LazyColumn` hace con las tarjetas que salen
   de pantalla?

Cuando lo tengas, comparte el código y tu teoría del paso 6.

<details>
<summary>Ver la solución y el experimento explicado</summary>

```kotlin
@Composable
fun FichaPokemon(name: String, numero: Int, tipo: String, modifier: Modifier = Modifier) {
    var esFavorito by remember { mutableStateOf(false) }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(name, style = MaterialTheme.typography.titleLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "N.º $numero",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    IconButton(onClick = { esFavorito = !esFavorito }) {
                        Icon(
                            imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                        )
                    }
                }
            }
            Text("Tipo: $tipo", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
```

**El experimento**: al marcar un Pokémon, desplazarse hasta el final y volver,
el corazón aparece **desmarcado**. La explicación encadena dos cosas ya
conocidas:

1. `LazyColumn` solo mantiene en composición las tarjetas **visibles**
   (capítulo 04): una tarjeta que sale de pantalla no se oculta — **deja de
   existir**.
2. `remember` guarda su caja **mientras el composable viva** (sección 3): si
   la composición de esa ficha muere, su memoria muere con ella. Al volver,
   Compose crea una ficha nueva, con una caja nueva, en `false`.

Es el contador sin `remember`, a otra escala: el estado vivía en un lugar que
no sobrevive. La solución es el tema de la siguiente sección.

</details>

## 5. State hoisting: subir el estado

El experimento del ejercicio 2 demostró que el estado guardado **dentro** de
cada tarjeta tiene dos problemas:

1. **Muere con la tarjeta**: `LazyColumn` destruye las fichas que salen de
   pantalla, y `remember` no sobrevive a esa destrucción.
2. **Está preso**: el enunciado del trabajo práctico exige una pantalla de
   Favoritos y un estado sincronizado entre pantallas. Si cada tarjeta guarda
   su favorito en secreto, el resto de la aplicación no puede verlo.

La solución se llama **state hoisting** (*subir el estado*): mover el estado
desde el componente hijo hacia un ancestro que viva más tiempo y que todos
compartan. En React es el clásico *lift state up*. El componente hijo queda
**sin estado propio** (*stateless*): recibe el valor y avisa los eventos.

```text
ANTES                              DESPUÉS
ListaPokemon                       ListaPokemon  ← el estado vive aquí
├── Ficha [esFavorito]             ├── Ficha(esFavorito = ..., onFavoritoClick = ...)
├── Ficha [esFavorito]             ├── Ficha(esFavorito = ..., onFavoritoClick = ...)
└── Ficha [esFavorito]             └── Ficha(esFavorito = ..., onFavoritoClick = ...)
```

La regla se memoriza así: **el estado baja, los eventos suben** — como en
React: el valor viaja por props y el cambio se pide por callback.

Piezas nuevas, con su anatomía completa:

**Un parámetro de tipo función.** Hasta ahora los parámetros eran datos
(`String`, `Int`, `Modifier`). Un parámetro también puede recibir una
**función**. Esto ya es familiar desde JavaScript, aunque sin nombre propio:

```javascript
boton.addEventListener("click", avisarQueTocaron)
// se le PASA una función a otra; addEventListener la ejecuta cuando ocurre el click
```

En Kotlin, la firma se declara así:

```kotlin
fun FichaPokemon(
    name: String,                 // recibe un texto
    onFavoritoClick: () -> Unit,  // recibe una FUNCIÓN
)
```

Para leer `() -> Unit` sin miedo:

- `()` — no recibe parámetros.
- `->` — separa lo que entra de lo que sale.
- `Unit` — **es un tipo de dato**: significa "no devuelvo nada útil". Es el
  `void` de otros lenguajes, o el `undefined` que devuelve una función de
  JavaScript sin `return`. Otro ejemplo para comparar: `(Int) -> String` es
  una función que recibe un entero y devuelve un texto.

Y una distinción que evita confusiones: **`() -> Unit` es el tipo; la lambda
es el valor** que se le pasa — la misma relación que hay entre `String` y
`"Pikachu"`:

```kotlin
name = "Pikachu"                       // tipo String    ← valor: un texto
onFavoritoClick = { favoritos = ... }  // tipo () -> Unit ← valor: una lambda
```

¿Para qué sirve? Para que la ficha funcione como un **timbre**: el timbre no
decide qué pasa cuando lo presionan — solo avisa. Quién decide es la casa (el
padre). La ficha ejecuta lo recibido cuando ocurre el evento —
`IconButton(onClick = onFavoritoClick)` — y el padre eligió qué código era.
Es la prop-callback de React: `onFavoritoClick: () => void`.

**`Set`: la colección sin duplicados.** Para recordar los favoritos alcanza
con guardar los **números** de los Pokémon marcados. La colección ideal es un
`Set` (conjunto): como una `List`, pero sin elementos repetidos — el `Set` de
JavaScript. Tres operaciones y listo:

| Kotlin | Qué hace | JavaScript |
|---|---|---|
| `setOf<Int>()` | Crea un conjunto vacío de enteros | `new Set()` |
| `numero in favoritos` | ¿Está el número en el conjunto? (`true`/`false`) | `favoritos.has(numero)` |
| `favoritos + numero` / `favoritos - numero` | Devuelve un conjunto **nuevo** con el número agregado o quitado | `new Set([...favoritos, numero])` |

Dos aclaraciones que suelen generar dudas:

- **Agregar un elemento que ya está no falla ni devuelve `null`**: siempre se
  obtiene un conjunto válido. `{25, 94} + 25` devuelve `{25, 94}` — el `Set`
  es el guardián de su regla: no se queja, simplemente no deja pasar dos veces
  al mismo. Y `{25, 94} + 4` devuelve `{25, 94, 4}`.
- **`+` y `-` no modifican el conjunto original** — devuelven uno **nuevo**, y
  si no se guarda, se pierde:

  ```kotlin
  favoritos + 25              // se crea un conjunto nuevo... y se descarta
  favoritos = favoritos + 25  // así: el nuevo REEMPLAZA al viejo en la caja
  ```

Esa asignación no es una molestia, es el mecanismo: la caja de
`mutableStateOf` detecta la **asignación** — es el mismo timbrazo que en el
contador, donde `clicks++` es en el fondo `clicks = clicks + 1`. React
funciona igual: el re-render llega cuando se llama al setter con un valor
nuevo. Modificar algo por dentro, sin asignar, no avisa a nadie.

### Ejercicio 3

Liberar a los favoritos:

1. En `FichaPokemon`, elimina la línea del estado (`var esFavorito by ...`) y
   agrega **dos parámetros** a la función: `esFavorito: Boolean` y
   `onFavoritoClick: () -> Unit`. El `IconButton` queda
   `IconButton(onClick = onFavoritoClick)` y el resto del `if` no cambia:
   sigue leyendo `esFavorito`, que ahora llega desde afuera.
2. En `ListaPokemon`, declara el estado compartido:
   `var favoritos by remember { mutableStateOf(setOf<Int>()) }`.
3. En el `items`, pasa los dos argumentos nuevos a cada ficha:
   - `esFavorito = pokemon.numero in favoritos`
   - `onFavoritoClick = { favoritos = if (pokemon.numero in favoritos) favoritos - pokemon.numero else favoritos + pokemon.numero }`
4. `FichaPokemonPreview` dejará de compilar: le faltan los dos argumentos
   nuevos. Pásale valores de ejemplo: `esFavorito = true` y
   `onFavoritoClick = { }` (una lambda vacía — en un preview no hay nada que
   hacer al tocar).
5. Ejecuta la app y repite el experimento del scroll: marca a Pikachu, baja
   hasta el final, vuelve. El corazón debe **seguir marcado**.

> ⚠️ **El estado sigue siendo efímero**: al girar el teléfono, los favoritos
> vuelven a cero — la Activity se destruye y se recrea, y este `remember`
> muere con ella. La solución definitiva del trabajo práctico llega por
> capas: `ViewModel` (capítulo 06) y persistencia con Room más adelante. El
> hoisting es el primer paso de ese camino, no el último.

Cuando funcione, comparte el código completo de `FichaPokemon` y
`ListaPokemon`.

<details>
<summary>Ver la solución</summary>

```kotlin
@Composable
fun FichaPokemon(
    name: String,
    numero: Int,
    tipo: String,
    esFavorito: Boolean,          // el valor baja desde el padre
    onFavoritoClick: () -> Unit,  // el evento sube hacia el padre
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(name, style = MaterialTheme.typography.titleLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "N.º $numero",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    IconButton(onClick = onFavoritoClick) {
                        Icon(
                            imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                        )
                    }
                }
            }
            Text("Tipo: $tipo", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ListaPokemon(pokemones: List<Pokemon>, modifier: Modifier = Modifier) {
    var favoritos by remember { mutableStateOf(setOf<Int>()) }   // el estado, arriba

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(pokemones) { pokemon ->
            FichaPokemon(
                name = pokemon.name,
                numero = pokemon.numero,
                tipo = pokemon.tipo,
                esFavorito = pokemon.numero in favoritos,
                onFavoritoClick = {
                    favoritos = if (pokemon.numero in favoritos)
                        favoritos - pokemon.numero
                    else
                        favoritos + pokemon.numero
                },
            )
        }
    }
}
```

Dos errores frecuentes en este ejercicio, vistos en batalla:

- **Redeclarar los parámetros adentro** (`var esFavorito: Boolean` en el
  cuerpo): un parámetro **ya es** una variable dentro de la función — recibir
  es declarar. Volver a declararla crea una variable vacía que tapa a la que
  llegó con el valor, y no compila.
- **Nombres que no coinciden**: `onFavoritoClic` en la firma y
  `onFavoritoClick` en el cuerpo son, para Kotlin, dos identificadores
  distintos. Una letra basta para el *unresolved reference*.

¿Por qué ahora el favorito sobrevive al scroll? Porque el estado vive en
`ListaPokemon`, que nunca sale de composición. Las fichas siguen muriendo y
renaciendo con el scroll — pero al renacer **preguntan** (`numero in
favoritos`) y el conjunto, que nunca murió, tiene la respuesta.

</details>

## Checkpoint

- [ ] Sé por qué una variable común no actualiza la pantalla y una caja de
  `mutableStateOf` sí.
- [ ] Puedo explicar qué hace `remember` y qué pasa si falta.
- [ ] Entiendo la recomposición: qué se vuelve a ejecutar y cuándo.
- [ ] Sé que la **asignación** es lo que dispara la recomposición (declarar la
  caja no avisa; asignarle un valor nuevo sí).
- [ ] Puedo leer `() -> Unit` y explicar qué es `Unit`.
- [ ] Entiendo el state hoisting: el estado baja como dato, los eventos suben
  como callback.
- [ ] Sé por qué el estado local de un elemento de `LazyColumn` se pierde al
  scrollear, y cómo el hoisting lo resuelve.

**Anterior**: [04 — Tarjetas y listas con Material](/pokedex/capitulos/04-tarjetas-material/) · **Siguiente**: 06 *(próximamente)*
