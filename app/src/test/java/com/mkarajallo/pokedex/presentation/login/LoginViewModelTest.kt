package com.mkarajallo.pokedex.presentation.login
import com.mkarajallo.pokedex.domain.RepositorioSesion
import org.junit.Assert.assertEquals
import org.junit.Test
private class RepositorioFalso(var respuesta: Boolean) : RepositorioSesion {
    override fun validar(usuario: String, contrasena: String) = respuesta
}
class LoginViewModelTest {
    @Test
    fun enviarActualizaElEstadoSegunLaValidacion() {
        val repositorio = RepositorioFalso(true)
        val vm = LoginViewModel(repositorio)
        vm.onIntent(LoginIntent.Enviar)
        assertEquals(LoginState(sesionIniciada = true), vm.state.value)
        repositorio.respuesta = false
        vm.onIntent(LoginIntent.Enviar)
        assertEquals(LoginState(error = "Usuario o contrasena incorrectos"), vm.state.value)
    }
}
