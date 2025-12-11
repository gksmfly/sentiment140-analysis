package llm

import model.Tweet

object LLMComparison {

    fun evaluate(original: List<Tweet>, predicted: List<Int>): Double {
        val correct = original.zip(predicted).count { (tweet, pred) ->
            tweet.polarity == pred
        }
        return correct.toDouble() / original.size
    }
}