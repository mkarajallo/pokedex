---
title: 04 — Tarjetas y listas con Material
description: Card, tipografía del tema, filas horizontales y LazyColumn — la Pokédex empieza a verse como una app real.
---

**Al terminar este capítulo vas a poder**: darle aspecto de app real a la
Pokédex — tarjetas Material con elevación, tipografía del tema, filas
horizontales, y una lista que escala con `LazyColumn`.

Partimos del código que dejó el [capítulo 03](/pokedex/capitulos/03-primera-pantalla/):
`FichaPokemon`, `ListaPokemon` y la lista `pokebola`.

---

## 1. `Card`: la ficha se hace tarjeta

Antes de tocar código, una presentación formal. **Material Design** es el
sistema de diseño de Google: un catálogo de componentes con apariencia y
comportamiento ya resueltos — botones, tarjetas, barras, menús. Si venís de
la web, cumple el rol de Bootstrap o MUI. Y ya venís usando piezas de
Material sin saberlo: `Scaffold` y `Text` salen de
`androidx.compose.material3`.

La pieza de hoy es **`Card`**: un contenedor con fondo, bordes redondeados y
elevación (sombra). En CSS sería un `div` con `border-radius` +
`box-shadow` + fondo — acá viene armado y respeta el tema de la app.

Convertir la ficha en tarjeta es envolver la `Column` existente:

```kotlin
@Composable
fun FichaPokemon(name: String, numero: Int, tipo: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(name)
            Text("N.º $numero")
            Text("Tipo: $tipo")
        }
    }
}
```

Dos detalles nuevos, y los dos son ORO:

**1. `modifier.fillMaxWidth()` — encadenar sobre lo recibido.** El `modifier`
que llega del llamador no se reemplaza: se **extiende**. La cadena queda
"las instrucciones de afuera, Y ADEMÁS ancho completo". Así el componente
suma lo suyo sin pisar lo que el llamador pidió.

**2. Ahora hay DOS modifiers, en dos niveles distintos.** El parámetro
`modifier` va al **contenedor raíz** (`Card`) — es la cara del componente
hacia afuera. La `Column` interna arranca un `Modifier` **nuevo** con su
`padding(16.dp)` — espacio interno ENTRE el borde de la tarjeta y el
contenido. Es la diferencia entre `margin` (afuera de la caja) y `padding`
(adentro), que en Compose se logra según **dónde** aplicás el padding:
antes del fondo, es margen; después, es relleno.

> 💡 En Compose **no existe `margin`**. Un `padding` aplicado al contenedor
> de afuera (o antes del fondo en la cadena) cumple ese rol. Otro motivo por
> el que el orden de la cadena importa.

### Ejercicio 1

1. Convertí tu `FichaPokemon` en tarjeta: `Card` envolviendo la `Column`,
   como arriba. Import con Alt+Enter (`androidx.compose.material3.Card`).
2. Las tarjetas quedan pegadas a los bordes de la pantalla. Arreglalo desde
   `setContent`, encadenando un segundo padding después del `innerPadding`:
   `Modifier.padding(innerPadding).padding(horizontal = 16.dp)`.
   Fijate que `padding` acepta argumentos con nombre: `horizontal`,
   `vertical`, o los cuatro lados por separado.
3. Mirá los dos previews: el de la ficha sola y el de la lista. ¿Qué cambió
   en cada uno sin tocarlos?
4. Corré la app: tarjetas con sombra, separadas del borde.

<details>
<summary>Ver una solución</summary>

```kotlin
// El componente, ahora tarjeta:
@Composable
fun FichaPokemon(name: String, numero: Int, tipo: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(name)
            Text("N.º $numero")
            Text("Tipo: $tipo")
        }
    }
}

// En setContent, la cadena de dos paddings:
Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
    ListaPokemon(
        pokemones = pokebola,
        modifier = Modifier
            .padding(innerPadding)              // 1º: esquivar las barras del sistema
            .padding(horizontal = 16.dp),       // 2º: aire a los costados
    )
}
```

