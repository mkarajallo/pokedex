---
title: 02 — Kotlin esencial
description: El lenguaje desde cero, comparado con JavaScript — variables, funciones, clases y null safety.
---

**Al terminar este capítulo vas a poder**: leer y escribir Kotlin básico — variables,
funciones, clases y null safety — entendiendo en qué se parece y en qué se diferencia
de JavaScript.

Para practicar sin tocar el proyecto usamos el **[Kotlin Playground](https://play.kotlinlang.org)**:
un editor online donde el código corre al instante (como CodePen, pero para Kotlin).
Todo programa arranca en `fun main() { }` — el punto de entrada, como el script principal en JS.

---

## 1. Variables: `val` y `var`

En JS tenés `const` y `let`. Kotlin tiene lo mismo, con otros nombres:

```kotlin
val pokemon = "Pikachu"   // val = const → NO se puede reasignar
var nivel = 10            // var = let  → SÍ se puede reasignar
nivel += 10               // ok (también existe nivel++)
pokemon = "Raichu"        // ❌ ERROR de compilación
```

La diferencia GIGANTE con JS: **Kotlin tiene tipos**, como TypeScript. No los escribimos
arriba porque Kotlin los **infiere**: sabe que `pokemon` es `String` y `nivel` es `Int`.
También se pueden escribir explícitos, aunque casi nunca hace falta:

```kotlin
val pokemon: String = "Pikachu"
val nivel: Int = 10
```

**Template strings**: en Kotlin van con `$variable` dentro de comillas normales — sin backticks:

```kotlin
println("$pokemon está en el nivel $nivel")
// para expresiones: "${nivel + 1}"
```

**Regla de oro** (igual que en JS moderno): usá `val` siempre que puedas; `var` solo
cuando de verdad necesitás reasignar. El compilador te ayuda: si un `var` nunca se
reasigna, te lo subraya con la advertencia *"Variable is never modified, so it can be
declared with 'val'"*. En JS nadie te avisa — en Kotlin, leé siempre las advertencias.

### Ejercicio 1

1. Un `val` con tu Pokémon favorito.
2. Un `var` con su nivel; subilo una línea después con `+=`.
3. Imprimilos con un template string.

<details>
<summary>Ver una solución</summary>

```kotlin
fun main() {
    val pokemon = "Pikachu"
    var nivel = 10
    nivel += 10

    println("$pokemon está en el nivel $nivel")
}
```

</details>

## 2. Funciones

En JS: `function`. En Kotlin: `fun`. Dos diferencias clave: **los tipos de los
parámetros son obligatorios**, y el tipo de retorno va después de los paréntesis:

```kotlin
//  parámetro: nombre: Tipo      ↓ tipo de retorno
fun subirNivel(nivel: Int, cantidad: Int = 1): Int {
    return nivel + cantidad
}
```

El `cantidad: Int = 1` es un **valor por defecto**, igual que en JS.

Si la función es una sola expresión, existe la forma corta — el equivalente de una
arrow function:

```kotlin
fun subirNivel(nivel: Int, cantidad: Int = 1) = nivel + cantidad
```

Sin llaves, sin `return`, y el tipo de retorno se infiere.
Es el gemelo de `const subirNivel = (nivel, cantidad = 1) => nivel + cantidad`.

**Argumentos con nombre** — esto en JS no existe (se simula pasando objetos) y en
Compose se usa HASTA EL CANSANCIO: cualquier función se puede llamar nombrando sus
parámetros, en cualquier orden:

```kotlin
subirNivel(cantidad = 5, nivel = 10)
```

### Ejercicio 2

Escribí una función `describir` que reciba `pokemon: String` y `nivel: Int` y
**devuelva** (no imprima) `"Pikachu está en el nivel 20"` con template string.
Llamala desde `main` e imprimí el resultado. Probala en las dos formas: con
llaves + `return`, y como expresión con `=`.

> ⚠️ **Error común**: `return println(...)` no compila — *"Type mismatch: inferred
> type is Unit but String was expected"*. `println` imprime y devuelve `Unit`
> (el `void`/`undefined` de Kotlin), no el texto. Es el mismo error que
> `return console.log(...)` en JS, solo que acá el compilador te frena antes de
> ejecutar. La función **produce** el valor; quien la llama decide qué hacer con él.

> 💡 En la forma expresión, el tipo de retorno también se puede omitir:
> `fun describir(pokemon: String, nivel: Int) = "..."` — se infiere de la expresión.

<details>
<summary>Ver una solución</summary>

```kotlin
fun main() {
    val pokemon = "Pikachu"
    var nivel = 10
    nivel += 10

    println(describir(pokemon, nivel))
}

// Forma 1: con llaves y return
fun describir(pokemon: String, nivel: Int): String {
    return "$pokemon está en el nivel $nivel"
}

// Forma 2: expresión (equivalente — elegí una)
// fun describir(pokemon: String, nivel: Int) = "$pokemon está en el nivel $nivel"
```

</details>

## 3. Null safety

El famoso `Cannot read properties of undefined` de JS, en Kotlin **no existe como
sorpresa en runtime** — el sistema de tipos lo elimina. Es LA razón por la que
Kotlin existe.

En Kotlin, un tipo normal **no puede ser null**:

```kotlin
val pokemon: String = null    // ❌ ERROR de compilación
```

Si algo PUEDE ser null, lo declarás con `?` — y es un tipo DISTINTO:

```kotlin
val apodo: String? = null     // String? = "String o null"
```

Y acá la magia: el compilador **te obliga** a manejar el null antes de usarlo:

```kotlin
println(apodo.length)     // ❌ no compila: apodo puede ser null
println(apodo?.length)    // ✅ safe call → imprime null si apodo es null
```

Las herramientas (las dos primeras ya las conocés de JS moderno):

| Kotlin | JS | Qué hace |
|---|---|---|
| `apodo?.length` | `apodo?.length` | Optional chaining: si es null, devuelve null |
| `apodo ?: "sin apodo"` | `apodo ?? "sin apodo"` | Elvis operator: valor por defecto si es null |
| `apodo!!.length` | — | "Confiá en mí, no es null". Si lo es → crash. **NO usar.** |

```kotlin
val apodo: String? = null
val mostrado = apodo ?: "sin apodo"   // mostrado es String (ya no String?)
println(mostrado.length)              // ✅ compila: el null ya fue manejado
```

**La regla**: si ves `!!` en tu código, casi seguro estás haciendo algo mal.
El objetivo es que el compilador PRUEBE que no hay null, no silenciarlo.

### Ejercicio 3

1. Declará `val apodo: String? = null`.
2. Imprimí su longitud con safe call (`?.`) — mirá qué sale.
3. Creá `val mostrado` con elvis (`?:`) y un valor por defecto, e imprimí
   `mostrado.length`.
4. Cambiá el `null` inicial por un texto y volvé a correr: mirá cómo cambian los dos.

<details>
<summary>Ver una solución</summary>

```kotlin
fun main() {
    val apodo: String? = null
    println(apodo?.length)              // null — safe call sin crash

    val mostrado = apodo ?: "sin apodo"
    println(mostrado.length)            // 9 — la longitud del default
    println(mostrado)                   // sin apodo

    val apodo2: String? = "Rayo"
    val mostrado2 = apodo2 ?: "sin apodo"
    println(mostrado2.length)           // 4 — el elvis dejó pasar el valor real
    println(mostrado2)                  // Rayo
}
```

> ⚠️ **Error común**: `val x ?: "default"` no compila. El elvis no declara
> variables — combina dos valores: `val x = posibleNull ?: "default"`
> (como `const x = valor ?? "default"` en JS). Y aplicarlo sobre un tipo
> no-nullable (`String`) es inútil: el compilador lo advierte.

</details>

## 4. Clases y data classes

En JS las clases son opcionales; en Kotlin son el pan de cada día. La sintaxis es
mucho más compacta: el **constructor primario** va en la misma línea que el nombre,
y declarar los parámetros con `val`/`var` los convierte automáticamente en
**propiedades**:

```kotlin
class Pokemon(val nombre: String, var nivel: Int)

fun main() {
    val pikachu = Pokemon("Pikachu", 10)   // sin new — se llama como función
    println(pikachu.nombre)                 // Pikachu
    pikachu.nivel += 1                      // nivel es var → se puede cambiar
}
```

El equivalente JS necesita mucho más ruido:

```js
class Pokemon {
    constructor(nombre, nivel) {
        this.nombre = nombre;
        this.nivel = nivel;
    }
}
const pikachu = new Pokemon("Pikachu", 10);
```

Fijate: **no existe `new`** en Kotlin, y no hubo que escribir `this.x = x` ni una vez.

### `data class`: la estrella del proyecto

Para clases que solo **transportan datos**, Kotlin tiene una palabra mágica:

```kotlin
data class Pokemon(val nombre: String, val nivel: Int)
```

Ese `data` le regala a la clase:

- **`toString()` útil**: `println(pikachu)` → `Pokemon(nombre=Pikachu, nivel=10)` en
  vez de una referencia críptica.
- **Igualdad por contenido**: `Pokemon("Pikachu", 10) == Pokemon("Pikachu", 10)` es
  `true`. En JS, dos objetos con el mismo contenido NO son iguales (`{} !== {}`).
- **`copy()`**: crea una copia cambiando solo lo que nombres —
  `pikachu.copy(nivel = 11)` — como el spread de JS: `{ ...pikachu, nivel: 11 }`.

¿Por qué importa TANTO? Porque la Pokédex va a estar llena de data classes: los
DTOs de la API, los modelos de dominio, los estados de la UI. Y `copy()` es el
corazón del manejo de estado inmutable en Compose — el mismo patrón que
`setState({ ...state, x })` en React.

### ¿`class` o `data class`?

`data class` **no es otro tipo de clase** — es una clase normal a la que la palabra
`data` le agrega código generado por el compilador. Equivale a decirle: *"esta clase
solo contiene datos — generá automáticamente el código repetitivo"*.

La prueba: sacale el `data` a `Pokemon` y corré lo mismo:

```kotlin
class Pokemon(val nombre: String, val nivel: Int, val tipo: String)

println(pikachu)     // Pokemon@1b6d3586  ← referencia críptica, no tus datos
// pikachu.copy()    // ❌ copy() ni siquiera existe
// pikachu == otro   // compara REFERENCIAS (como en JS), no contenido
```

La misma clase, sin los regalos. Con `data`, el compilador escribe por detrás el
`toString()`, el `equals()` por contenido y el `copy()` que en JS harías a mano.

**¿Cuándo usar cada una?** Preguntate: *¿esta clase ES datos o HACE cosas?*

| | Ejemplos | Por qué |
|---|---|---|
| **ES datos** → `data class` | un Pokémon, una respuesta de la API, el estado de una pantalla | los querés imprimir, comparar y copiar |
| **HACE cosas** → `class` | un repositorio que busca Pokémon, un ViewModel | tienen comportamiento; comparar o copiar dos repositorios no significa nada |

### Ejercicio 4

1. Creá una `data class Pokemon` con `nombre: String`, `nivel: Int` y
   `tipo: String`.
2. En `main`, creá un `pikachu` y hacé `println(pikachu)` — mirá el `toString`
   gratis.
3. Creá `raichu` usando `pikachu.copy(...)` cambiando nombre y nivel.
4. Comprobá con `==` si `pikachu` es igual a `pikachu.copy()` sin cambios.
   ¿Qué esperás que dé?

<details>
<summary>Ver una solución</summary>

```kotlin
data class Pokemon(val nombre: String, val nivel: Int, val tipo: String)

fun main() {
    val pikachu = Pokemon("Pikachu", 10, "Electricidad")
    println(pikachu)      // Pokemon(nombre=Pikachu, nivel=10, tipo=Electricidad)

    val raichu = pikachu.copy(nombre = "Raichu", nivel = 15)
    println(raichu)       // Pokemon(nombre=Raichu, nivel=15, tipo=Electricidad)

    println(pikachu == pikachu.copy())   // true — igualdad por CONTENIDO
}
```

> 💡 Dos detalles de estilo que acá son ley: propiedades con **`val`**
> (inmutable — un Pokémon distinto se crea con `copy()`, no mutando), y `copy()`
> con **argumentos con nombre**, nombrando solo lo que cambia — `tipo` se copia solo.

</details>

## 5. Colecciones y lambdas

Las listas de Kotlin se van a sentir familiares: `map`, `filter` y compañía
existen igual que en JS. Antes de usarlas, hay que entender bien qué es una
lambda — el concepto sobre el que se apoya todo lo demás (incluido Compose).

### ¿Qué es una lambda?

**Una función sin nombre que se usa como un valor**: se guarda en una variable
o se pasa como argumento a otra función. En JavaScript se usan todo el tiempo,
aunque no siempre con ese nombre:

```js
[1, 2, 3].map(n => n * 2)   // n => n * 2 ES una lambda
```

**¿Para qué sirve?** `map` recorre la lista, pero no sabe qué hacer con cada
elemento. La lambda es la "receta" que se le pasa: `map` la aplica a cada uno.
Las lambdas sirven para **pasarle comportamiento a otra función**.

Como es un valor, una lambda puede guardarse en una variable y llamarse después:

```kotlin
val duplicar = { n: Int -> n * 2 }   // la variable guarda una FUNCIÓN
println(duplicar(4))                 // 8 — se invoca con paréntesis
```

¿Cómo sabe `duplicar` que `4` va a `n`? Igual que en cualquier función: los
argumentos se asignan en orden a los parámetros declarados antes de la flecha.
Es el equivalente exacto de `const duplicar = n => n * 2` en JS.

El tipo de esa variable es un **tipo de función**, y puede escribirse explícito:

```kotlin
val duplicar: (Int) -> Int = { n -> n * 2 }
//            ↑ "función que recibe un Int y devuelve un Int"
```

Con ese tipo, el compilador rechaza `duplicar("hola")` — las funciones también
están tipadas.

### La sintaxis, paso a paso

En Kotlin la lambda va entre **llaves**, con `->` en vez de `=>`. Estas cinco
líneas hacen exactamente lo mismo — es la misma lambda, simplificada de a un
paso:

```kotlin
val numeros = listOf(1, 2, 3)

numeros.map({ n: Int -> n * 2 })   // 1. forma completa
numeros.map({ n -> n * 2 })        // 2. el tipo se infiere
numeros.map() { n -> n * 2 }       // 3. último argumento → sale del paréntesis
numeros.map { n -> n * 2 }         // 4. paréntesis vacíos → se eliminan
numeros.map { it * 2 }             // 5. un solo parámetro → se llama "it"
```

Los pasos 3–4 son el **trailing lambda**; el 5 es el parámetro implícito **`it`**.

**"Idiomática"** significa: la forma en que la comunidad del lenguaje escribe por
convención. En JS pasa igual — `function(n) { return n * 2 }` y `n => n * 2`
hacen lo mismo, pero todo el código real usa la flecha. En Kotlin, la forma
idiomática es la 5: `numeros.map { it * 2 }`.

### Listas: la inmutabilidad es explícita

En JS, todo array acepta `push`. En Kotlin, el tipo decide:

```kotlin
val equipo = listOf("Pikachu", "Charmander")   // List: NO se puede modificar
val caja = mutableListOf("Squirtle")           // MutableList: sí
caja.add("Bulbasaur")                          // ok
equipo.add("Mewtwo")                           // ❌ add() no existe en List
```

La regla es la misma que con `val`/`var`: **`listOf` salvo que se necesite mutar**.

Las operaciones que ya conocés, lado a lado:

| Kotlin | JS |
|---|---|
| `lista.map { it.nombre }` | `lista.map(p => p.nombre)` |
| `lista.filter { it.nivel > 10 }` | `lista.filter(p => p.nivel > 10)` |
| `lista.forEach { println(it) }` | `lista.forEach(p => console.log(p))` |
| `lista.find { it.nombre == "Pikachu" }` | `lista.find(p => p.nombre === "Pikachu")` |
| `lista.any { it.nivel > 50 }` | `lista.some(p => p.nivel > 50)` |
| `lista.sumOf { it.nivel }` | `lista.reduce((a, p) => a + p.nivel, 0)` |

> 💡 El **trailing lambda** es la base visual de Compose: `Column { Text("Hola") }`
> es una función que recibe una lambda como último argumento. Toda la UI se
> construye con esta sintaxis.

### Ejercicio 5

Partiendo de la `data class Pokemon` del ejercicio anterior:

1. Creá una `val equipo = listOf(...)` con al menos 4 Pokémon de distintos
   niveles y tipos.
2. Obtené los nombres de los que tienen `nivel > 10` (encadenando `filter` y
   `map`) e imprimilos.
3. Calculá la suma de niveles del equipo con `sumOf`.
4. Intentá hacer `equipo.add(...)` y leé el error del compilador.

<details>
<summary>Ver una solución</summary>

```kotlin
data class Pokemon(val nombre: String, val nivel: Int, val tipo: String)

fun main() {
    val equipo = listOf(
        Pokemon("Pikachu", 5, "Eléctrico"),
        Pokemon("Gengar", 36, "Fantasma / Veneno"),
        Pokemon("Lucario", 55, "Lucha / Acero"),
        Pokemon("Rayquaza", 70, "Dragón / Volador"),
    )

    val fuertes = equipo.filter { it.nivel > 10 }
    println(fuertes)                    // los 3 con nivel > 10

    val nombres = fuertes.map { it.nombre }
    println(nombres)                    // [Gengar, Lucario, Rayquaza]

    // Encadenado directo, sin variable intermedia:
    println(equipo.filter { it.nivel > 10 }.map { it.nombre })

    val totalNiveles = equipo.sumOf { it.nivel }
    println(totalNiveles)               // 166

    // equipo.add(Pokemon("Mewtwo", 90, "Psíquico"))
    // ❌ "unresolved reference: add" — List es de solo lectura:
    // el tipo directamente no ofrece esa operación. Para listas
    // editables existe mutableListOf().
}
```

</details>

## 6. `if` y `when` como expresiones

En JS, `if` es una instrucción: hace algo, pero no vale nada. Por eso existe el
ternario (`cond ? a : b`) para asignar. En Kotlin no hay ternario, porque no
hace falta: **el `if` devuelve un valor**.

```kotlin
// JS:  const estado = nivel > 50 ? "élite" : "en formación";
val estado = if (nivel > 50) "élite" else "en formación"
```

Cuando un `if` se usa como expresión, el `else` es **obligatorio** — el valor
tiene que existir siempre.

### `when`: el `switch` mejorado

`when` reemplaza al `switch` de JS, sin sus problemas: no hay `break` (no existe
el *fallthrough* accidental), cada rama va con `->`, y también **devuelve un
valor**:

```kotlin
val descripcion = when (tipo) {
    "Eléctrico" -> "⚡ cuidado con los cables"
    "Fantasma", "Veneno" -> "🌙 mejor de noche"   // varias opciones por rama
    else -> "tipo $tipo"
}
```

Las ramas no se limitan a valores exactos — aceptan **rangos** y condiciones:

```kotlin
val rango = when (nivel) {
    in 1..15 -> "novato"        // in = ¿está dentro del rango?
    in 16..49 -> "intermedio"
    else -> "élite"
}
```

Usado como expresión, `when` también exige cubrir todos los casos (`else`).
El compilador garantiza que ninguna situación quede sin respuesta.

### Expresión o instrucción: las dos formas de usarlos

Tanto `if` como `when` pueden usarse de dos maneras, y conviene distinguirlas:

**Como instrucción** — para *hacer* algo (imprimir, llamar una función). No se
asigna a nada, y el `else` es opcional:

```kotlin
if (nivel > 50) {
    println("¡Cuidado con este Pokémon!")   // solo pasa algo si se cumple
}
```

**Como expresión** — para *producir un valor* que se asigna o se devuelve. El
`else` pasa a ser obligatorio, porque el valor tiene que existir en todos los
casos:

```kotlin
val estado = if (nivel > 50) "élite" else "en formación"
//  ↑ estado SIEMPRE necesita un valor; sin else, ¿qué valdría con nivel = 10?
```

La misma regla aplica a `when`: si su resultado se asigna o se devuelve, el
compilador exige cubrir todos los casos.

### ¿Cuándo usar `if` y cuándo `when`?

| Situación | Conviene | Ejemplo |
|---|---|---|
| Una condición y su alternativa | `if`/`else` | `if (nivel > 50) "élite" else "resto"` |
| Dos o más rangos o categorías | `when` | los rangos de `rango(nivel)` |
| Comparar contra valores concretos | `when` | `when (tipo) { "Eléctrico" -> ... }` |
| Condiciones sin un valor común que comparar | `when` sin argumento | ver abajo |

`when` también funciona **sin argumento**: cada rama es una condición completa,
y reemplaza a una cadena de `else if` de forma más prolija:

```kotlin
val consejo = when {
    nivel < 10 && tipo == "Eléctrico" -> "entrenalo en la pradera"
    nivel < 10 -> "todavía es débil"
    else -> "listo para combatir"
}
```

Regla práctica: con **una** condición, `if`. Con **varias**, `when` — se lee de
arriba hacia abajo como una tabla de decisión, gana la primera rama verdadera.

### Errores comunes al empezar

Los tres errores siguientes aparecieron al resolver esta sección por primera
vez — son típicos viniendo de JavaScript:

**1. Declarar una función con `val`.**

```kotlin
val rango(nivel: Int): String { ... }   // ❌ val declara VARIABLES
fun rango(nivel: Int): String { ... }   // ✅ fun declara FUNCIONES
```

Además, los parámetros no llevan `val`: se escribe `nivel: Int` a secas
(dentro de la función ya son inmutables).

**2. Encadenar condiciones sin repetir el `if`.**

```kotlin
if (nivel <= 15) "novato" else (nivel <= 49) "intermedio"   // ❌ no compila
if (nivel <= 15) "novato" else if (nivel <= 49) "intermedio" else "élite"  // ✅
```

Después de un `else` solo puede venir un valor final u **otro `if` completo**.
Una condición suelta entre paréntesis no significa nada.

**3. Strings casi iguales que no son iguales.**

```kotlin
"élite" == "elite"   // false — para el compilador son dos textos distintos
```

Si dos funciones deben devolver las mismas categorías, los textos tienen que
coincidir carácter por carácter (tildes incluidas). Más adelante veremos que
para categorías fijas existe una herramienta mejor que los strings: los `enum`.

### Ejercicio 6

Sobre el equipo del ejercicio anterior:

1. Escribí una función `rango(nivel: Int): String` usando `when` con rangos:
   `"novato"` hasta 15, `"intermedio"` de 16 a 49, `"élite"` de 50 en adelante.
2. Recorré el equipo con `forEach` imprimiendo `"Lucario es élite"` para cada
   Pokémon, usando la función.
3. Reescribí `rango` usando solo `if`/`else if`/`else` como expresión (un único
   `=`). Comparalas: ¿cuál se lee mejor?

<details>
<summary>Ver una solución</summary>

```kotlin
fun rango(nivel: Int): String {
    return when (nivel) {
        in 1..15 -> "novato"
        in 16..49 -> "intermedio"
        else -> "élite"
    }
}

fun rango2(nivel: Int) =
    if (nivel <= 15) "novato" else if (nivel <= 49) "intermedio" else "élite"

fun main() {
    // ... el equipo del ejercicio 5 ...
    equipo.forEach { println("${it.nombre} es ${rango(it.nivel)}") }
}
```

> ⚠️ **Error común**: `else (condición) valor` no compila. Después de un `else`
> va un valor final u otro `if` completo: `else if (condición) valor`.

> 💡 Para rangos numéricos, `when` suele ser más legible: cada rama declara su
> rango completo, mientras que el `if` encadenado depende del orden de lectura.

</details>

## Checkpoint

- [ ] Sé cuándo usar `val` y cuándo `var`, y por qué el compilador sugiere `val`.
- [ ] Puedo escribir una función con parámetros tipados, valores por defecto y
      en forma de expresión (`=`).
- [ ] Entiendo qué es `String?`, y sé usar `?.` y `?:` (y por qué evitar `!!`).
- [ ] Sé la diferencia entre `class` y `data class`, y qué regala `data`
      (`toString`, igualdad por contenido, `copy`).
- [ ] Puedo explicar qué es una lambda y leer `lista.map { it.algo }` sin dudar.
- [ ] Sé que `listOf` crea listas de solo lectura y `mutableListOf` editables.
- [ ] Puedo usar `if` y `when` como expresiones que devuelven un valor.

**Anterior**: [01 — El entorno](/capitulos/01-entorno/) · **Siguiente**: 03 — Primera pantalla con Compose *(próximamente)*

