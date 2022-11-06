package moe.kmou424.sqlite

import moe.kmou424.Global
import moe.kmou424.common.utils.ResultSetUtil.getAttr
import moe.kmou424.sqlite.entities.SQLiteTable
import moe.kmou424.sqlite.enums.ColumnType
import moe.kmou424.sqlite.utils.MapRender.toTypedObject
import moe.kmou424.sqlite.utils.SQLFormatter
import moe.kmou424.sqlite.utils.SQLFormatter.formatSQLCondition
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

open class SQLiteManager(private var dbPath: String) {
    val database: Database

    init {
        if (!dbPath.endsWith(".sqlite3"))
            dbPath += ".sqlite3"
        database = Database.connect(url = "jdbc:sqlite:$dbPath", driver = Global.SQLiteDriver)
    }

    fun create(table: String, columnsWithTypes: ColumnMapper) {
        transaction(database) {
            exec(SQLFormatter.formatSQLCreateTable(table, columnsWithTypes))
        }
    }

    fun delete(table: String, condition: String, conditionArgs: List<String>) {
        transaction(database) {
            exec("%s%s;".format(
                SQLFormatter.formatSQLDelete(table),
                condition.formatSQLCondition(conditionArgs)
            ))
        }
    }

    inline fun <reified T> insert(table: String, data: T, ignoreKeys: List<String>? = null) {
        transaction(database) {
            exec(SQLFormatter.formatSQLInsert(table, when (data) {
                is SQLiteTable -> data.toPairList<T>()
                is List<*> -> data
                else -> return@transaction
            }, ignoreKeys).also {
                if (it.isEmpty()) return@transaction
            })
        }
    }

    inline fun <reified T : SQLiteTable> query(table: String, columnsWithTypes: List<Pair<String, ColumnType>>,
                                               condition: String? = null, conditionArgs: List<Any?>? = null): List<T> {
        val result = emptyArray<T>().toMutableList()
        val conditionString = if (condition != null && conditionArgs != null) condition.formatSQLCondition(conditionArgs) else ""

        transaction(database) {
            exec("%s%s;".format(
                SQLFormatter.formatSQLQuery(table, columnsWithTypes),
                conditionString
            )) { resultSet ->
                while (resultSet.next()) {
                    val hashMap = HashMap<String, Any?>()
                    for (idx in columnsWithTypes.indices) {
                        hashMap[columnsWithTypes[idx].first] = resultSet.getAttr(columnsWithTypes[idx].second, columnsWithTypes[idx].first)
                    }
                    result.add(hashMap.toTypedObject(T::class.java))
                }
            }
        }
        return result.toList()
    }

    inline fun <reified T> update(table: String, data: T, condition: String, conditionArgs: List<String>) {
        transaction(database) {
            exec("%s%s;".format(
                SQLFormatter.formatSQLUpdateTable(table, when (data) {
                    is SQLiteTable -> data.toPairList<T>()
                    is List<*> -> data
                    else -> return@transaction
                }).also {
                    if (it.isEmpty()) return@transaction
                }, condition.formatSQLCondition(conditionArgs)))
        }
    }
}