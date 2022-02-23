package genshin.bean

import kotlinx.serialization.Serializable

@Serializable
data class ListAwards(
    val month: Int,
    val awards: List<Awards>,
    val resign: Boolean
)

@Serializable
data class Awards(
    val cnt: Int,
    val icon: String,
    val name: String
)