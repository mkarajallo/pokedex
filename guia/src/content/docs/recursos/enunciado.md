---
title: Enunciado del TP
description: Los requisitos que la app Pokédex debe cumplir.
---

## 1. Objetivo

El objetivo de este mini proyecto es desarrollar una aplicación Android utilizando **Kotlin** y **Jetpack Compose**, aplicando los principales conceptos y patrones de arquitectura vistos durante el desarrollo.

La aplicación estará basada en la API pública **PokéAPI** y permitirá al usuario:

- Consultar un listado de Pokémon.
- Visualizar el detalle de un Pokémon.
- Agregar y quitar Pokémon de favoritos.
- Consultar la lista de favoritos.
- Mantener los favoritos almacenados localmente.

Este proyecto busca principalmente evaluar y practicar:

- Desarrollo de interfaces con **Jetpack Compose**.
- Manejo de **estados e intents**.
- Arquitectura **MVVM**.
- **Clean Architecture**.
- Inyección de dependencias con **Hilt**.
- Consumo de APIs REST utilizando **Retrofit**.
- Persistencia local utilizando **Room**.
- Navegación utilizando **Navigation Compose**.
- Separación de responsabilidades entre las diferentes capas de la aplicación.

---

## 2. API a utilizar

Para obtener la información de los Pokémon se utilizará **PokéAPI**.

- **API:** https://pokeapi.co/
- **Documentación:** https://pokeapi.co/docs/v2

No es necesario implementar ningún backend propio para este proyecto.

---

## 3. Funcionalidades

La aplicación deberá contar, como mínimo, con las siguientes funcionalidades.

### 3.1. Login

Al iniciar la aplicación se deberá mostrar una pantalla de login.

El login será **básico y local**, por lo que no será necesario realizar una autenticación contra un backend.

La pantalla deberá contener, como mínimo:

- Usuario.
- Contraseña.
- Botón para iniciar sesión.

Se puede definir una combinación de usuario y contraseña fija para permitir el acceso a la aplicación.

Por ejemplo:

```text
Usuario: admin
Contraseña: 123456
```

La validación deberá realizarse mediante la capa correspondiente de la arquitectura, evitando colocar lógica de negocio directamente dentro del `Composable`.

Una vez realizado correctamente el login, el usuario deberá acceder a la pantalla principal.

---

## 4. Pantalla principal

Luego del login se deberá mostrar una pantalla principal que contenga un **Bottom Navigation Bar**.

La navegación inferior deberá contar, como mínimo, con dos secciones:

- Pokémon
- Favoritos

Conceptualmente:

```text
┌──────────────────────────────────────┐
│                                      │
│          Contenido de pantalla       │
│                                      │
│                                      │
├──────────────────────────────────────┤
│     Pokémon           Favoritos      │
└──────────────────────────────────────┘
```

La implementación visual queda a criterio del desarrollador, siempre que permita navegar correctamente entre ambas secciones.

---

## 5. Listado de Pokémon

La sección de Pokémon deberá mostrar un listado obtenido desde **PokéAPI**.

Cada elemento deberá mostrar información básica del Pokémon, por ejemplo:

- Imagen.
- Nombre.
- Identificador.

Cada Pokémon deberá permitir:

- Acceder al detalle.
- Agregarlo a favoritos.
- Quitar de favoritos si ya se encuentra agregado.

La implementación queda a criterio del desarrollador, siempre que se mantenga una buena experiencia de usuario.

---

## 6. Detalle del Pokémon

Al seleccionar un Pokémon se deberá navegar a una pantalla de detalle.

La pantalla deberá mostrar información relevante obtenida desde PokéAPI.

Por ejemplo:

- Imagen.
- Nombre.
- ID.
- Tipos.
- Altura.
- Peso.
- Estadísticas.
- Habilidades.

No es necesario mostrar toda la información disponible en la API. Se deberá seleccionar información relevante para construir una pantalla de detalle clara.

Desde esta pantalla también deberá ser posible:

- Agregar el Pokémon a favoritos.
- Quitar el Pokémon de favoritos.

El estado de favorito deberá mantenerse sincronizado con:

- El listado de Pokémon.
- La pantalla de detalle.
- La pantalla de favoritos.

---

## 7. Favoritos

La sección de favoritos deberá mostrar los Pokémon que el usuario haya marcado como favoritos.

Los favoritos deberán almacenarse localmente utilizando **Room**.

