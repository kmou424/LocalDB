package moe.kmou424.sqlite.utils

import moe.kmou424.common.utils.TypeUtil
import moe.kmou424.sqlite.enums.KeyExtra
import moe.kmou424.sqlite.enums.KeyType

object SQLFormatter {
    fun formatSQLCreateTable(
        table: String,
        columnsWithTypes: List<Pair<String, KeyType>>,
        columnsExtra: List<List<KeyExtra>>
    ): String {
        val cols = emptyArray<String>().toMutableList()
        for (idx in columnsWithTypes.indices) {
            val col = emptyArray<String>().toMutableList().also {
                it.add(columnsWithTypes[idx].first)
                it.add(columnsWithTypes[idx].second.sql)
                if (columnsExtra[idx].contains(KeyExtra.PRIMARYKEY)) {
                    it.add(KeyExtra.PRIMARYKEY.sql)
                    if (columnsExtra[idx].contains(KeyExtra.AUTOINCREMENT) && columnsWithTypes[idx].second.autoInc)
                        it.add(KeyExtra.AUTOINCREMENT.sql)
                }
                if (columnsExtra[idx].contains(KeyExtra.NOTNULL))
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

    fun formatSQLInsert(table: String, columnsWithValues: List<Any?>, ignoreKeys: List<String>?): String {
        // prepare data source
        val columnsWithValuesAct: MutableList<Pair<String, Any?>> = mutableListOf<Pair<String, Any?>>().also {
            for (item in columnsWithValues) {
                it.add(TypeUtil.getPairMapper(item) ?: return String())
            }
        }

        val columns = emptyList<String>().toMutableList()
        val values = emptyList<String>().toMutableList()

        for (item in columnsWithValuesAct) {
            // ignore some key
            if (ignoreKeys != null && ignoreKeys.contains(item.first))
                continue
            columns.add(item.first)
            values.add(formatTypedString(item.second))
        }

        return "INSERT INTO $table (${columns.joinToString(", ")}) VALUES (${values.joinToString(", ")});"
    }

    fun formatSQLQuery(table: String, columnsWithTypes: List<Pair<String, KeyType>>): String {
        return "SELECT %s FROM %s".format(
            emptyList<String>().toMutableList().also { for (item in columnsWithTypes) it.add(item.first) }.joinToString(", "),
            table
        )
    }

    fun formatSQLUpdateTable(table: String, keysWithValues: List<Any?>): String {
        // prepare data source
        val keysWithValuesAct: MutableList<Pair<String, Any?>> = mutableListOf<Pair<String, Any?>>().also {
            for (item in keysWithValues) {
                it.add(TypeUtil.getPairMapper(item) ?: return String())
            }
        }

        val updateList = emptyList<String>().toMutableList().also {
            for (i in keysWithValuesAct) {
                it.add("%s = %s".format(
                    i.first,
                    formatTypedString(i.second)
                ))
            }
        }
        return "UPDATE $table SET ${updateList.joinToString(", ")}"
    }

    fun String.formatSQLCondition(conditionArgs: List<Any?>): String {
        var conditionString = " WHERE $this"
        val conditionArguments = conditionArgs.toMutableList()

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