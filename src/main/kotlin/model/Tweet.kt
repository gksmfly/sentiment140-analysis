package model

data class Tweet(
    val polarity: Int,
    val id: Long,
    val date: String,
    val query: String,
    val user: String,
    val text: String
)