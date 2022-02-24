package loli.ball.genshin

import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.random.Random

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
        period = Duration.ofDays(1).toMillis()
    ) {
        val waitTime = Random.nextLong(Duration.ofHours(1).toMillis())
        println("随机等待${waitTime/1000}秒")
        Thread.sleep(waitTime)
        val checkinAll = AutoCheckin.checkinAll("cookies.txt")
        println("现在是 " + formatter.format(Date()))
        println(checkinAll.joinToString("\n"))
    }
}
