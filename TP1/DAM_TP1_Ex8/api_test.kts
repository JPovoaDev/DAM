import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner

fun main() {
    testApi("https://www.thesportsdb.com/api/v1/json/3/searchplayers.php?p=Ronaldo")
    testApi("https://www.thesportsdb.com/api/v1/json/3/searchteams.php?t=Real%20Madrid")
    // Let's test what happens when we send an empty string (to simulate initial default state)
    testApi("https://www.thesportsdb.com/api/v1/json/3/searchplayers.php?p=")
    testApi("https://www.thesportsdb.com/api/v1/json/3/searchteams.php?t=")
}

fun testApi(urlString: String) {
    println("Testing: $urlString")
    try {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val scanner = Scanner(connection.inputStream)
            scanner.useDelimiter("\\A")
            val hasInput = scanner.hasNext()
            if (hasInput) {
                val data = scanner.next()
                println("Success. Response snippet: ${data.take(150)}...")
            }
        } else {
            println("Failed with response code: $responseCode")
        }
    } catch (e: Exception) {
        println("Exception: ${e.message}")
    }
    println("-----------------------")
}
