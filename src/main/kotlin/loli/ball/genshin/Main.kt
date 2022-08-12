package loli.ball.genshin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.pow
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun main(vararg args: String) {
    val start = Date.from(
        LocalDateTime.now()
            .truncatedTo(ChronoUnit.DAYS)
            .plusHours(3)
            .atZone(ZoneId.systemDefault())
            .toInstant()
    )
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    fixedRateTimer(
        name = AutoCheckin::class.simpleName,
        startAt = start,
        period = 1.days.inWholeMilliseconds
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val waitTime = (0..1.hours.inWholeMilliseconds).random()
            println("随机等待${waitTime / 1000}秒")
            delay(waitTime)
            println("现在是 " + formatter.format(Date()))
            val cookies = File("cookies.txt").readText()
            cookies.split("\n").forEach { cookie ->
                if (cookie.isNotBlank()) {
                    val result = AutoCheckin.checkin(cookie).log()
                    if (result.startsWith(AutoCheckin.FAILED)) {
                        launch {
                            tryExponentialBackoff {
                                val success = AutoCheckin.checkin(cookie).log()
                                success.startsWith(AutoCheckin.SUCCESS)
                            }
                        }
                    }
                    delay((0..30).random().seconds)
                }
            }
        }
    }
}

suspend fun tryExponentialBackoff(maxTime: Int = 5, callback: () -> Boolean) {
    var tryTimes = 0
    while (tryTimes++ < maxTime) {
        delay((2 pow tryTimes).minutes)
        delay((0..60).random().seconds)
        if (callback()) break
    }
}

private infix fun Int.pow(i: Int): Int {
    return this.toDouble().pow(i.toDouble()).toInt()
}

private fun <T> T.log() = apply { println(this) }