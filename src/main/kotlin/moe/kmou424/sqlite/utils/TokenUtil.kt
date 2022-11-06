package moe.kmou424.sqlite.utils

import moe.kmou424.common.utils.SimpleTokenUtil
import moe.kmou424.localdb.dao.database.sys.AppAuthorizedDataBaseTable
import moe.kmou424.localdb.dao.database.sys.AppUserTable
import moe.kmou424.localdb.services.database.sys.AppSQLiteManager
import moe.kmou424.sqlite.SQLiteManager
import moe.kmou424.sqlite.enums.ColumnType

object TokenUtil {

    inline fun <reified T> SQLiteManager.getUniqueToken(): String {
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
                query[0] is AppAuthorizedDataBaseTable -> this.query(
                    AppSQLiteManager.AppTables.AuthorizedDataBase,
                    listOf("id" to ColumnType.INTEGER),
                    "databaseKey=?",
                    listOf(token)
                )
                else -> emptyList()
            }
        }

        return token
    }

}