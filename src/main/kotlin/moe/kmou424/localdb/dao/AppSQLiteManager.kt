package moe.kmou424.localdb.dao

import moe.kmou424.localdb.entities.database.sys.SysApplicationTable
import moe.kmou424.localdb.entities.database.sys.SysUserTable
import moe.kmou424.sqlite.SQLiteManager
import moe.kmou424.sqlite.enums.ColumnType
import moe.kmou424.sqlite.utils.TokenUtil.getUniqueToken

class AppSQLiteManager(dbPath: String) : SQLiteManager(dbPath) {

    object AppTables {
        const val Users = "_Users"
        const val Applications = "_Applications"
    }

    lateinit var sysUsersData: HashMap<String, SysUserTable>
    lateinit var sysApplicationsData: HashMap<String, SysApplicationTable>

    private fun insertApplication(authorizedDataBase: SysApplicationTable): Boolean {
        if (this.query<SysApplicationTable>(
                AppTables.Applications,
                columnsWithTypes = listOf("id" to ColumnType.INTEGER),
                condition = "database=?",
                conditionArgs = listOf(authorizedDataBase.database)
            ).isNotEmpty()
        ) return false

        this.insert(AppTables.Applications, data = authorizedDataBase, ignoreKeys = listOf("id"))
        notifyApplicationsTableChanged()
        return true
    }

    fun insertUser(user: SysUserTable): Boolean {
        if (this.query<SysUserTable>(
                AppTables.Users,
                columnsWithTypes = listOf("id" to ColumnType.INTEGER),
                condition = "name=?",
                conditionArgs = listOf(user.name)
            ).isNotEmpty()
        ) return false

        val userDataBase = SysApplicationTable(
            applicationKey = this.getUniqueToken(),
            database = user.name
        )

        this.insertApplication(userDataBase)
        user.applicationKeyOwned = userDataBase.applicationKey

        this.insert(AppTables.Users, data = user, ignoreKeys = listOf("id"))
        notifyUsersTableChanged()
        return true
    }

    fun notifyUsersTableChanged() {
        sysUsersData = HashMap<String, SysUserTable>().also {
            val list = this.query<SysUserTable>(
                AppTables.Users,
                listOf(
                    "id" to ColumnType.INTEGER,
                    "name" to ColumnType.TEXT,
                    "password" to ColumnType.TEXT,
                    "tokenWillExpire" to ColumnType.BOOLEAN,
                    "token" to ColumnType.TEXT,
                    "tokenExpireTime" to ColumnType.DATETIME,
                    "applicationKeyOwned" to ColumnType.TEXT
                )
            )
            for (i in list) {
                it[i.token] = i
            }
        }
    }

    fun notifyApplicationsTableChanged() {
        sysApplicationsData = HashMap<String, SysApplicationTable>().also {
            val list = this.query<SysApplicationTable>(
                AppTables.Applications,
                listOf(
                    "id" to ColumnType.INTEGER,
                    "applicationKey" to ColumnType.TEXT,
                    "database" to ColumnType.TEXT
                )
            )
            for (i in list) {
                it[i.applicationKey] = i
            }
        }
    }
}