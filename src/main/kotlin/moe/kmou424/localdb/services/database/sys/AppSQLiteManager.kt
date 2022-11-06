package moe.kmou424.localdb.services.database.sys

import moe.kmou424.localdb.dao.database.sys.AppAuthorizedDataBaseTable
import moe.kmou424.localdb.dao.database.sys.AppUserTable
import moe.kmou424.sqlite.SQLiteManager
import moe.kmou424.sqlite.enums.ColumnType
import moe.kmou424.sqlite.utils.TokenUtil.getUniqueToken

class AppSQLiteManager(dbPath: String) : SQLiteManager(dbPath) {

    object AppTables {
        const val Users = "_Users"
        const val AuthorizedDataBase = "_AuthorizedDataBase"
    }

    private fun insertAuthorizedDataBase(authorizedDataBase: AppAuthorizedDataBaseTable): Boolean {
        if (this.query<AppAuthorizedDataBaseTable>(
                AppTables.AuthorizedDataBase,
                columnsWithTypes = listOf("id" to ColumnType.INTEGER),
                condition = "databaseName=?",
                conditionArgs = listOf(authorizedDataBase.databaseName)
            ).isNotEmpty()
        ) return false

        this.insert(AppTables.AuthorizedDataBase, data = authorizedDataBase, ignoreKeys = listOf("id"))
        return true
    }

    fun insertUser(user: AppUserTable): Boolean {
        if (this.query<AppUserTable>(
                AppTables.Users,
                columnsWithTypes = listOf("id" to ColumnType.INTEGER),
                condition = "name=?",
                conditionArgs = listOf(user.name)
            ).isNotEmpty()
        ) return false

        val userDataBase = AppAuthorizedDataBaseTable(
            databaseKey = this.getUniqueToken<AppAuthorizedDataBaseTable>(),
            databaseName = user.name
        )

        this.insertAuthorizedDataBase(userDataBase)
        user.databaseKeyOwned = userDataBase.databaseKey

        this.insert(AppTables.Users, data = user, ignoreKeys = listOf("id"))
        return true
    }
}