package main

import analysis.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import model.Tweet
import reader.TweetReader
import llm.OpenAILLM
import llm.LLMComparison
import kotlin.random.Random

fun main(args: Array<String>) {

    val path = args.getOrNull(0)
        ?: "data/training.1600000.processed.noemoticon.csv"

    println("🚀 Loading tweets (streaming mode)...\n")

    val tweetsStream: Sequence<Tweet> = TweetReader.read(path)

    println("=== Sentiment Distribution ===")
    val sentimentDist = tweetsStream
        .groupBy { it.polarity }
        .mapValues { it.value.size }
    println("Negative (0): ${sentimentDist[0]}")
    println("Positive (4): ${sentimentDist[4]}")
    println("Total: ${(sentimentDist[0] ?: 0) + (sentimentDist[4] ?: 0)}\n")

    println("=== Top Active Users ===")
    TweetReader.read(path)
        .groupingBy { it.user }
        .eachCount()
        .entries
        .sortedByDescending { it.value }
        .take(20)
        .forEach { println("${it.key}: ${it.value} tweets") }
    println()

    println("=== Average Tweet Length by Sentiment ===")
    println(TextAnalysis.avgLengthBySentiment(TweetReader.read(path)))
    println()

    println("=== Top Words (Positive) ===")
    println(TextAnalysis.topWords(TweetReader.read(path), 4))
    println("\n=== Top Words (Negative) ===")
    println(TextAnalysis.topWords(TweetReader.read(path), 0))
    println()

    println("=== Distinctive Words (Positive vs Negative) ===")
    val d = TextAnalysis.distinctiveWords(TweetReader.read(path))
    println("Positive Unique: ${d["positive_unique"]}")
    println("Negative Unique: ${d["negative_unique"]}\n")

    // =========================================
    // LLM 평가
    // =========================================
    val apiKey = System.getenv("OPENAI_API_KEY")
        ?: error("OPENAI_API_KEY 환경 변수를 설정하세요")

    val sampleSize = 1000
    val sampleTweets = TweetReader.read(path)
        .shuffled(Random(42))
        .take(sampleSize)
        .toList()

    println("=== LLM Sentiment Classification ===")
    println("GPT 모델로 ${sampleSize}개 샘플 병렬 평가 중...\n")

    val llmPredicted: List<Int> = runBlocking {
        sampleTweets.map { tweet ->
            async {
                OpenAILLM.classifyAsync(tweet.text, apiKey)
            }
        }.awaitAll()
    }

    val accuracy = LLMComparison.evaluate(sampleTweets, llmPredicted)
    println("\nLLM Accuracy = $accuracy\n")

    println("=== FINAL SUMMARY ===")
    println("✔ Streaming 기반 전처리 및 분석 완료")
    println("✔ 텍스트 전처리 및 특징 분석 완료")
    println("✔ 사용자 활동 패턴 분석 완료")
    println("✔ GPT 감정 분류 정확도 = $accuracy")
}