La respuesta a la pregunta 3: **los dos previews cambiaron solos**, porque
llaman al mismo `FichaPokemon`. Un componente se cambia una vez y todos sus
usuarios se actualizan — previews, lista y app. Ese es el poder de los
componentes.

> ⚠️ **Error común**: poner la cadena de paddings en el `modifier` del
> propio `Scaffold`, usando `innerPadding` antes de que exista. `innerPadding`
> **nace como parámetro de la lambda** — solo existe dentro de las llaves
> `{ innerPadding -> ... }`, igual que el parámetro de un callback de JS no
> existe fuera del callback. Además el `Scaffold` es la pantalla entera: él
> conserva su `fillMaxSize()`; el padding es del contenido.

</details>

## 2. La tipografía del tema: jerarquía visual

Mirá tu tarjeta: nombre, número y tipo se ven **idénticos**. El ojo no sabe
qué es lo importante. Toda interfaz necesita **jerarquía**: el título grande,
lo secundario discreto.

La solución NO es hardcodear tamaños de fuente. Es usar la **escala
tipográfica del tema**, que viene con `MaterialTheme`:

```kotlin
Text(name, style = MaterialTheme.typography.titleLarge)
Text(
    "N.º $numero",
    style = MaterialTheme.typography.labelMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
)
Text("Tipo: $tipo", style = MaterialTheme.typography.bodyMedium)
```

`MaterialTheme.typography` es un catálogo de estilos con nombre —
`titleLarge`, `bodyMedium`, `labelSmall`… — definidos una sola vez en el
tema (viven en `ui/theme/Type.kt`, esa carpeta que el proyecto trae desde
el día uno). Si venís de la web: son **design tokens**, como usar
`var(--font-title)` en vez de `font-size: 22px` suelto en cada elemento.

¿Por qué importa?

- **Consistencia**: todos los títulos de la app se ven iguales, gratis.
- **Un solo lugar para cambiar**: ajustás el tema y la app entera se
  actualiza.
- **Modo oscuro gratis**: lo mismo vale para los colores —
  `MaterialTheme.colorScheme.onSurfaceVariant` es "el color para texto
  secundario **de este tema**", que en modo oscuro cambia solo. Un color
  hardcodeado, no.

> 💡 Si alguna vez necesitás un tamaño de letra a mano, la unidad es **`sp`**
> (*scale-independent pixels*): como `dp`, pero además respeta el tamaño de
> fuente que el usuario configuró en su teléfono (accesibilidad). Regla:
> `dp` para medidas, `sp` para texto. Usando la tipografía del tema, casi
> nunca lo vas a escribir.

### El catálogo completo, para explorar

La escala tipográfica de Material 3 son **5 roles × 3 tamaños = 15 estilos**:

| Rol | Para qué | Variantes |
|---|---|---|
| `display` | Texto gigante de impacto (números hero, portadas) | `displayLarge/Medium/Small` |
| `headline` | Encabezados de sección | `headlineLarge/Medium/Small` |
| `title` | Títulos de énfasis medio — tarjetas, diálogos | `titleLarge/Medium/Small` |
| `body` | Texto corrido, párrafos | `bodyLarge/Medium/Small` |
| `label` | Botones, etiquetas, texto utilitario chico | `labelLarge/Medium/Small` |

**Documentación oficial**:

