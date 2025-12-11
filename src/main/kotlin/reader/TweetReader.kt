package reader

import com.opencsv.CSVReader
import model.Tweet
import java.io.FileReader

object TweetReader {

    fun read(path: String): Sequence<Tweet> = sequence {
        CSVReader(FileReader(path)).use { reader ->
            var row = reader.readNext()
            while (row != null) {
                yield(
                    Tweet(
                        polarity = row[0].toInt(),
                        id = row[1].toLong(),
                        date = row[2],
                        query = row[3],
                        user = row[4],
                        text = row[5]
                    )
                )
                row = reader.readNext()
            }
        }
    }
}
