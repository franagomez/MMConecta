package cl.grupo11.mmconecta.viewmodel

import cl.grupo11.mmconecta.model.LearningContent
import cl.grupo11.mmconecta.repository.ContentRepository
import cl.grupo11.mmconecta.repository.FavoritesRepository
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
 * Pruebas unitarias de HomeViewModel (EP4 - Guía 15).
 * Stack: JUnit5 + Kotest (aserciones) + MockK (mocks de ContentRepository
 * y FavoritesRepository) + kotlinx-coroutines-test (control del viewModelScope).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val sampleContents = listOf(
        LearningContent("1", "Introducción a las masas", "Panadería", "desc 1"),
        LearningContent("2", "Ideas para tu vitrina", "Pastelería", "desc 2"),
        LearningContent("3", "Primeros pasos en chocolatería", "Chocolatería", "desc 3")
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `al iniciar carga el catalogo y arma las categorias`() = runTest {
        val content = mockk<ContentRepository>()
        val favorites = mockk<FavoritesRepository>()
        coEvery { content.getContents() } returns sampleContents
        coEvery { favorites.load() } returns emptySet()

        val viewModel = HomeViewModel(favorites, content)
        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        state.contentsLoading shouldBe false
        state.categories shouldBe listOf("Todos", "Panadería", "Pastelería", "Chocolatería")
        state.contents.size shouldBe 3
        state.error shouldBe null
    }

    @Test
    fun `si falla la carga del catalogo muestra error y retryContents lo recupera`() = runTest {
        val content = mockk<ContentRepository>()
        val favorites = mockk<FavoritesRepository>()
        coEvery { content.getContents() } throws RuntimeException("sin conexión") andThen sampleContents
        coEvery { favorites.load() } returns emptySet()

        val viewModel = HomeViewModel(favorites, content)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.value.error shouldBe "No pudimos cargar el catálogo. Revisa tu conexión e inténtalo otra vez."
        viewModel.uiState.value.contents.size shouldBe 0

        viewModel.retryContents()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.value.contents.size shouldBe 3
        viewModel.uiState.value.error shouldBe null
    }

    @Test
    fun `selectCategory filtra el catalogo por esa categoria`() = runTest {
        val content = mockk<ContentRepository>()
        val favorites = mockk<FavoritesRepository>()
        coEvery { content.getContents() } returns sampleContents
        coEvery { favorites.load() } returns emptySet()

        val viewModel = HomeViewModel(favorites, content)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.selectCategory("Chocolatería")

        viewModel.uiState.value.contents.map { it.id } shouldBe listOf("3")
    }

    @Test
    fun `toggleFavorite agrega y luego quita un favorito, guardando cada cambio`() = runTest {
        val content = mockk<ContentRepository>()
        val favorites = mockk<FavoritesRepository>()
        coEvery { content.getContents() } returns sampleContents
        coEvery { favorites.load() } returns emptySet()
        coEvery { favorites.save(any()) } returns Unit

        val viewModel = HomeViewModel(favorites, content)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleFavorite("1")
        dispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.value.favoriteIds shouldBe setOf("1")

        viewModel.toggleFavorite("1")
        dispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.value.favoriteIds shouldBe emptySet()

        coVerify { favorites.save(setOf("1")) }
        coVerify { favorites.save(emptySet()) }
    }

    @Test
    fun `toggleFavorite con un id que no existe en el catalogo no hace nada`() = runTest {
        val content = mockk<ContentRepository>()
        val favorites = mockk<FavoritesRepository>()
        coEvery { content.getContents() } returns sampleContents
        coEvery { favorites.load() } returns emptySet()

        val viewModel = HomeViewModel(favorites, content)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleFavorite("no-existe")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.value.favoriteIds shouldBe emptySet()
        coVerify(exactly = 0) { favorites.save(any()) }
    }
}
