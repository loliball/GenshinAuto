package loli.ball.genshin.bean

import kotlinx.serialization.Serializable

@Serializable
data class ReturnData<T>(
    val `data`: T?,
    val message: String,
    val retcode: Int
)