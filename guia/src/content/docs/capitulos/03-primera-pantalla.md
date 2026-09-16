---
title: 03 — Primera pantalla con Compose
description: Qué es Jetpack Compose, cómo leer el código que generó Android Studio y tu primer cambio de UI.
---

**Al terminar este capítulo vas a poder**: explicar qué es Jetpack Compose y el
modelo declarativo, leer con confianza el código que Android Studio generó, y
construir la primera pantalla de la Pokédex con tus propios composables.

A diferencia del capítulo anterior, acá dejamos el Playground: **todo pasa en
Android Studio**, dentro del proyecto Pokedex que creamos en el
[capítulo 01](/pokedex/capitulos/01-entorno/).

---

## 1. ¿Qué es Jetpack Compose?

Hay dos maneras de construir una interfaz de usuario, y conocerlas explica por
qué Compose es como es.

**La manera imperativa**: le das órdenes a la pantalla, paso a paso. Es el
JavaScript vanilla que ya conocés:

```js
// JS imperativo: construir y actualizar a mano
const titulo = document.createElement("h1");
titulo.textContent = "Pokédex";
document.body.appendChild(titulo);

// ...y cuando el dato cambia, actualizar es TU responsabilidad:
titulo.textContent = "Pokédex (150)";
```

El problema: en una app real hay decenas de datos y decenas de elementos en
pantalla. Mantenerlos sincronizados a mano es la fuente de bugs número uno.

**La manera declarativa**: describís cómo se ve la pantalla para un estado
dado, y el framework se encarga de actualizarla cuando el estado cambia. Es
exactamente la idea de React:

```jsx
// React: la UI se DESCRIBE en función de los datos
function Titulo({ cantidad }) {
  return <h1>Pokédex ({cantidad})</h1>;
}
```

**Jetpack Compose es el React de Android.** La misma idea, en Kotlin:

```kotlin
@Composable
fun Titulo(cantidad: Int) {
    Text("Pokédex ($cantidad)")
}
```

La frase que resume todo el modelo, y que vas a ver repetida en este proyecto:

> **UI = f(estado)** — la interfaz es el resultado de ejecutar funciones sobre
> datos. Cuando los datos cambian, Compose vuelve a ejecutar las funciones
> necesarias y la pantalla se actualiza sola.

Ese "volver a ejecutar" tiene nombre — **recomposición** — y lo vamos a ver en
detalle cuando trabajemos con estado.

> 💡 **Nota histórica**: antes de Compose, la UI de Android se escribía en
> archivos XML (el equivalente del HTML) y se manipulaba desde el código (el
> equivalente de jQuery). Ese mundo todavía existe en proyectos viejos, pero
> Compose es el camino moderno que Google recomienda — y el único que vamos a
> usar acá.

## 2. El código que ya tenés, pieza por pieza

Abrí `app/src/main/java/com/mkarajallo/pokedex/MainActivity.kt`. Todo lo que
Android Studio generó en la Fase 0 se entiende con lo que ya sabés. Vamos por
partes.

### La Activity: la puerta de entrada

```kotlin
class MainActivity : ComponentActivity() {
```

Una **Activity** es el punto de entrada visual de una app Android: cuando
tocás el ícono, el sistema operativo abre esta clase. Pensala como el
`index.html` de la app.

