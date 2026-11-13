package cl.grupo11.mmconecta.repository

import kotlinx.coroutines.delay

interface LoginRepository {
    suspend fun login(username: String, password: String): Boolean
}

/**
 * Validación local de login (sin backend todavía). Cuando el microservicio
 * de usuarios esté listo, se reemplaza esta clase por una que consuma la API
 * vía Retrofit, sin tocar el ViewModel ni la pantalla.
 */
class LocalLoginRepository : LoginRepository {
    override suspend fun login(username: String, password: String): Boolean {
        delay(600) // simula latencia de red
        return username.isNotBlank() && password.length >= 4
    }
}
