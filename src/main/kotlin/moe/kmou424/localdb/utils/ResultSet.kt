package moe.kmou424.localdb.utils

import moe.kmou424.sqlite.enums.KeyType
import java.sql.ResultSet

fun ResultSet.getAttr(type: KeyType, key: String): Any? {
    return when (type) {
        KeyType.INT -> this.getInt(key)
        KeyType.INTEGER -> this.getInt(key)
        KeyType.BOOLEAN -> this.getBoolean(key)
        KeyType.DATETIME -> this.getDate(key)
        KeyType.TEXT -> this.getString(key)
    }
}