Los dos puntos son **herencia**: `MainActivity` *hereda* de
`ComponentActivity` — en JS sería `class MainActivity extends
ComponentActivity`. Heredar significa que nuestra clase recibe gratis todo el
comportamiento de una Activity (mostrarse, cerrarse, rotar…) y nosotros solo
completamos lo específico.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
```

`onCreate` se ejecuta cuando Android crea la pantalla — el equivalente al
momento en que el navegador carga tu página. `override` avisa que estamos
reemplazando un método heredado, y `super.onCreate(...)` le dice al padre
"primero hacé lo tuyo". Este bloque lo escribió el template y casi nunca se
toca — no hace falta memorizarlo.

> 💡 Fijate en `Bundle?` — ese `?` es el null safety del
> [capítulo 02](/pokedex/capitulos/02-kotlin-esencial/) apareciendo en código
> real: el parámetro puede ser null, y el tipo lo declara.

### `setContent`: acá arranca Compose

```kotlin
setContent {
    PokedexTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Greeting(
                name = "Android",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
```

- **`setContent { }`** — todo lo que va dentro de esas llaves ES la interfaz.
  Es el equivalente de `createRoot(...).render(<App />)` en React. Y esas
  llaves ya las conocés: es un **trailing lambda** (capítulo 02, sección 5).
  Compose se lee así en todos lados: funciones que reciben lambdas con más UI
  adentro.
- **`PokedexTheme`** — envuelve la app con sus colores y tipografías, como un
  `ThemeProvider` de React. Su código vive en `ui/theme/` (por eso esa carpeta
  existe en el proyecto).
- **`Scaffold`** — el "andamio" de la pantalla. Merece explicación propia:
  la tiene, acá abajo.
- **`Greeting(name = "Android", ...)`** — la llamada a nuestro componente,
  con **argumentos con nombre**. Te dije que en Compose se usaban hasta el
  cansancio.

### `Scaffold`: el andamio de la pantalla

La palabra viene de la construcción: *scaffold* significa **andamio**. Y el
nombre es perfecto: es la estructura que sostiene una pantalla típica
mientras la construís.

`Scaffold` es un composable de Material Design que trae el **esqueleto** de
una pantalla Android, con huecos con nombre para las piezas de siempre:

```kotlin
Scaffold(
    topBar = { /* barra superior: título, acciones */ },
    bottomBar = { /* barra de navegación inferior */ },
    floatingActionButton = { /* el botón redondo flotante */ },
) { innerPadding ->
    // el contenido principal de la pantalla
}
```

Si venís de la web, es el template base de un sitio: `<header>`, `<footer>`
y un `<main>` para el contenido. Hoy usamos solo el contenido; cuando la
Pokédex tenga barra superior con título, la enchufamos en `topBar` sin tocar
nada más.

**¿Y el `innerPadding`?** La app se dibuja de borde a borde de la pantalla
(eso hace `enableEdgeToEdge()` en `onCreate`), incluso debajo de la barra de
estado (la de la hora y la batería) y la de navegación. `Scaffold` mide
cuánto ocupan esas barras y te lo entrega como `innerPadding` para que se lo
apliques a tu contenido. La cadena completa tiene tres eslabones:

1. `Scaffold` te lo da: `{ innerPadding -> ... }` (una lambda con parámetro —
   capítulo 02).
2. Vos se lo pasás al componente: `modifier = Modifier.padding(innerPadding)`.
3. El componente lo aplica: `Column(modifier = modifier)`.

Si algún día ves tu contenido metido abajo del reloj, se cortó uno de esos
eslabones.

### `@Composable`: tu primer componente

```kotlin
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
```

Esto es un **componente**, exactamente en el sentido de React:

- La anotación **`@Composable`** marca que esta función describe UI. Es la
  regla número uno de Compose: una función `@Composable` solo puede llamarse
  desde otra función `@Composable` (igual que un hook de React solo vive
  dentro de componentes).
- El nombre va en **PascalCase** (`Greeting`, no `greeting`) — la misma
  convención que los componentes de React.
- Los **parámetros son sus props**: `Greeting` recibe `name: String` como un
  componente React recibiría `{ name }`.
- No tiene `return` — un composable no *devuelve* UI, la **emite**: llamar a
  `Text(...)` adentro ya la agrega a la pantalla.
- **`Text`** es un composable que viene con Compose, como `<p>` viene con
  HTML. Y adentro, `"Hello $name!"` — template string del capítulo 02.
- **`Modifier`** por ahora quedémonos con esto: es el parámetro con el que se
  ajusta apariencia y posición (relleno, tamaño, fondo…). Merece su propia
  sección y la va a tener.

### `@Preview`: Storybook incorporado

```kotlin
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PokedexTheme {
        Greeting("Android")
    }
}
```

`@Preview` le pide a Android Studio que dibuje ese composable en el panel de
vista previa, **sin emulador y sin instalar nada**. Es un Storybook que viene
de fábrica: escribís una función que muestra tu componente con datos de
ejemplo, y el editor la renderiza al lado del código.

Para verla: con `MainActivity.kt` abierto, arriba a la derecha del editor
están los botones **Code | Split | Design** — elegí **Split** para ver código
y preview a la vez. Si el panel pide *"Build & Refresh"*, tocalo y esperá.

> 💡 **¿"Definir" dos veces?** Una duda muy común al ver el mismo componente
> llamado desde `setContent` Y desde el preview con datos distintos. La
> respuesta: una función se **define** una sola vez —
> `fun Greeting(name: String)`, ahí nacen sus parámetros — y se **llama**
> tantas veces como haga falta. `name = "Android"` en una llamada no define
> nada: es un argumento con nombre, una etiqueta que aclara qué valor va a
> qué parámetro. Y las dos llamadas no compiten, porque viven en mundos
> distintos: la app real ejecuta lo de `setContent`; el panel de preview
> ejecuta la función `@Preview`. Son dos "pantallas" independientes que usan
> el mismo componente — igual que tu `index.js` y una story de Storybook
> llaman al mismo componente React con datos diferentes.

### El mapa completo

| Compose | React | Qué es |
|---|---|---|
| `@Composable fun` | componente función | una pieza de UI reutilizable |
| parámetros de la función | props | los datos que recibe |
| `setContent { }` | `createRoot().render()` | donde se monta la UI |
| `PokedexTheme` | `ThemeProvider` | colores y tipografías globales |
| `Text`, `Scaffold` | `<p>`, componentes de librería | piezas ya hechas |
| `@Preview` | Storybook | ver componentes sin correr la app |
| `Modifier` | `style` / `className` (aprox.) | apariencia y posición |

### Ejercicio 1

Tu primer cambio de UI, de punta a punta:

1. Abrí el proyecto Pokedex en Android Studio y andá a `MainActivity.kt`.
2. Activá la vista **Split** y esperá a que el preview renderice.
3. Modificá lo necesario para que la pantalla diga
   `¡Hola, entrenador <tu nombre>!` en vez de `Hello Android!`.
   Pista: `Greeting` recibe el nombre por parámetro — fijate desde cuántos
   lugares se lo llama.
4. Mirá cómo el preview se actualiza solo, y después corré la app en el
   emulador (▶) para verla en pantalla "real".

<details>
<summary>Ver una solución</summary>

Hay que tocar dos lugares: el texto dentro de `Greeting`, y el `name` que se
le pasa desde `setContent`:

```kotlin
// Dentro de setContent:
Greeting(
    name = "Mario",
    modifier = Modifier.padding(innerPadding)
)

// El componente:
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "¡Hola, entrenador $name!",
        modifier = modifier
    )
}
```

> 💡 **Detalle que sorprende**: si dejaste `GreetingPreview` como estaba, el
> preview sigue mostrando *"¡Hola, entrenador Android!"*. No es un error:
> el preview le pasa sus **propios** datos (`Greeting("Android")`), igual que
> una story de Storybook usa datos de ejemplo. La app real usa lo que se pasa
> en `setContent`; cada preview, lo suyo. Eso permite previsualizar variantes
> de un componente sin tocar la app.

</details>

## 3. `Column`, `Row` y `Modifier`: acomodar cosas en pantalla

Un `Text` solo no hace una Pokédex. Las pantallas reales apilan y alinean
elementos, y para eso Compose tiene tres contenedores básicos. Si venís de
CSS, esto es flexbox con otros nombres:

| Compose | CSS / HTML | Qué hace |
|---|---|---|
| `Column { }` | `display: flex; flex-direction: column` | apila hijos en vertical |
| `Row { }` | `display: flex; flex-direction: row` | alinea hijos en horizontal |
| `Box { }` | `position: relative` + hijos encimados | superpone hijos (capas) |

Y se usan con la sintaxis que ya conocés — trailing lambdas anidados:

```kotlin
Column {
    Text("Pikachu")
    Text("N.º 025")
    Text("Tipo: Eléctrico")
}
```

Eso dibuja los tres textos uno debajo del otro. Sin `Column`, Compose los
dibujaría **uno encima del otro** — no sabe cómo querés acomodarlos si no se
lo decís.

### `Modifier`: la caja de herramientas de estilo

Prometido en la sección 2, acá está. `Modifier` es cómo se ajusta apariencia,
tamaño y posición de un composable. Se construye **encadenando** llamadas, y
cada composable lo recibe como parámetro:

```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()        // ocupá toda la pantalla (width: 100%; height: 100%)
        .padding(16.dp)       // relleno interno (padding: 16px... más o menos)
) {
    Text("Pikachu")
}
```

Dos cosas nuevas ahí:

- **`dp`** (*density-independent pixels*): la unidad de medida de Android.
  Como los píxeles de CSS, pero se adapta a la densidad de cada pantalla para
  que 16.dp se vea del mismo tamaño físico en un teléfono barato y en uno
  premium. Regla simple: donde en CSS pondrías `px`, acá va `.dp`.
- **El orden de la cadena importa**: `padding(16.dp).fillMaxSize()` y
  `fillMaxSize().padding(16.dp)` no dan lo mismo (¿relleno antes o después de
  estirarse?). Si un layout se ve raro, lo primero a revisar es el orden de
  los modifiers.

### El patrón `modifier: Modifier = Modifier`

Todos los composables bien escritos reciben este parámetro, y la primera vez
confunde porque la palabra aparece tres veces seguidas:

```kotlin
fun FichaPokemon(nombre: String, modifier: Modifier = Modifier) {
//               nombre del parámetro ↑        ↑ tipo    ↑ default
    Column(modifier = modifier) {   // ← y acá se USA lo recibido
        // ...
    }
}
```

- `modifier` — el nombre del parámetro (por convención, siempre este).
- `: Modifier` — su tipo.
- `= Modifier` — el valor por defecto: un modifier **vacío**, sin
  instrucciones. Como un `style=""` en HTML: si nadie manda nada, no pasa nada.

¿Para qué existe? Para que **quien usa el componente pueda ajustarlo desde
afuera** — igual que pasarle `style` o `className` a un componente de React:

```kotlin
FichaPokemon(
    nombre = "Pikachu",
    modifier = Modifier.padding(innerPadding),  // instrucciones desde afuera
)
```

> ⚠️ **La trampa clásica**: recibir el `modifier` y no usarlo. Si la `Column`
> interna no hace `Column(modifier = modifier)`, las instrucciones del
> llamador mueren en la puerta: el `padding(innerPadding)` del `Scaffold`
> nunca llega, y el contenido queda dibujado debajo de la barra de estado.
> El parámetro se recibe **y se pasa** al contenedor raíz del componente.

### Alinear y espaciar

`Column` y `Row` aceptan parámetros para alinear a sus hijos — con argumentos
con nombre, como siempre:

```kotlin
Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,   // centrar en horizontal
    verticalArrangement = Arrangement.spacedBy(8.dp),     // 8.dp entre cada hijo
) {
    Text("Pikachu")
    Text("N.º 025")
}
```

`Arrangement.spacedBy(8.dp)` es el `gap` de flexbox: separa a los hijos entre
sí sin que cada uno tenga que preocuparse por sus márgenes.

> 💡 **Los imports se resuelven solos**: al escribir `Column`, `dp` o
> `Alignment`, Android Studio los subraya en rojo. Posicionate sobre la
> palabra y presioná **Alt+Enter** → *Import*. No hace falta escribir imports
> a mano nunca.

### Ejercicio 2

Vamos a crear la primera pieza real de la Pokédex: una ficha de Pokémon.

1. Debajo de `Greeting`, creá un composable `FichaPokemon` que reciba
   `nombre: String`, `numero: Int` y `tipo: String`, más el `modifier`
   opcional de rigor.
2. Adentro, una `Column` que muestre tres `Text`: el nombre, `"N.º 025"`
   (formateado desde el número) y `"Tipo: Eléctrico"` (desde el tipo).
   Separá los hijos con `Arrangement.spacedBy(4.dp)`.
3. Escribile su propio `@Preview` con un Pokémon de ejemplo.
4. En `setContent`, reemplazá la llamada a `Greeting` por tu `FichaPokemon`
   (pasale el `Modifier.padding(innerPadding)`).
5. Corré la app: tu primera pantalla con un componente propio.

<details>
<summary>Ver una solución</summary>

```kotlin
// En setContent:
Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
    FichaPokemon(
        name = "Pikachu",
        numero = 25,
        tipo = "Eléctrico",
        modifier = Modifier.padding(innerPadding),
    )
}

