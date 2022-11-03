package moe.kmou424.sqlite

import moe.kmou424.Global
import moe.kmou424.localdb.utils.getAttr
import moe.kmou424.sqlite.utils.SQLFormatter.formatSQLCondition
import moe.kmou424.sqlite.dao.SQLiteTable
import moe.kmou424.sqlite.enums.KeyExtra
import moe.kmou424.sqlite.enums.KeyType
import moe.kmou424.sqlite.utils.MapRender.toTypedObject
import moe.kmou424.sqlite.utils.SQLFormatter
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

open class SQLiteManager(private var dbPath: String) {
    val database: Database

    init {
        if (!dbPath.endsWith(".sqlite3"))
            dbPath += ".sqlite3"
        database = Database.connect(url = "jdbc:sqlite:$dbPath", driver = Global.SQLiteDriver)
    }

    fun create(table: String, columns_with_types: List<Pair<String, KeyType>>, columns_extra: List<List<KeyExtra>>) {
        transaction(database) {
            exec(SQLFormatter.formatSQLCreateTable(table, columns_with_types, columns_extra))
        }
    }

    fun delete(table: String, condition: String, condition_args: List<String>) {
        transaction(database) {
            exec("%s%s;".format(
                SQLFormatter.formatSQLDelete(table),
                condition.formatSQLCondition(condition_args)
            ))
        }
    }

    inline fun <reified T : SQLiteTable> insert(table: String, data: T) {
        transaction(database) {
            exec(SQLFormatter.formatSQLInsert(table, data.toPairList<T>()))
        }
    }

    inline fun <reified T : SQLiteTable> query(table: String, columns_with_types: List<Pair<String, KeyType>>,
                                               condition: String? = null, condition_args: List<Any?>? = null): List<T> {
        val result = emptyArray<T>().toMutableList()
        val conditionString = if (condition != null && condition_args != null) condition.formatSQLCondition(condition_args) else ""

        transaction(database) {
            exec("%s%s;".format(
                SQLFormatter.formatSQLQuery(table, columns_with_types),
                conditionString
            )) { resultSet ->
                while (resultSet.next()) {
                    val hashMap = HashMap<String, Any?>()
                    for (idx in columns_with_types.indices) {
                        hashMap[columns_with_types[idx].first] = resultSet.getAttr(columns_with_types[idx].second, columns_with_types[idx].first)
                    }
                    result.add(hashMap.toTypedObject(T::class.java))
                }
            }
        }
        return result.toList()
    }

    inline fun <reified T : SQLiteTable> update(table: String, data: T, condition: String, condition_args: List<String>) {
        transaction(database) {
            exec("%s%s;".format(
                SQLFormatter.formatSQLUpdateTable(table, data.toPairList<T>()), condition.formatSQLCondition(condition_args)))
        }
    }
}