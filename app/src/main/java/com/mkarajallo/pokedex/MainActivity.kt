package com.mkarajallo.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mkarajallo.pokedex.ui.theme.PokedexTheme


data class Pokemon(val name: String, val numero: Int, val tipo: String)

val pokebola = listOf(
    Pokemon("Pikachu", 25, "Eléctrico"),
    Pokemon("Gengar", 94, "Fantasma / Veneno"),
    Pokemon("Lucario", 448, "Lucha / Acero"),
    Pokemon("Rayquaza", 384, "Dragón / Volador"),
    Pokemon("Charmander", 4, "Fuego"),
    Pokemon("Snorlax", 143, "Normal"),
    Pokemon("Gyarados", 130, "Agua / Volador"),
    Pokemon("Scyther", 123, "Bicho / Volador"),
    Pokemon("Umbreon", 197, "Siniestro"),
    Pokemon("Metagross", 376, "Acero / Psíquico"),
    Pokemon("Lapras", 131, "Agua / Hielo"),
    Pokemon("Tyranitar", 248, "Roca / Siniestro"),
    Pokemon("Mewtwo", 150, "Psíquico"),
    Pokemon("Mimikyu", 778, "Fantasma / Hada"),
    Pokemon("Dragonite", 149, "Dragón / Volador"),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokedexTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListaPokemon(
                        pokemones = pokebola,
                        modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp),
                    )

                }
            }
        }
    }
}

@Composable
fun FichaPokemon(name:String, numero: Int, tipo:String, modifier: Modifier = Modifier,) {

    Card( modifier= modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(name, style = MaterialTheme.typography.titleLarge)
                Text("N.º $numero", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("Tipo: $tipo", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ListaPokemon(pokemones: List<Pokemon>, modifier: Modifier = Modifier,) {
    LazyColumn (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        ){
        items(pokemones){ pokemon ->
            FichaPokemon(
                name=pokemon.name,
                numero=pokemon.numero,
                tipo=pokemon.tipo)
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