// El componente:
@Composable
fun FichaPokemon(name: String, numero: Int, tipo: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(name)
        Text("N.º $numero")
        Text("Tipo: $tipo")
    }
}

// Y su preview, con datos de ejemplo propios:
@Preview(showBackground = true)
@Composable
fun FichaPokemonPreview() {
    PokedexTheme {
        FichaPokemon(name = "Pikachu", numero = 25, tipo = "Eléctrico")
    }
}
```

> ⚠️ **Errores comunes** — todos aparecieron al resolver este ejercicio por
> primera vez:
>
> 1. **Olvidar `@Composable`**. Sin la anotación, la función no puede dibujar
>    UI: *"@Composable invocations can only happen from the context of a
>    @Composable function"*.
> 2. **Recibir los parámetros y no usarlos**, escribiendo los textos a mano.
>    La ficha dice "Pikachu" aunque le pases "Charmander" — los datos se
>    inyectan con template strings: `Text(name)`, `Text("N.º $numero")`.
> 3. **Recibir el `modifier` y no pasarlo a la `Column`**. Las instrucciones
>    del llamador se pierden y el contenido queda debajo de la barra de
>    estado (ver "La trampa clásica" más arriba).
> 4. **Llamar `FichaPokemon()` sin argumentos en el preview**. En Kotlin los
>    parámetros sin valor por defecto son obligatorios — *"No value passed
>    for parameter 'name'"*. El preview necesita sus datos de ejemplo.
> 5. **El import fantasma `import android.R`**. Android Studio a veces lo
>    agrega solo al aceptar un import apurado. Es peligroso: pisa la clase
>    `R` de tu propia app (donde viven tus recursos) y causa errores raros
>    después. Si aparece en gris (sin usar), borralo — o usá
>    *Code → Optimize Imports* (Ctrl+Alt+O).

</details>

## 4. De una ficha a una lista: UI = f(datos), en serio

Hasta acá, una ficha con datos escritos a mano. Una Pokédex real muestra
**muchos** Pokémon que vienen de una lista de datos. Y acá se juntan los dos
capítulos: la `data class` y las colecciones del
[capítulo 02](/pokedex/capitulos/02-kotlin-esencial/) son EXACTAMENTE lo que
Compose necesita.

La pieza clave es una idea simple: **los composables son funciones**, así que
se pueden llamar dentro de un bucle. Cada vuelta emite una ficha:

```kotlin
data class Pokemon(val name: String, val numero: Int, val tipo: String)

