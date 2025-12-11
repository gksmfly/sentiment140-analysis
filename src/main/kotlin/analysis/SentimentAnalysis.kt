package analysis

import model.Tweet

object SentimentAnalysis {

    // 감정 분포 계산
    fun sentimentDistribution(tweets: List<Tweet>): Map<Int, Int> {
        return tweets.groupingBy { it.polarity }.eachCount()
    }

    // 출력용
    fun printDistribution(dist: Map<Int, Int>) {
        println("Negative (0): ${dist[0] ?: 0}")
        println("Positive (4): ${dist[4] ?: 0}")
        println("Total: ${(dist[0] ?: 0) + (dist[4] ?: 0)}")
    }
}