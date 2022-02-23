import genshin.AutoCheckin
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
        println("random wait ${waitTime/1000}s")
        Thread.sleep(waitTime)
        val checkinAll = AutoCheckin.checkinAll("cookies.txt")
        println("now: " + formatter.format(Date()))
        println(checkinAll.joinToString("\n"))
    }
}