@Composable
fun ListaPokemon(pokemones: List<Pokemon>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        pokemones.forEach { pokemon ->
            FichaPokemon(
                name = pokemon.name,
                numero = pokemon.numero,
                tipo = pokemon.tipo,
            )
        }
    }
}
```

Leelo con calma: `forEach` recorre la lista, y en cada vuelta **llama a un
composable** con los datos de ese Pokémon. Es el equivalente exacto de
`{lista.map(p => <Ficha {...p} />)}` en React.

Esto es **UI = f(datos)** hecho realidad: agregá un Pokémon a la lista y la
pantalla muestra una ficha más — sin tocar ni una línea de UI. La interfaz
es un reflejo de los datos.

¿Y de dónde sale la lista? Por ahora, escrita a mano arriba del archivo:

```kotlin
val pokebola = listOf(
    Pokemon("Pikachu", 25, "Eléctrico"),
    Pokemon("Charmander", 4, "Fuego"),
    // ...
)
```

Más adelante esa lista va a venir de la PokeAPI — y lo hermoso es que
`ListaPokemon` **no va a cambiar**: le da igual de dónde salgan los datos.

> 💡 **Aviso para el futuro**: `Column` dibuja TODOS sus hijos, incluso los
> que no entran en pantalla. Con 4 Pokémon es perfecto; con 1.000 sería un
> desastre de memoria. Para listas largas existe `LazyColumn`, que dibuja
> solo lo visible (como la virtualización en React). Llega en su momento.

### Ejercicio 3

1. Arriba del archivo (fuera de la clase), declará la
   `data class Pokemon(val name: String, val numero: Int, val tipo: String)`.
2. Creá una `val pokebola = listOf(...)` con al menos 4 Pokémon — nombres y
   números de Pokédex bien escritos, que esto lo lee gente.
3. Escribí el composable `ListaPokemon` que reciba `List<Pokemon>` y el
   `modifier`, con una `Column` y `Arrangement.spacedBy(16.dp)`, recorriendo
   la lista con `forEach` y llamando a `FichaPokemon` en cada vuelta.
   **Ojo**: `ListaPokemon` es un componente NUEVO — `FichaPokemon` sigue
   existiendo tal cual. La lista no dibuja textos: dibuja fichas, llamando
   al componente que ya tenés. Es la **composición** de React: `<PokemonList>`
   renderiza `<PokemonCard>`s.
4. Hacele su `@Preview` con una lista de ejemplo.
5. En `setContent`, reemplazá `FichaPokemon` por
   `ListaPokemon(pokemones = pokebola, modifier = Modifier.padding(innerPadding))`.
6. Corré la app. Después agregá un quinto Pokémon a la lista y volvé a
   correr: mirá cómo la pantalla se actualiza sola.

<details>
<summary>Ver una solución</summary>

```kotlin
data class Pokemon(val name: String, val numero: Int, val tipo: String)