Esto significa que la información deberá mantenerse incluso si la aplicación se cierra y vuelve a abrir.

La pantalla deberá permitir:

- Visualizar los Pokémon favoritos.
- Acceder al detalle.
- Quitar un Pokémon de favoritos.

> **Importante:** no se deberá depender únicamente de una variable en memoria para mantener los favoritos.

---

## 8. Arquitectura

El proyecto deberá utilizar una combinación de:

- **MVVM**
- **Clean Architecture**

La estructura deberá separar claramente las responsabilidades de cada capa.

Una posible organización sería:

```text
presentation/
├── login/
├── pokemon/
├── favorites/
├── detail/
└── navigation/

domain/
├── model/
├── repository/
└── usecase/

data/
├── remote/
│   ├── api/
│   └── dto/
├── local/
│   ├── dao/
│   ├── entity/
│   └── database/
└── repository/
```

La estructura exacta de paquetes puede variar, pero se deberá mantener una clara separación de responsabilidades.

---

## 9. Flujo de la aplicación

El flujo esperado para una funcionalidad será similar al siguiente:

```text
Composable
    │
    │ State + Intent
    ▼
ViewModel
    │
    ▼
UseCase
    │
    ▼
Repository
    │
    ├──────────────► Remote Data Source
    │                      │
    │                      ▼
    │                   Retrofit
    │                      │
    │                      ▼
    │                   PokéAPI
    │
    └──────────────► Local Data Source
                           │
                           ▼
                          Room
```

Cada capa deberá conocer únicamente las responsabilidades que le corresponden, evitando acoplamientos innecesarios entre UI, dominio y fuentes de datos.

---

## 10. Comunicación entre UI y ViewModel

Una consideración importante del proyecto será la forma en que la UI se comunique con el `ViewModel`.

El `Composable` no deberá contener lógica de negocio ni realizar directamente las operaciones de la aplicación.

La idea será que el `Composable` reciba:

- El estado de la pantalla.
- Un `Intent` para comunicar las acciones del usuario.

Por ejemplo:

```kotlin
@Composable
fun PokemonScreen(
    state: PokemonState,
    onIntent: (PokemonIntent) -> Unit
)
```

El `Composable` será responsable principalmente de:

1. Representar el estado recibido.
2. Capturar las acciones del usuario.
3. Enviar dichas acciones mediante `Intent`.

Por ejemplo:

```kotlin
Button(
    onClick = {
        onIntent(
            PokemonIntent.OnPokemonSelected(pokemon.id)
        )
    }
)
```

El procesamiento del `Intent` deberá realizarse en el `ViewModel`.

Conceptualmente:

```text
Usuario
   │
   ▼
Composable
   │
   │ Intent
   ▼
ViewModel
   │
   ▼
UseCase
```

---

## 11. Navigation y NavGraph

La navegación deberá implementarse utilizando la librería oficial de **Navigation para Android/Compose**.

El `NavGraph` será el encargado de definir las diferentes rutas de la aplicación.

El flujo principal será similar a:

```text
Login
  │
  ▼
Home
 ├── Pokémon
 │     └── Detalle Pokémon
 │
 └── Favoritos
       └── Detalle Pokémon
```

El `NavGraph` será también el punto desde el cual se conectará cada pantalla con su correspondiente `ViewModel` y se proporcionarán al `Composable` el `State` y los `Intent`.

Por ejemplo:

```kotlin
fun NavGraphBuilder.pokemonNavGraph(
    navController: NavHostController
) {

    composable(route = "login") {
        LoginScreen(
            onLogin = {
                navController.navigate("home")
            }
        )
    }

    composable(route = "home") {
        HomeScreen(
            onPokemonSelected = { pokemonId ->
                navController.navigate("pokemon/$pokemonId")
            }
        )
    }

    composable(
        route = "pokemon/{pokemonId}",
        arguments = listOf(
            navArgument("pokemonId") {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->

        val pokemonId =
            backStackEntry.arguments?.getInt("pokemonId") ?: 0

        val viewModel: PokemonDetailViewModel = hiltViewModel()
        val state by viewModel.state.collectAsState()

        PokemonDetailScreen(
            state = state,
            onIntent = viewModel::onIntent
        )
    }
}
```

La implementación exacta puede variar. Lo importante es mantener la separación entre:

- Navegación.
- `ViewModel`.
- Estado.
- UI.

