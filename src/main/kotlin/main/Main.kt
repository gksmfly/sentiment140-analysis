package main

import reader.TweetReader
import analysis.*
import llm.LLMComparison
import llm.OpenAILLM
import model.Tweet

fun main(args: Array<String>) {

    val path = args.getOrNull(0)
        ?: "data/training.1600000.processed.noemoticon.csv"

    println("🚀 Loading tweets (streaming mode)...\n")

    // CSV를 메모리에 올리지 않고 streaming 방식으로 처리
    val tweetsStream: Sequence<Tweet> = TweetReader.read(path)

    // ---------------------------------------------------------
    // 1) 감정 분포
    // ---------------------------------------------------------
    println("=== Sentiment Distribution ===")
    val sentimentDist = tweetsStream
        .groupBy { it.polarity }
        .mapValues { it.value.size }
    println("Negative (0): ${sentimentDist[0] ?: 0}")
    println("Positive (4): ${sentimentDist[4] ?: 0}")
    println("Total: ${(sentimentDist[0] ?: 0) + (sentimentDist[4] ?: 0)}\n")

    // ---------------------------------------------------------
    // Streaming은 한 번 소비하면 다시 못 쓰므로
    // 필요한 구간에서 다시 read() 호출해 새로운 스트림을 받아야 함
    // ---------------------------------------------------------

    // ---------------------------------------------------------
    // 2) 사용자 활동 패턴 분석
    // ---------------------------------------------------------
    println("=== Top Active Users ===")
    val activeUsers = TweetReader.read(path)
        .groupingBy { it.user }
        .eachCount()
        .entries
        .sortedByDescending { it.value }
        .take(20)
    activeUsers.forEach { println("${it.key}: ${it.value} tweets") }
    println()

    // ---------------------------------------------------------
    // 3) 텍스트 분석 (전처리 + 함수형 파이프라인)
    // ---------------------------------------------------------
    println("=== Average Tweet Length by Sentiment ===")
    val avgLength = TextAnalysis.avgLengthBySentiment(TweetReader.read(path))
    println(avgLength)
    println()

    println("=== Top Words (Positive) ===")
    val topPos = TextAnalysis.topWords(TweetReader.read(path), 4)
    println(topPos)
    println()

    println("=== Top Words (Negative) ===")
    val topNeg = TextAnalysis.topWords(TweetReader.read(path), 0)
    println(topNeg)
    println()

    println("=== Distinctive Words (Positive vs Negative) ===")
    val distinctive = TextAnalysis.distinctiveWords(TweetReader.read(path))
    println("Positive Unique: ${distinctive["positive_unique"]}")
    println("Negative Unique: ${distinctive["negative_unique"]}")
    println()

    // ---------------------------------------------------------
    // 4) LLM 감정 분류 비교 (교수님 추가 요구 기능)
    // ---------------------------------------------------------
    println("=== LLM Sentiment Classification ===")

    val apiKey = System.getenv("OPENAI_API_KEY")
    if (apiKey == null) {
        println("❌ ERROR: OPENAI_API_KEY 환경 변수를 설정하세요.")
        println("IntelliJ → Run/Debug Configurations → Environment Variables")
        return
    }

    val sampleSize = 1000
    val sampleTweets = TweetReader.read(path).shuffled(kotlin.random.Random(42)).take(sampleSize).toList()

    println("GPT 모델로 ${sampleSize}개 샘플 평가 중... (시간 걸릴 수 있음)")
    val llmPredicted = sampleTweets.map { OpenAILLM.classify(it.text, apiKey) }
    val accuracy = LLMComparison.evaluate(sampleTweets, llmPredicted)

    println("LLM Accuracy = $accuracy\n")

    // ---------------------------------------------------------
    // 5) 최종 요약
    // ---------------------------------------------------------
    println("=== FINAL SUMMARY ===")
    println("✔ Streaming 기반 전처리 및 분석 완료")
    println("✔ 텍스트 전처리 + 함수형 파이프라인 적용")
    println("✔ 감정 분포 / 사용자 패턴 / 텍스트 특징 분석 완료")
    println("✔ LLM 감정 분류 정확도 = $accuracy")
}
