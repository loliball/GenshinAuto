package loli.ball.genshin

import loli.ball.genshin.bean.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

object RequestEncrypt {

    private const val USER_INFO_URL =
        "https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie?game_biz=hk4e_cn"
    private const val SIGN_INFO_URL =
        "https://api-takumi.mihoyo.com/event/bbs_sign_reward/home?act_id=e202009291139501"
    private const val SIGN_DAYS_URL =
        "https://api-takumi.mihoyo.com/event/bbs_sign_reward/info"
    private const val SIGN_URL =
        "https://api-takumi.mihoyo.com/event/bbs_sign_reward/sign"
    private const val DAILY_NOTE_URL =
        "https://api-takumi-record.mihoyo.com/game_record/app/genshin/api/dailyNote"
    private const val UA =
        "Mozilla/5.0 (Linux; Android 10; Redmi K30 Pro Build/QKQ1.200419.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/81.0.4044.138 Mobile Safari/537.36 miHoYoBBS/2.34.1"

    private val client by lazy {
        OkHttpClient()
    }

    private val json by lazy {
        Json {
            ignoreUnknownKeys = true
        }
    }

    fun dailyNote(cookie: String, userInfo: UserInfo): DailyNote? {
        val httpUrl = DAILY_NOTE_URL.toHttpUrl().newBuilder()
            .addQueryParameter("role_id", userInfo.game_uid)
            .addQueryParameter("server", userInfo.region)
            .build()
        val request = Request.Builder()
            .url(httpUrl)
            .headers(getHeadersDS5(cookie, httpUrl.query!!))
            .build()
        return requestOrError(request)
    }

    fun checkin(cookie: String, userInfo: UserInfo): CheckinOK? {
        val jsonObject = buildJsonObject {
            put("act_id", "e202009291139501")
            put("region", userInfo.region)
            put("uid", userInfo.game_uid)
        }.toString()
        val request = Request.Builder()
            .url(SIGN_URL)
            .post(jsonObject.toRequestBody("application/json;charset=utf-8".toMediaTypeOrNull()))
            .headers(getHeadersDS221(cookie))
            .build()
        return requestOrError(request)
    }

    fun getCheckinDays(cookie: String, userInfo: UserInfo): CheckinDays? {
        val httpUrl = SIGN_DAYS_URL.toHttpUrl().newBuilder()
            .addQueryParameter("region", userInfo.region)
            .addQueryParameter("act_id", "e202009291139501")
            .addQueryParameter("uid", userInfo.game_uid)
            .build()
        val request = Request.Builder()
            .url(httpUrl)
            .headers(getHeaders(cookie))
            .build()
        return requestOrError(request)
    }

    fun loadAwards(): ListAwards? {
        val request = Request.Builder()
            .url(SIGN_INFO_URL)
            .build()
        return requestOrError(request)
    }

    fun loadUser(cookie: String): UserInfo? {
        val request = Request.Builder()
            .url(USER_INFO_URL)
            .headers(getHeaders(cookie))
            .build()
        return requestOrError<ListUserInfo>(request)?.list?.single()
    }


    private inline fun <reified R> requestOrError(request: Request): R? {
        return runCatching {
            client.newCall(request).execute().body?.string()?.let { json1 ->
                val returnData = json.decodeFromString<ReturnData<R>>(json1)
                if (returnData.retcode != 0) {
                    error("msg: ${returnData.message} raw: $json1")
                } else {
                    returnData.data
                }
            }
        }.onFailure {
            System.err.println("RequestEncrypt::requestOrError")
            System.err.println(it.message)
        }.getOrNull()
    }

