package com.mkarajallo.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mkarajallo.pokedex.ui.theme.PokedexTheme


data class Pokemon(val name: String, val numero: Int, val tipo: String)

val pokebola = listOf(
    Pokemon("Pikachu", 25 , "Eléctrico"),
    Pokemon("Gengar", 36 , "Fantasma / Veneno"),
    Pokemon("Lucario", 55 , "Lucha / Acero"),
    Pokemon("Rayquaza", 70 , "Dragón / Volador"),
    Pokemon("Charmander", 8 , "Fuego"),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokedexTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListaPokemon (
                        pokemones = pokebola,
                        modifier = Modifier.padding(innerPadding))
                }

            }
        }
    }
}

@Composable
fun FichaPokemon(name:String, numero: Int, tipo:String, modifier: Modifier = Modifier,) {

    Column (modifier = modifier,verticalArrangement = Arrangement.spacedBy(4.dp),) {
        Text(name)
        Text("N.º $numero")
        Text("Tipo: $tipo")
    }
}

@Composable
fun ListaPokemon(pokemones: List<Pokemon>, modifier: Modifier = Modifier,) {

    Column (modifier = modifier,verticalArrangement = Arrangement.spacedBy(16.dp),) {
        pokemones.forEach { pokemon ->
            FichaPokemon(name=pokemon.name, numero=pokemon.numero, tipo=pokemon.tipo)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FichaPokemonPreview() {
    PokedexTheme {
        FichaPokemon(name = "Pikachu", numero = 25, tipo = "Eléctrico")
    }
}
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
