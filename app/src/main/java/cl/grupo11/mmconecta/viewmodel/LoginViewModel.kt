package cl.grupo11.mmconecta.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.grupo11.mmconecta.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

class LoginViewModel(private val repository: LoginRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun changeUsername(value: String) {
        _uiState.update { it.copy(username = value, errorMessage = null) }
    }

    fun changePassword(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun login() {
        val current = _uiState.value
        if (current.username.isBlank() || current.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingresa tu usuario y contraseña") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val success = repository.login(current.username, current.password)
            _uiState.update {
                if (success) it.copy(isLoading = false, isLoggedIn = true)
                else it.copy(isLoading = false, errorMessage = "Usuario o contraseña incorrectos")
            }
        }
    }
}
