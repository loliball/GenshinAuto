package genshin.bean

import kotlinx.serialization.Serializable

@Serializable
data class CheckinDays(
    val first_bind: Boolean,
    val is_sign: Boolean,
    val is_sub: Boolean,
    val month_first: Boolean,
    val sign_cnt_missed: Int,
    val today: String,
    val total_sign_day: Int
)