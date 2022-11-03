package moe.kmou424.sqlite.enums

enum class KeyExtra(val sql: String) {
    NOTNULL("NOT NULL"),
    AUTOINCREMENT("AUTOINCREMENT"),
    PRIMARYKEY("PRIMARY KEY")
}