- [Material 3 — Type scale](https://m3.material.io/styles/typography/type-scale-tokens):
  la escala completa con tamaños, pesos e interlineados de cada estilo.
- [Compose + Material 3](https://developer.android.com/develop/ui/compose/designsystems/material3):
  cómo se usa el sistema (tipografía, colores, formas) desde Compose.

### Ejercicio 2

1. Aplicale estilos del tema a los tres `Text` de tu ficha: `titleLarge`
   para el nombre, `labelMedium` + color `onSurfaceVariant` para el número,
   `bodyMedium` para el tipo (imports con Alt+Enter, como siempre).
2. Mirá el preview de la ficha: ¿se distingue ahora qué es lo importante?
3. Probá cambiar `titleLarge` por `headlineLarge` y volvé. Es un catálogo:
   explorá dos o tres opciones y quedate con la que te guste.
4. Corré la app.

<details>
<summary>Ver una solución</summary>

```kotlin
@Composable
fun FichaPokemon(name: String, numero: Int, tipo: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(name, style = MaterialTheme.typography.titleLarge)
            Text(
                "N.º $numero",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text("Tipo: $tipo", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
```

> 💡 En el editor, `MaterialTheme.typography.` + autocompletado lista los 15
> estilos — se prueban en el preview sin correr nada.

</details>

## 3. `Row` y el eje horizontal

Mirá una Pokédex de verdad: el número no va debajo del nombre — va **al
costado**, como una etiqueta. Para acomodar en horizontal ya sabés que
existe `Row`; ahora lo usamos en serio.

El plan para la ficha: una fila con el nombre a la izquierda y el número a
la derecha, y el tipo debajo:

```kotlin
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
        Text(
            "N.º $numero",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    Text("Tipo: $tipo", style = MaterialTheme.typography.bodyMedium)
}
```

Todo lo nuevo acá es flexbox con nombres de Compose:

| Compose | CSS flexbox | Qué hace |
|---|---|---|
| `horizontalArrangement = Arrangement.SpaceBetween` | `justify-content: space-between` | primer hijo al inicio, último al final |
| `verticalAlignment = Alignment.CenterVertically` | `align-items: center` | centra los hijos en el eje cruzado |

Dos detalles que muerden:

- **El `Row` necesita `fillMaxWidth()`**: sin ancho completo, la fila mide
  solo lo que ocupan sus hijos y `SpaceBetween` no tiene espacio que
  repartir — se ve todo pegado y parece que "no anda".
- En `Row`, los nombres se invierten respecto de `Column`: el eje principal
  es horizontal (`horizontalArrangement`) y el cruzado vertical
  (`verticalAlignment`). En `Column` es al revés. La lógica: *Arrangement*
  siempre reparte sobre el eje en que el contenedor apila; *Alignment*
  alinea en el otro.

### Ejercicio 3

1. Reestructurá tu ficha: `Row` con nombre + número (como arriba), tipo
   debajo. Imports: `Row` y `Alignment` con Alt+Enter.
2. Antes de mirar el preview, predecí: ¿dónde va a quedar cada texto?
3. Ahora sí, preview. Después borrá `Modifier.fillMaxWidth()` del `Row` y
   mirá qué pasa con el número. Volvé a ponerlo.
4. Corré la app.

<details>
<summary>Ver una solución</summary>

La ficha completa después de la reestructura:

```kotlin
@Composable
fun FichaPokemon(name: String, numero: Int, tipo: String, modifier: Modifier = Modifier) {
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
                Text(
                    "N.º $numero",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text("Tipo: $tipo", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
```

Y el resultado del experimento del paso 3: al borrar `fillMaxWidth()`,
**nombre y número quedan pegados**. El `Row` sin ancho mide solo lo que
ocupan sus hijos, y `SpaceBetween` no tiene espacio sobrante que repartir.
Con `fillMaxWidth()`, el espacio sobrante va ENTRE los hijos y empuja el
número a la derecha — como un flex container con `width: auto` vs
`width: 100%`.

</details>

## 4. `LazyColumn`: la lista que escala

En el capítulo 03 dejamos un aviso: *"`Column` dibuja TODOS sus hijos,
incluso los que no entran en pantalla"*. Llegó el momento de cobrarlo.

Tu `Column` actual tiene dos problemas que todavía no viste porque tenés
pocos Pokémon:

1. **No scrollea.** Agregá 20 Pokémon a la lista: los que no entran en la
   pantalla simplemente quedan cortados, y no hay forma de verlos.
2. **Compone todo.** Con 151 Pokémon (la Pokédex original completa), Compose
   construiría las 151 tarjetas de entrada, aunque solo 6 se vean. Memoria y
   tiempo desperdiciados.

**`LazyColumn`** resuelve los dos: compone **solo lo visible** (y va
creando/reciclando a medida que scrolleás) y trae el **scroll incorporado**.
Es la virtualización de listas que en React aportan librerías como
`react-window` — acá viene de fábrica.

El cambio en `ListaPokemon` es chico pero la sintaxis cambia:

```kotlin
@Composable
fun ListaPokemon(pokemones: List<Pokemon>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(pokemones) { pokemon ->
            FichaPokemon(
                name = pokemon.name,
                numero = pokemon.numero,
                tipo = pokemon.tipo,
            )
        }
    }
}
```

La diferencia clave: dentro de `LazyColumn` **no se llaman composables
directamente** — se **describen** los elementos con `items(lista) { ... }`.
Tiene sentido: si la lista compusiera todo de una, no sería lazy. `items` le
dice "estos son los datos; llamá a esta lambda solo para los que haga falta
dibujar". El `forEach` desaparece: `items` ES el bucle, pero perezoso.

> ⚠️ **La trampa del import**: hay varios `items` posibles y Android Studio
> a veces ofrece el equivocado. Si `items(pokemones) { ... }` no compila con
> un error raro de tipos, revisá que el import sea
> `androidx.compose.foundation.lazy.items` (la variante que recibe una
> `List`).

### Ejercicio 4

1. Cambiá la `Column` de `ListaPokemon` por `LazyColumn` con `items`, como
   arriba. Ojo con el import de `items`.
2. Agregale a `pokebola` Pokémon hasta pasar los diez — vale inventar
   niveles… digo, buscar los números reales en la Pokédex. 😄
3. Corré la app y **scrolleá**. Fijate que el scroll ya rebota y frena solo:
   comportamiento nativo, gratis.
4. Pregunta para pensar (la respondemos en el próximo capítulo): si tocás
   una tarjeta, ¿cómo sabría la app CUÁL tocaste?

<details>
<summary>Ver una solución</summary>

```kotlin
@Composable
fun ListaPokemon(pokemones: List<Pokemon>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(pokemones) { pokemon ->
            FichaPokemon(
                name = pokemon.name,
                numero = pokemon.numero,
                tipo = pokemon.tipo,
            )
        }
    }
}
```

Y la respuesta a la pregunta 4, adelantada: **el closure**. Cada tarjeta se
dibuja dentro de `items(pokemones) { pokemon -> ... }`, así que su lambda ya
"recuerda" a SU Pokémon — cuando le agreguemos un click, será
`onClick = { seleccionar(pokemon) }`, sin condicionales ni búsquedas. Igual
que `onClick={() => seleccionar(pokemon)}` dentro de un `.map()` de React.

> ⚠️ **Error común**: al reestructurar la ficha, perder la `Column` interna
> de la `Card` sin darse cuenta. Compila y hasta se apila igual — el
> contenido de `Card` es internamente un scope de columna — pero el
> `padding(16.dp)` y el `spacedBy(4.dp)` que viajaban en esa `Column`
> desaparecen en silencio: el texto queda pegado a los bordes. Moraleja: si
> un refactor "funciona" pero se ve distinto, buscá qué contenedor se cayó.

</details>

## Checkpoint

- [ ] Sé qué es Material Design y reconozco qué componentes de `material3`
      ya venía usando.
- [ ] Puedo envolver un componente en `Card`, y entiendo por qué el
      `modifier` recibido va a la raíz y se **extiende**
      (`modifier.fillMaxWidth()`).
- [ ] Entiendo que en Compose no hay `margin`: el rol lo cumple un padding
      aplicado por fuera, y el orden de la cadena decide.
- [ ] Uso la tipografía y los colores del tema (`MaterialTheme.typography`,
      `colorScheme`) en vez de tamaños y colores hardcodeados.
- [ ] Sé armar una fila con `Row` + `SpaceBetween` + `CenterVertically`, y
      por qué sin `fillMaxWidth()` "no anda".
- [ ] Puedo explicar qué hace `LazyColumn` distinto de `Column`, y escribir
      `items(lista) { ... }` con el import correcto.

**Anterior**: [03 — Primera pantalla con Compose](/pokedex/capitulos/03-primera-pantalla/) · **Siguiente**: [05 — Estado y recomposición](/pokedex/capitulos/05-estado/)
