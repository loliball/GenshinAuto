package genshin.bean

import kotlinx.serialization.Serializable

@Serializable
data class ListUserInfo(
    val list: List<UserInfo>
)

@Serializable
data class UserInfo(
    val game_biz: String,
    val game_uid: String,
    val is_chosen: Boolean,
    val is_official: Boolean,
    val level: Int,
    val nickname: String,
    val region: String,
    val region_name: String
)