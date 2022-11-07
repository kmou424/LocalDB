package moe.kmou424.localdb.dao

import moe.kmou424.localdb.entities.database.sys.AppApplicationTable
import moe.kmou424.localdb.entities.database.sys.AppUserTable
import moe.kmou424.sqlite.SQLiteManager
import moe.kmou424.sqlite.enums.ColumnType
import moe.kmou424.sqlite.utils.TokenUtil.getUniqueToken

class AppSQLiteManager(dbPath: String) : SQLiteManager(dbPath) {

    object AppTables {
        const val Users = "_Users"
        const val Applications = "_Applications"
    }

    private fun insertApplication(authorizedDataBase: AppApplicationTable): Boolean {
        if (this.query<AppApplicationTable>(
                AppTables.Applications,
                columnsWithTypes = listOf("id" to ColumnType.INTEGER),
                condition = "database=?",
                conditionArgs = listOf(authorizedDataBase.database)
            ).isNotEmpty()
        ) return false

        this.insert(AppTables.Applications, data = authorizedDataBase, ignoreKeys = listOf("id"))
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

        val userDataBase = AppApplicationTable(
            applicationKey = this.getUniqueToken<AppApplicationTable>(),
            database = user.name
        )

        this.insertApplication(userDataBase)
        user.applicationKeyOwned = userDataBase.applicationKey

        this.insert(AppTables.Users, data = user, ignoreKeys = listOf("id"))
        return true
    }
}