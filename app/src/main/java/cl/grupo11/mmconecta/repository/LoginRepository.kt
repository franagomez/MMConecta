package cl.grupo11.mmconecta.repository

import cl.grupo11.mmconecta.network.ApiClient
import cl.grupo11.mmconecta.network.ApiService
import cl.grupo11.mmconecta.network.LoginRequestBody
import kotlinx.coroutines.delay

interface LoginRepository {
    suspend fun login(username: String, password: String): Boolean
}

/**
 * Validación local de login (sin backend). Se deja como respaldo por si el
 * backend no está disponible durante la defensa.
 */
class LocalLoginRepository : LoginRepository {
    override suspend fun login(username: String, password: String): Boolean {
        delay(600) // simula latencia de red
        return username.isNotBlank() && password.length >= 4
    }
}

/**
 * Implementacion real para el EP4: valida las credenciales contra el
 * backend Spring Boot vía Retrofit. Si el backend no responde (por ejemplo
 * no esta corriendo), se trata como login fallido en vez de crashear la app.
 */
class RetrofitLoginRepository(
    private val api: ApiService = ApiClient.api
) : LoginRepository {
    override suspend fun login(username: String, password: String): Boolean {
        return try {
            api.login(LoginRequestBody(username, password)).success
        } catch (error: Exception) {
            false
        }
    }
}
