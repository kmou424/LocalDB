package moe.kmou424.sqlite.utils

import moe.kmou424.sqlite.enums.KeyExtra
import moe.kmou424.sqlite.enums.KeyType

object SQLFormatter {
    fun formatSQLCreateTable(
        table: String,
        columns_with_types: List<Pair<String, KeyType>>,
        columns_extra: List<List<KeyExtra>>
    ): String {
        val cols = emptyArray<String>().toMutableList()
        for (idx in columns_with_types.indices) {
            val col = emptyArray<String>().toMutableList().also {
                it.add(columns_with_types[idx].first)
                it.add(columns_with_types[idx].second.sql)
                if (columns_extra[idx].contains(KeyExtra.PRIMARYKEY)) {
                    it.add(KeyExtra.PRIMARYKEY.sql)
                    if (columns_extra[idx].contains(KeyExtra.AUTOINCREMENT) && columns_with_types[idx].second.autoInc)
                        it.add(KeyExtra.AUTOINCREMENT.sql)
                }
                if (columns_extra[idx].contains(KeyExtra.NOTNULL))
                    it.add(KeyExtra.NOTNULL.sql)
                else
                    it.add("NULL")
            }
            cols.add(col.joinToString(" "))
        }
        return "CREATE TABLE IF NOT EXISTS $table(${cols.joinToString(", ")});"
    }

    fun formatSQLDelete(table: String): String {
        return "DELETE FROM $table"
    }

    fun formatSQLInsert(table: String, columns_with_values: List<Pair<String, Any?>>): String {
        val columns = emptyList<String>().toMutableList()
        val values = emptyList<String>().toMutableList()

        for (item in columns_with_values) {
            columns.add(item.first)
            values.add(formatTypedString(item.second))
        }

        return "INSERT INTO $table (${columns.joinToString(", ")}) VALUES (${values.joinToString(", ")});"
    }

    fun formatSQLQuery(table: String, columns_with_types: List<Pair<String, KeyType>>): String {
        return "SELECT %s FROM %s".format(
            emptyList<String>().toMutableList().also { for (item in columns_with_types) it.add(item.first) }.joinToString(", "),
            table
        )
    }

    fun formatSQLUpdateTable(table: String, keys_with_values: List<Pair<String, Any?>>): String {
        val updateList = emptyList<String>().toMutableList().also {
            for (i in keys_with_values) {
                it.add("%s = %s".format(
                    i.first,
                    formatTypedString(i.second)
                ))
            }
        }
        return "UPDATE $table SET ${updateList.joinToString(", ")}"
    }

    fun String.formatSQLCondition(condition_args: List<Any?>): String {
        var conditionString = " WHERE $this"
        val conditionArguments = condition_args.toMutableList()

        // process '&'
        while (conditionString.contains('&')) {
            conditionString = conditionString.replace("&", " AND ")
        }

        if (conditionString.count { it == '?' } != conditionArguments.size) return ""

        // process arguments
        while (conditionString.contains('?')) {
            if (conditionArguments.size == 0) break
            conditionString = conditionString.replace("?", formatTypedString(conditionArguments[0]))
            conditionArguments.removeAt(0)
        }

        return conditionString
    }

    private fun formatTypedString(obj: Any?): String {
        return when (obj) {
            // If it's number or boolean, do not surround with ""
            is Number -> "$obj"
            is Boolean -> "$obj"
            null -> "null"
            else -> "\"$obj\""
        }
    }
}