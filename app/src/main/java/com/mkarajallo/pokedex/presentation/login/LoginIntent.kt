package com.mkarajallo.pokedex.presentation.login

sealed class LoginIntent {
    data class CambioUsuario(val valor: String) : LoginIntent()
    data class CambioContrasena(val valor: String) : LoginIntent()
    data object Enviar : LoginIntent()
}