    //包装请求头
    private fun getHeaders(cookie: String): Headers {
        return Headers.Builder()
            .add("Host", "api-takumi.mihoyo.com")
            .add("Accept", "application/json, text/plain, */*")
            .add("Origin", "https://webstatic.mihoyo.com")
            .add(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 10; Redmi K30 Pro Build/QKQ1.200419.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/81.0.4044.138 Mobile Safari/537.36 miHoYoBBS/2.7.0"
            )
            .add("x-rpc-device_id", "e7425860-4908-3e49-84e3-464bc8ce92a9")
            .add("X-Requested-With", "com.mihoyo.hyperion")
            .add("Sec-Fetch-Site", "same-site")
            .add("Sec-Fetch-Mode", "cors")
            .add("Sec-Fetch-Dest", "empty")
            .add(
                "Referer",
                "https://webstatic.mihoyo.com/bbs/event/signin-ys/index.html?bbs_auth_required=true&act_id=e202009291139501&utm_source=bbs&utm_medium=mys&utm_campaign=icon"
            )
            .add("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
            .add("Cookie", cookie)
            .build()
    }

    private fun getHeadersDS221(cookie: String): Headers {
        return Headers.Builder()
            .add("Host", "api-takumi.mihoyo.com")
            .add("Connection", "keep-alive")
            .add("DS", getDS221())
            .add("Origin", "https://webstatic.mihoyo.com")
            .add("x-rpc-app_version", "2.34.1")
            .add("User-Agent", UA)
            .add("x-rpc-device_id", "e7425860-4908-3e49-84e3-464bc8ce92a9")
            .add("Accept", "application/json, text/plain, */*")
            .add("Content-Type", "application/json;charset=UTF-8")
            .add("x-rpc-client_type", "2")
            .add("X-Requested-With", "com.mihoyo.hyperion")
            .add("Sec-Fetch-Site", "same-site")
            .add("Sec-Fetch-Mode", "cors")
            .add("Sec-Fetch-Dest", "empty")
            .add("Referer", "https://webstatic.mihoyo.com")
            .add("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
            .add("Cookie", cookie)
            .build()
    }

    private fun getHeadersDS5(cookie: String, query: String): Headers {
        return Headers.Builder()
            .add("Host", "api-takumi-record.mihoyo.com")
            .add("DS", getDS5(q = query))
            .add("Origin", "https://webstatic.mihoyo.com")
            .add("x-rpc-app_version", "2.34.1")
            .add("User-Agent", UA)
            .add("x-rpc-device_id", "e7425860-4908-3e49-84e3-464bc8ce92a9")
            .add("Accept", "application/json, text/plain, */*")
            .add("Content-Type", "application/json;charset=UTF-8")
            .add("x-rpc-client_type", "5")
            .add("X-Requested-With", "com.mihoyo.hyperion")
            .add("Sec-Fetch-Site", "same-site")
            .add("Sec-Fetch-Mode", "cors")
            .add("Sec-Fetch-Dest", "empty")
            .add("Referer", "https://webstatic.mihoyo.com")
            .add("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
            .add("Cookie", cookie)
            .build()
    }

    //生成DS
    private fun getDS221(): String {
        val random = Random()
        val nextInt = random.nextInt(15)
        //2.2.1 cx2y9z9a29tfqvr1qsq6c7yz99b5jsqt
        //2.3.0 h8w582wxwgqvahcdkpvdhbh2w9casgfl
        //2.7.0 fd3ykrh7o1j54g581upo1tvpam0dsgtf
        //2.8.0 dmq2p7ka6nsu0d3ev6nex4k1ndzrnfiy
        //2.34.1 z8DRIUjNDT7IT5IZXvrUAxyupA1peND9
        val a = "z8DRIUjNDT7IT5IZXvrUAxyupA1peND9"
        val b = System.currentTimeMillis().toString().substring(0, 10)
        val c = UUID.randomUUID().toString().replace("-", "").substring(nextInt, nextInt + 6)
        val d = md5Hex("salt=$a&t=$b&r=$c")
        return "$b,$c,$d,"
    }

    //生成DS
    private fun getDS5(q: String, b: String = ""): String {
        val random = Random()
        val nextInt = random.nextInt(15)
        val a = "xV8v4Qu54lUKrEYFZkJhB8cuOh9Asafs"
        val t = System.currentTimeMillis().toString().substring(0, 10)
        val r = UUID.randomUUID().toString().replace("-", "").substring(nextInt, nextInt + 6)
        val d = md5Hex("salt=$a&t=$t&r=$r&b=$b&q=$q")
        return "$t,$r,$d"
    }

    private fun md5Hex(str: String): String {
        val md = MessageDigest.getInstance("MD5")
        md.update(str.toByteArray())
        return BigInteger(1, md.digest()).toString(16)
    }

}