package llm

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.math.min

object OpenAILLM {

    private val client = OkHttpClient()
    private val gson = Gson()

    // 동시성 제한 (한 번에 3개만 API 호출)
    private val semaphore = kotlinx.coroutines.sync.Semaphore(3)

    // 최대 재시도 횟수
    private const val MAX_RETRIES = 3

    suspend fun classifyAsync(text: String, apiKey: String): Int =
        withContext(Dispatchers.IO) {

            semaphore.acquire()  // 동시성 제한
            try {
                var attempt = 0

                while (attempt < MAX_RETRIES) {
                    try {
                        val json = """
                        {
                          "model": "gpt-4o-mini",
                          "temperature": 0,
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

                        val response = client.newCall(request).execute()

                        if (!response.isSuccessful) {
                            val code = response.code

                            // 429 (rate limit) 은 재시도
                            if (code == 429) {
                                val waitTime = 300L * (attempt + 1)
                                println("⚠️ API 429 → ${waitTime}ms 후 재시도 (attempt=$attempt)")
                                delay(waitTime)
                                attempt++
                                continue
                            }

                            // 기타 오류는 실패 처리
                            println("⚠️ API 오류 발생: HTTP $code")
                            return@withContext 0
                        }

                        val responseBody = response.body?.string()
                        if (responseBody == null) return@withContext 0

                        val root = gson.fromJson(responseBody, JsonObject::class.java)
                        val answer = root["choices"]
                            .asJsonArray[0].asJsonObject["message"]
                            .asJsonObject["content"]
                            .asString.trim()

                        return@withContext if (answer == "4") 4 else 0

                    } catch (e: Exception) {
                        println("⚠️ 예외 발생 → 재시도 attempt=$attempt (${e.message})")
                        delay(300L * (attempt + 1))
                        attempt++
                    }
                }

                // 모든 재시도 실패 → fallback
                println("❗ 최대 재시도 실패 → fallback=0")
                0
            } finally {
                semaphore.release()
            }
        }
}
