package cl.grupo11.mmconecta.model


data class LearningContent(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val steps: List<String> = emptyList()
)
