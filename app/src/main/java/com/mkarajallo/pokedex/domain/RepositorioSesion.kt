package com.mkarajallo.pokedex.domain

interface RepositorioSesion {
    fun validar(usuario: String, contrasena: String): Boolean
}