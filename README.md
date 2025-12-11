# Sentiment140 Twitter 감정 분석 프로젝트

이 프로젝트는 **Sentiment140 데이터셋(160만 개 트윗)**을 활용하여 감정(긍정/부정) 패턴을 분석하고, 텍스트 전처리 및 Kotlin 함수형 프로그래밍을 사용해 대규모 데이터를 효율적으로 처리하는 것을 목표로 합니다.

---

## 📁 프로젝트 디렉토리 구조

```text
sentiment140-analysis/
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
├── analysis.md
└── src/
    └── main/
        └── kotlin/
            ├── main/
            │   └── Main.kt
            ├── analysis/
            │   ├── SentimentAnalysis.kt
            │   ├── TextAnalysis.kt
            │   └── UserAnalysis.kt
            ├── llm/
            │   ├── LLMComparison.kt
            │   └── OpenAILLM.kt
            ├── model/
            │   └── Tweet.kt
            └── reader/
                └── TweetReader.kt
