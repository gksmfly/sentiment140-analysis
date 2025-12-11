package analysis

import model.Tweet

object TextAnalysis {

    // 기본 불용어 리스트
    private val stopwords = setOf(
        "the", "a", "an", "and", "or", "but", "is", "are", "am", "to", "in",
        "of", "for", "on", "with", "at", "by", "from", "it", "this", "that",
        "be", "as", "was", "were", "so", "if", "then", "too", "very", "can",
        "i", "you", "he", "she", "they", "we", "me", "my", "your"
    )

    // 텍스트 전처리 함수 (함수형 pipeline)
    private fun preprocess(text: String): List<String> =
        text.lowercase()
            .replace(Regex("http\\S+"), "")      // URL 제거
            .replace(Regex("@\\w+"), "")         // @유저 제거
            .replace(Regex("[^a-z\\s]"), " ")    // 특수문자 제거
            .split(Regex("\\s+"))                // 토큰 분리
            .map { it.trim() }
            .filter { it.isNotBlank() && it.length > 2 } // 최소 길이 3자
            .filter { it !in stopwords }         // 불용어 제거

    // 감정별 평균 트윗 길이
    fun avgLengthBySentiment(tweets: Sequence<Tweet>): Map<Int, Double> =
        tweets.groupBy { it.polarity }
            .mapValues { (_, group) -> group.map { it.text.length }.average() }

    // 감정별 Top 단어
    fun topWords(tweets: Sequence<Tweet>, sentiment: Int, top: Int = 20): List<Pair<String, Int>> =
        tweets
            .filter { it.polarity == sentiment }
            .flatMap { preprocess(it.text) }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(top)
            .map { it.key to it.value }

    // 감정별 단어 차이점: 긍정/부정 top 비교
    fun distinctiveWords(
        tweets: Sequence<Tweet>,
        top: Int = 20
    ): Map<String, List<Pair<String, Int>>> {

        val positive = topWords(tweets, 4, top * 2)
        val negative = topWords(tweets, 0, top * 2)

        val posOnly = positive.filter { (word, _) ->
            word !in negative.map { it.first }
        }.take(top)

        val negOnly = negative.filter { (word, _) ->
            word !in positive.map { it.first }
        }.take(top)

        return mapOf(
            "positive_unique" to posOnly,
            "negative_unique" to negOnly
        )
    }
}
