package llm

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import com.google.gson.JsonObject

object OpenAILLM {

    private val client = OkHttpClient()
    private val gson = Gson()

    fun classify(text: String, apiKey: String): Int {

        val json = """
            {
              "model": "gpt-4o-mini",
              "messages": [
                {"role": "system", "content": "Return ONLY 0 or 4. 0=negative, 4=positive."},
                {"role": "user", "content": "$text"}
              ]
            }
        """.trimIndent()

        val body = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: return 0

            val root = gson.fromJson(responseBody, JsonObject::class.java)
            val answer = root["choices"]
                .asJsonArray[0].asJsonObject["message"]
                .asJsonObject["content"]
                .asString.trim()

            return if (answer == "4") 4 else 0
        }
    }
}
