package loli.ball.genshin.bean

@kotlinx.serialization.Serializable
data class DailyNote(
    val current_resin: Int,
    val max_resin: Int,
    val resin_recovery_time: String,
    val current_home_coin: Int,
    val max_home_coin: Int,
    val home_coin_recovery_time: String
)