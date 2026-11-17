package cl.grupo11.mmconecta.network

import cl.grupo11.mmconecta.model.LearningContent
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class LoginRequestBody(val username: String, val password: String)
data class LoginResponseBody(val success: Boolean, val message: String)

/**
 * Contrato de la API REST del backend MMConecta (Spring Boot).
 * Los campos de LearningContent (id, title, description, category, steps)
 * coinciden exactamente con el JSON que entrega el backend, asi que Gson
 * puede mapearlo directo sin necesitar un DTO aparte.
 */
interface ApiService {

    @GET("api/catalogo")
    suspend fun getCatalogo(@Query("categoria") categoria: String? = null): List<LearningContent>

    @GET("api/catalogo/{id}")
    suspend fun getContenido(@Path("id") id: String): LearningContent

    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequestBody): LoginResponseBody
}
