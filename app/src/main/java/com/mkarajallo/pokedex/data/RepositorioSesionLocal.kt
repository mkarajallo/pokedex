package com.mkarajallo.pokedex.data

import com.mkarajallo.pokedex.domain.RepositorioSesion

class RepositorioSesionLocal : RepositorioSesion {
    override fun validar(usuario: String, contrasena: String): Boolean {
        return usuario == "admin" && contrasena == "123456"
    }

}