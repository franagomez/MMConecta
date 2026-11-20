package cl.grupo11.mmconecta.viewmodel

import cl.grupo11.mmconecta.repository.LoginRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Pruebas unitarias de LoginViewModel (EP4 - Guía 15).
 * Stack: JUnit5 + Kotest (aserciones) + MockK (mock de LoginRepository)
 * + kotlinx-coroutines-test (control del viewModelScope).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login exitoso deja isLoggedIn en true`() = runTest {
        val repository = mockk<LoginRepository>()
        coEvery { repository.login("francisca", "1234") } returns true

        val viewModel = LoginViewModel(repository)
        viewModel.changeUsername("francisca")
        viewModel.changePassword("1234")
        viewModel.login()
        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        state.isLoggedIn shouldBe true
        state.isLoading shouldBe false
        state.errorMessage shouldBe null
        coVerify(exactly = 1) { repository.login("francisca", "1234") }
    }

    @Test
    fun `credenciales invalidas muestran mensaje de error y no deja loggeado`() = runTest {
        val repository = mockk<LoginRepository>()
        coEvery { repository.login(any(), any()) } returns false

        val viewModel = LoginViewModel(repository)
        viewModel.changeUsername("francisca")
        viewModel.changePassword("mala")
        viewModel.login()
        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        state.isLoggedIn shouldBe false
        state.errorMessage shouldBe "Usuario o contraseña incorrectos"
    }

    @Test
    fun `campos en blanco muestran error de validacion sin llamar al repositorio`() = runTest {
        val repository = mockk<LoginRepository>()

        val viewModel = LoginViewModel(repository)
        viewModel.changeUsername("")
        viewModel.changePassword("")
        viewModel.login()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.value.errorMessage shouldBe "Ingresa tu usuario y contraseña"
        coVerify(exactly = 0) { repository.login(any(), any()) }
    }

    @Test
    fun `escribir en los campos limpia el mensaje de error`() = runTest {
        val repository = mockk<LoginRepository>()
        coEvery { repository.login(any(), any()) } returns false

        val viewModel = LoginViewModel(repository)
        viewModel.login()
        dispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.value.errorMessage shouldBe "Ingresa tu usuario y contraseña"

        viewModel.changeUsername("francisca")

        viewModel.uiState.value.errorMessage shouldBe null
    }
}