### 11.1. Definición de rutas

Se recomienda evitar escribir las rutas directamente en diferentes partes de la aplicación.

En lugar de:

```kotlin
navController.navigate("pokemon/$pokemonId")
```

y:

```kotlin
composable("pokemon/{pokemonId}")
```

se puede centralizar la definición de las rutas.

Por ejemplo:

```kotlin
sealed class PokemonRoutes(
    val route: String
) {

    data object Login : PokemonRoutes(
        route = "login"
    )

    data object Home : PokemonRoutes(
        route = "home"
    )

    data object PokemonDetail : PokemonRoutes(
        route = "pokemon/{pokemonId}"
    )
}
```

Y para generar una ruta con parámetros:

```kotlin
fun PokemonRoutes.PokemonDetail.createRoute(
    pokemonId: Int
): String {
    return "pokemon/$pokemonId"
}
```

De esta manera, el código que realiza la navegación puede mantenerse más ordenado:

```kotlin
navController.navigate(
    PokemonRoutes.PokemonDetail.createRoute(pokemonId)
)
```

No es obligatorio utilizar exactamente esta implementación, pero se recomienda seguir un enfoque similar.

---

## 12. ViewModel

Cada funcionalidad relevante deberá contar con su correspondiente `ViewModel`.

El `ViewModel` será responsable de:

- Recibir los `Intent` provenientes de la UI.
- Ejecutar los casos de uso correspondientes.
- Mantener y actualizar el estado de la pantalla.
- Manejar estados de carga.
- Manejar errores.
- Exponer el estado a la UI.

El `ViewModel` **no deberá realizar directamente llamadas a Retrofit o Room**.

Por ejemplo:

```text
PokemonIntent
      │
      ▼
PokemonViewModel
      │
      ▼
GetPokemonsUseCase
      │
      ▼
PokemonRepository
```

---

## 13. Use Cases

La lógica de negocio deberá estar representada mediante **Use Cases**.

Algunos ejemplos podrían ser:

```text
LoginUseCase
GetPokemonsUseCase
GetPokemonDetailUseCase
GetFavoritePokemonsUseCase
AddPokemonToFavoriteUseCase
RemovePokemonFromFavoriteUseCase
IsPokemonFavoriteUseCase
```

Los `Use Cases` deberán ser inyectados en los `ViewModels` mediante **Hilt**.

El objetivo es evitar que el `ViewModel` conozca directamente los detalles de implementación de los repositorios.

---

## 14. Repository

El `Repository` será responsable de abstraer el origen de los datos.

Por ejemplo:

```kotlin
interface PokemonRepository {

    suspend fun getPokemons(): List<Pokemon>

    suspend fun getPokemonDetail(id: Int): Pokemon

    fun getFavorites(): Flow<List<Pokemon>>

    suspend fun addFavorite(pokemon: Pokemon)

    suspend fun removeFavorite(id: Int)

    fun isFavorite(id: Int): Flow<Boolean>
}
```

La interfaz deberá pertenecer a la **capa de dominio**.

La implementación concreta deberá pertenecer a la **capa de datos**.

Esto permite que el dominio no dependa directamente de:

- Retrofit.
- Room.
- DTOs.
- Entities.
- Otras tecnologías concretas.

Conceptualmente:

```text
Domain
   │
   │ PokemonRepository
   ▼
Data
   │
   ├── Remote Data Source
   │       └── Retrofit
   │
   └── Local Data Source
           └── Room
```

---

## 15. Retrofit

Para las comunicaciones con PokéAPI se deberá utilizar **Retrofit**.

Se deberá crear una interfaz que represente los endpoints necesarios de la API.

Por ejemplo:

```kotlin
interface PokemonApi {

    @GET("pokemon")
    suspend fun getPokemons(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): PokemonListResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(
        @Path("id") id: Int
    ): PokemonResponse
}
```

Los modelos utilizados para representar las respuestas de la API deberán mantenerse separados de los modelos utilizados por la capa de dominio.

Por ejemplo:

```text
PokemonDto
      │
      ▼
PokemonMapper
      │
      ▼
Pokemon
```

De esta manera, los cambios realizados en la respuesta de la API no deberían afectar directamente a la capa de dominio.

---

## 16. Room

**Room** deberá utilizarse para almacenar localmente los Pokémon marcados como favoritos.

Una posible entidad podría ser:

```text
PokemonEntity
```

Con información como:

