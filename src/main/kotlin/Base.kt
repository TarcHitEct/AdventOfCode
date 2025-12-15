import java.io.*
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.net.ssl.HttpsURLConnection
import kotlin.reflect.KFunction
import kotlin.time.Duration

var startTime: Long? = null
var endTime: Long? = null

fun readInput(year: Int, day: Int): String {
    val conn = URL("https://adventofcode.com/$year/day/$day/input").openConnection() as HttpsURLConnection
    conn.setRequestProperty("cookie", "session=$sessionId");
    if (conn.responseCode != HttpsURLConnection.HTTP_OK) {
        throw Exception("Request Failed. HTTP Error Code: " + conn.responseCode);
    }
    val inputText = conn.inputStream.bufferedReader().use { it.readText().replace("\r", "") }
    startTime = System.currentTimeMillis()
    return inputText
}

fun readInput(main: KFunction<*>): String {
    val ref = main.javaClass.name
    val day = ref.replace(Regex(".*_(.*)Kt.*"), "$1").toInt()
    val year = main.javaClass.packageName.substring(3).toInt()
    return readInput(year, day)
}

fun solveA(main: KFunction<*>, answer: Any) {
    endTime = System.currentTimeMillis()
    val ref = main.javaClass.name
    val day = ref.replace(Regex(".*_(.*)Kt.*"), "$1").toInt()
    val year = main.javaClass.packageName.substring(3).toInt()
    solve(year, day, 1, answer.toString())
    startTime = System.currentTimeMillis()
}

fun solveB(main: KFunction<*>, answer: Any) {
    endTime = System.currentTimeMillis()
    val ref = main.javaClass.name
    val day = ref.replace(Regex(".*_(.*)Kt.*"), "$1").toInt()
    val year = main.javaClass.packageName.substring(3).toInt()
    solve(year, day, 2, answer.toString())
    startTime = System.currentTimeMillis()
}

fun solve(year: Int, day: Int, level: Int, answer: String) {
    var duration = "-"
    if (endTime != null && startTime != null)
        duration = (endTime!! - startTime!!).toString() + "ms"
    println("$year/$day/$level answer: $answer (took $duration)")
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://adventofcode.com/$year/day/$day/answer"))
        .POST(formData(mapOf("level" to level.toString(), "answer" to answer)))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("cookie", "session=$sessionId")
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
    if (response.lowercase().contains("one gold star")) {
        println("SOLVED!")
    } else if (response.lowercase().contains("too low")) {
        println("TOO LOW!")
    } else if (response.lowercase().contains("too high")) {
        println("TOO HIGH!")
    } else if (response.lowercase().contains("not the right answer")) {
        println("INCORRECT!")
    } else if (response.lowercase().contains("already complete")) {
        val correctAnswer = getSolvedAnswer(year, day, level)
        if (answer != correctAnswer) {
            println("ALREADY SOLVED! Correct answer: $correctAnswer")
        } else {
            println("ALREADY SOLVED!")
        }
    } else if (response.lowercase().contains("left to wait")) {
        val waitTime = Regex("You have (.*) left to wait").find(response)!!.groupValues[1]
        println("WAIT ${waitTime}...")
        Thread.sleep(Duration.parse(waitTime).inWholeMilliseconds + 500)
        solve(year, day, level, answer)
    } else {
        println("Unknown response: $response")
    }
}

fun getSolvedAnswer(year: Int, day: Int, level: Int): String? {
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://adventofcode.com/$year/day/$day"))
        .GET()
        .header("cookie", "session=$sessionId")
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
    val solutions = Regex("Your puzzle answer was[^<]*<code>([^<]*)</code>").findAll(response)
        .map { it.groupValues[1] }.toList()
    return solutions.getOrNull(level - 1)
}

fun formData(data: Map<String, String>): HttpRequest.BodyPublisher? {
    val res = data.map { (k, v) -> "${(k.utf8())}=${v.utf8()}" }
        .joinToString("&")
    return HttpRequest.BodyPublishers.ofString(res)
}

fun String.utf8(): String = URLEncoder.encode(this, "UTF-8")

fun <T : Serializable> deepCopy(obj: T): T {
    val baos = ByteArrayOutputStream()
    val oos = ObjectOutputStream(baos)
    oos.writeObject(obj)
    oos.close()
    val bais = ByteArrayInputStream(baos.toByteArray())
    val ois = ObjectInputStream(bais)
    @Suppress("unchecked_cast")
    return ois.readObject() as T
}

fun lcm(n1: Int, n2: Int): Int {
    var lcm: Int = if (n1 > n2) n1 else n2
    while (true) {
        if (lcm % n1 == 0 && lcm % n2 == 0) {
            break
        }
        ++lcm
    }
    return lcm
}