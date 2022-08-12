package loli.ball.genshin

import loli.ball.genshin.bean.Awards
import loli.ball.genshin.bean.ListAwards
import okhttp3.Cookie
import java.io.File
import java.io.FileInputStream
import java.time.LocalDate

@Suppress("unused")
object AutoCheckin {

    const val SUCCESS = "签到成功"
    const val FAILED = "签到失败"

    private var innerAwards: ListAwards? = null
    private val awards: List<Awards>
        get() {
            if (innerAwards == null || LocalDate.now().dayOfMonth != innerAwards!!.month) {
                innerAwards = RequestEncrypt.loadAwards()
            }
            return innerAwards!!.awards
        }

    fun checkinAll(cookiesFile: String): List<String> {
        val results = mutableListOf<String>()
        FileInputStream(cookiesFile).reader().forEachLine { cookie ->
            if (cookie.isNotBlank()) {
                results += checkin(cookie)
            }
        }
        return results
    }

    fun checkin(cookie: String, ignoreCheck: Boolean = false): String {
        RequestEncrypt.loadUser(cookie)?.let { user ->
            val ua = "${user.nickname}(${user.game_uid})"
//            println(user)
            val checkinDays = RequestEncrypt.getCheckinDays(cookie, user)
//            println(checkinDays)
            checkinDays?.let {
                return if (ignoreCheck || !it.is_sign) {
                    if (RequestEncrypt.checkin(cookie, user) != null) {
                        val index = checkinDays.total_sign_day
                        if (awards.size > index) {
                            "$SUCCESS $ua ${awards[index].name}*${awards[index].cnt}"
                        } else {
                            "$SUCCESS $ua 奖励信息获取失败"
                        }
                    } else {
                        "$FAILED $ua"
                    }
                } else {
                    "$FAILED $ua 已经签到过了"
                }
            }
            return "$FAILED $ua 无法获取签到信息"
        }
        return "$FAILED 无法加载用户"
    }

    fun distinctCookies(inFile: String, outFile: String) {
        File(outFile).outputStream().use { out ->
            val strings = mutableListOf<String>()
            File(inFile).inputStream().use {
                it.reader().forEachLine {
                    strings += distinctCookie(it)
                }
            }
            out.write(strings.joinToString("\n").toByteArray())
        }
    }

    fun distinctCookie(cookie: String): String {
        val filterCookies = listOf("account_id", "cookie_token")
        val cookies = mutableListOf<Cookie>()
        cookie.split(";").forEach {
            val kv = it.split("=")
            if (kv.size == 2) {
                val key = kv[0].trim()
                val value = kv[1].trim()
                if (filterCookies.contains(key)) {
                    cookies += Cookie.Builder()
                        .domain("api-takumi.mihoyo.com")
                        .name(key)
                        .value(value)
                        .build()
                }
            }
        }
        cookies.sortBy {
            filterCookies.indexOf(it.name)
        }
        return cookies.joinToString(";") {
            "${it.name}=${it.value}"
        }
    }

}