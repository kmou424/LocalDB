package moe.kmou424.sqlite.utils

import moe.kmou424.common.utils.SimpleTokenUtil
import moe.kmou424.localdb.dao.AppSQLiteManager
import moe.kmou424.localdb.entities.database.sys.AppApplicationTable
import moe.kmou424.localdb.entities.database.sys.AppUserTable
import moe.kmou424.sqlite.ColumnName
import moe.kmou424.sqlite.entities.SQLiteTable
import moe.kmou424.sqlite.enums.ColumnType

object TokenUtil {

    inline fun <reified T : SQLiteTable> AppSQLiteManager.getUniqueToken(): String {
        var token = SimpleTokenUtil.generate()
        var query = listOf<T>().toMutableList().also {
            it.add(T::class.java.getConstructor().newInstance())
        }.toList()
        if (query[0] is AppUserTable)

        while (query.isNotEmpty()) {
            token = SimpleTokenUtil.generate()
            query = when {
                query[0] is AppUserTable -> this.query(
                    AppSQLiteManager.AppTables.Users,
                    listOf("id" to ColumnType.INTEGER),
                    "token=?",
                    listOf(token)
                )
                query[0] is AppApplicationTable -> this.query(
                    AppSQLiteManager.AppTables.Applications,
                    listOf("id" to ColumnType.INTEGER),
                    "applicationKey=?",
                    listOf(token)
                )
                else -> emptyList()
            }
        }

        return token
    }

    inline fun <reified T : SQLiteTable> AppSQLiteManager.verifyToken(token: String, tokenColumnName: ColumnName): Boolean {
        return this.query<SQLiteTable>(
            when (T::class.java.getConstructor().newInstance()) {
                is AppUserTable -> AppSQLiteManager.AppTables.Users
                is AppApplicationTable -> AppSQLiteManager.AppTables.Applications
                else -> return false
            },
            listOf("id" to ColumnType.INTEGER),
            "$tokenColumnName=?",
            listOf(token)
        ).isNotEmpty()
    }

}