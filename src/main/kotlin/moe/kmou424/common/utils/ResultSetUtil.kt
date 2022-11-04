package moe.kmou424.common.utils

import moe.kmou424.sqlite.enums.ColumnType
import java.sql.ResultSet

object ResultSetUtil {

    fun ResultSet.getAttr(type: ColumnType, key: String): Any? {
        return when (type) {
            ColumnType.INT -> this.getInt(key)
            ColumnType.INTEGER -> this.getInt(key)
            ColumnType.BOOLEAN -> this.getBoolean(key)
            ColumnType.DATETIME -> this.getDate(key)
            ColumnType.TEXT -> this.getString(key)
        }
    }

}