package com.mkarajallo.pokedex.presentation.login

data class LoginState(
    val usuario: String = "",
    val contrasena: String = "",
    val error: String? = null
)

