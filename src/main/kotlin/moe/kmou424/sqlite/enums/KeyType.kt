package moe.kmou424.sqlite.enums

enum class KeyType(
    val sql: String,
    val autoInc: Boolean = false
) {
    INT("INT"),
    INTEGER("INTEGER", autoInc = true),
    TEXT("TEXT"),
    BOOLEAN("BOOLEAN"),
    DATETIME("TEXT")
}