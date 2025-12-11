package analysis

import model.Tweet   // 이것만 필요함

object UserAnalysis {

    fun mostActiveUsers(tweets: List<Tweet>, top: Int = 20): List<Pair<String, Int>> {
        return tweets.groupingBy { it.user }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(top)
            .map { it.key to it.value }
    }
}