val pokebola = listOf(
    Pokemon("Pikachu", 25, "Eléctrico"),
    Pokemon("Gengar", 94, "Fantasma / Veneno"),
    Pokemon("Lucario", 448, "Lucha / Acero"),
    Pokemon("Rayquaza", 384, "Dragón / Volador"),
)

// En setContent — ListaPokemon REEMPLAZA a la llamada de FichaPokemon:
Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
    ListaPokemon(
        pokemones = pokebola,
        modifier = Modifier.padding(innerPadding),
    )
}

// El componente lista. FichaPokemon sigue existiendo tal cual:
@Composable
fun ListaPokemon(pokemones: List<Pokemon>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        pokemones.forEach { pokemon ->
            FichaPokemon(
                name = pokemon.name,
                numero = pokemon.numero,
                tipo = pokemon.tipo,
            )
        }
    }
}

// Y su preview, con una lista de ejemplo:
@Preview(showBackground = true)
@Composable
fun ListaPokemonPreview() {
    PokedexTheme {
        ListaPokemon(
            pokemones = listOf(
                Pokemon("Pikachu", 25, "Eléctrico"),
                Pokemon("Gengar", 94, "Fantasma / Veneno"),
            )
        )
    }
}
```

> ⚠️ **Errores comunes** — todos aparecieron al resolver este ejercicio por
> primera vez:
>
> 1. **Renombrar `FichaPokemon` a `ListaPokemon`** en vez de crear un
>    componente nuevo. Son dos piezas que conviven: la ficha dibuja UNO, la
>    lista recorre y llama a la ficha. Composición, como en React.
> 2. **`FichaPokemon()` sin argumentos dentro del `forEach`**. Los datos
>    salen del elemento de la vuelta actual: `it` o un parámetro nombrado
>    (`pokemon -> ...`) — el mismo `it` de `lista.map { it.nombre }`.
> 3. **Dejar la llamada vieja a `FichaPokemon` junto a `ListaPokemon`** en el
>    `Scaffold`: superposición otra vez. Reemplazar es reemplazar.
> 4. **`@Composable` sin `@Preview`** en la función de preview: el panel no
>    la muestra. `@Composable` la hace dibujable; `@Preview` es la que le
>    dice a Android Studio "mostrala". Van las dos.
> 5. Convención: las **variables van en camelCase** (`pokebola`, no
>    `Pokebola`) — la mayúscula inicial es para clases y composables.

</details>

## Checkpoint

- [ ] Puedo explicar la diferencia entre UI imperativa y declarativa, y qué
      significa **UI = f(estado)**.
- [ ] Sé leer `MainActivity.kt` completo: Activity, `onCreate`, `setContent`,
      `PokedexTheme`, `Scaffold` y su `innerPadding`.
- [ ] Puedo crear un composable propio: `@Composable`, parámetros como props,
      y el patrón `modifier: Modifier = Modifier` (recibirlo Y usarlo).
- [ ] Entiendo `Column`/`Row`, el encadenado de `Modifier`, la unidad `dp` y
      `Arrangement.spacedBy`.
- [ ] Sé escribir un `@Preview` con datos de ejemplo del tipo que el
      componente pida — y sé por qué no compite con `setContent`.
- [ ] Puedo dibujar una lista de datos recorriéndola con `forEach` y llamando
      a otro composable (composición).

**Anterior**: [02 — Kotlin esencial](/pokedex/capitulos/02-kotlin-esencial/) · **Siguiente**: [04 — Tarjetas y listas con Material](/pokedex/capitulos/04-tarjetas-material/)
