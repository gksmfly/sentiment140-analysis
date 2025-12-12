## Sentiment140 Twitter 감정 분석 프로젝트

이 프로젝트는 **Sentiment140 데이터셋(160만 개 트윗)**을 활용하여 감정(긍정/부정) 패턴을 분석하고, 텍스트 전처리 및 Kotlin 함수형 프로그래밍을 사용해 대규모 데이터를 효율적으로 처리하는 것을 목표로 합니다.

---

### 📁 프로젝트 디렉토리 구조

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
```

### 📦 데이터 파일 준비

Sentiment140 데이터는 라이선스 문제로 인해 Git에 포함되지 않으므로, Kaggle에서 직접 다운로드해야 합니다.

1. **Kaggle 링크:** [Sentiment140 dataset with 1.6 million tweets](https://www.kaggle.com/datasets/kazanova/sentiment140)
2. 다운로드 후 압축을 해제하고, 아래 위치에 파일을 배치하세요:

```text
sentiment140-analysis/
└── data/
    └── training.1600000.processed.noemoticon.csv
```

### 프로젝트 실행 방법

#### 1. JDK 설정
이 프로젝트는 **JDK 24**를 기반으로 작성되었습니다.
- **IntelliJ IDEA:** `File` -> `Project Structure` -> `Project SDK`를 **JDK 24**로 설정하세요.

#### 2. Gradle 빌드
터미널에서 아래 명령어를 입력하여 프로젝트를 빌드합니다.

```bash
./gradlew build
```

#### 3. 프로그램 실행
기본 경로(`data/training.1600000.processed.noemoticon.csv`)로 실행:

```bash
./gradlew run
```

### LLM(API) 감정 분석 기능 사용

OpenAILLM.kt를 통해 GPT 모델로 감정 분류 정확도(Accuracy)를 평가할 수 있습니다.
이를 위해 OpenAI API Key를 환경 변수로 등록해야 합니다.

#### macOS 전체 앱 환경 변수 등록
```
launchctl setenv OPENAI_API_KEY " "
```
" "에 키 값을 넣으면 됩니다.

### 주요 실행 결과 예시
아래는 실제 실행 결과 예시입니다.

#### 감정 분포
```
Negative (0): 456259
Positive (4): 379459
```

#### 활동량 Top 20 사용자
```
lost_dog: 274 tweets
tweetpet: 236 tweets
webwoke: 211 tweets
...
```

#### 긍정 단어 Top Words
```
love, good, thanks, lol, day, happy ...
```

#### 부정 단어 Top Words
```
not, have, don, miss, sad, want, need ...
```

#### LLM 정확도 비교
```
GPT 모델로 200개 샘플 평가 중...
LLM Accuracy = 0.77
```
