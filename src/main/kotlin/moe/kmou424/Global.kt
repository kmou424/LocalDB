package moe.kmou424

import com.fasterxml.jackson.databind.ObjectMapper

object Global {
    const val SQLiteDriver = "org.sqlite.JDBC"

    val DefaultCharset = Charsets.UTF_8
    val GlobalObjectMapper = ObjectMapper()
    val GlobalPrettyObjectWriter = GlobalObjectMapper.writerWithDefaultPrettyPrinter()!!
}