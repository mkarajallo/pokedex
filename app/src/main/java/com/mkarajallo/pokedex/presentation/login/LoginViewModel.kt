package com.mkarajallo.pokedex.presentation.login

import androidx.lifecycle.ViewModel
import com.mkarajallo.pokedex.data.RepositorioSesionLocal
import com.mkarajallo.pokedex.domain.RepositorioSesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel(
    private val repositorio: RepositorioSesion = RepositorioSesionLocal()
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state
    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.CambioUsuario -> _state.value = _state.value.copy(usuario = intent.valor)
            is LoginIntent.CambioContrasena -> _state.value =
                _state.value.copy(contrasena = intent.valor)

            LoginIntent.Enviar -> {
                val actual = _state.value
                if (repositorio.validar(actual.usuario, actual.contrasena)) {
                    _state.value = actual.copy(sesionIniciada = true, error = null)
                } else {
                    _state.value = actual.copy(
                        error = "Usuario o contrasena incorrectos",
                        sesionIniciada = false
                    )
                }
            }
        }
    }
}