```text
id
name
image
...
```

Un DAO podría contener operaciones como:

```kotlin
@Dao
interface PokemonDao {

    @Query("SELECT * FROM pokemon")
    fun getFavorites(): Flow<List<PokemonEntity>>

    @Insert(onConflict = REPLACE)
    suspend fun insert(pokemon: PokemonEntity)

    @Query("DELETE FROM pokemon WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM pokemon WHERE id = :id)")
    fun isFavorite(id: Int): Flow<Boolean>
}
```

La implementación final queda a criterio del desarrollador.

---

## 17. Inyección de dependencias

Se deberá utilizar **Hilt** para la inyección de dependencias.

Como mínimo, deberían ser proporcionadas mediante Hilt las dependencias principales de:

- Retrofit.
- API de PokéAPI.
- Room.
- DAO.
- Repository.
- Use Cases.
- ViewModels.

El objetivo es evitar instanciaciones manuales de estas dependencias dentro de las pantallas.

Por ejemplo, la UI no debería realizar algo como:

```kotlin
val retrofit = Retrofit.Builder()
    ...
    .build()
```

Las dependencias deberán ser proporcionadas por Hilt y recibidas por las clases que las necesiten.

---

## 18. Estados de pantalla

Las pantallas deberán manejar correctamente diferentes estados.

Por ejemplo:

```kotlin
data class PokemonState(
    val isLoading: Boolean = false,
    val pokemons: List<Pokemon> = emptyList(),
    val error: String? = null
)
```

La UI deberá reaccionar al estado recibido.

Conceptualmente:

```text
isLoading = true
        │
        ▼
Mostrar Loading


isLoading = false
pokemons = [...]
        │
        ▼
Mostrar listado


error != null
        │
        ▼
Mostrar error
```

Se deberá evitar manejar este tipo de lógica mediante variables locales dispersas dentro de los `Composables`.

La UI deberá ser principalmente una representación del estado actual de la aplicación.

---

## 19. Resultado esperado

Al finalizar el proyecto, la aplicación debería permitir realizar el siguiente flujo:

```text
                 ┌──────────────┐
                 │    Login     │
                 └──────┬───────┘
                        │
                        ▼
                 ┌──────────────┐
                 │     Home     │
                 └──────┬───────┘
                        │
             ┌──────────┴──────────┐
             │                     │
             ▼                     ▼
       ┌───────────┐        ┌────────────┐
       │  Pokémon  │        │ Favoritos  │
       └─────┬─────┘        └──────┬─────┘
             │                     │
             └──────────┬──────────┘
                        │
                        ▼
                 ┌──────────────┐
                 │   Detalle    │
                 │   Pokémon    │
                 └──────┬───────┘
                        │
                        ▼
                 ┌──────────────┐
                 │   Favorito   │
                 │     Room     │
                 └──────────────┘
```

El usuario deberá poder:

1. Iniciar sesión.
2. Consultar el listado de Pokémon.
3. Acceder al detalle de un Pokémon.
4. Agregar un Pokémon a favoritos.
5. Quitar un Pokémon de favoritos.
6. Consultar la sección de favoritos.
7. Acceder al detalle desde favoritos.
8. Mantener los favoritos después de cerrar y volver a abrir la aplicación.

---

## 20. Consideraciones generales

La implementación visual y algunos detalles técnicos quedan a criterio del desarrollador. Sin embargo, se espera que el proyecto respete los siguientes principios:

- Mantener una clara separación de responsabilidades.
- Evitar lógica de negocio dentro de los `Composables`.
- Evitar que los `ViewModels` accedan directamente a Retrofit o Room.
- Utilizar `Use Cases` para representar la lógica de negocio.
- Utilizar `Repository` como abstracción de las fuentes de datos.
- Mantener separados los modelos de API, dominio y base de datos.
- Utilizar Hilt para la inyección de dependencias.
- Utilizar Room para la persistencia de favoritos.
- Utilizar Navigation Compose para la navegación.
- Manejar correctamente estados de carga y error.
- Mantener una comunicación basada en **State + Intent** entre UI y ViewModel.
- Priorizar código mantenible, legible y con responsabilidades bien definidas.

> **Importante:** los ejemplos incluidos en este documento son únicamente referencias para orientar la implementación. **No es necesario replicar exactamente la estructura, nombres de clases o código mostrado**, siempre que se cumplan los objetivos y principios indicados.
