package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.routing.*
import moe.kmou424.common.utils.AesUtil
import moe.kmou424.localdb.appConfiguration
import moe.kmou424.localdb.appDataBase
import moe.kmou424.localdb.dao.AppSQLiteManager
import moe.kmou424.localdb.entities.database.sys.SysApplicationTable
import moe.kmou424.localdb.entities.database.sys.SysUserTable
import moe.kmou424.localdb.entities.http.HttpResponse
import moe.kmou424.localdb.entities.http.reinsert
import moe.kmou424.localdb.entities.http.send
import moe.kmou424.localdb.utils.ConfigurationUtil
import moe.kmou424.localdb.utils.ConfigurationUtil.saveAppConfiguration
import moe.kmou424.sqlite.enums.ColumnRestrict
import moe.kmou424.sqlite.enums.ColumnType
import moe.kmou424.sqlite.utils.TokenUtil.getUniqueToken

fun Application.configureApp() {
    routing {
        post("/v1/app/{target}") {
            val target = call.parameters["target"]
            call.send(
                when (target) {
                    "init" -> initApp()
                    else -> HttpResponse.FAILED.reinsert("message" to "unsupported operation /app/$target")
                }
            )
        }
    }
}

private fun initAppDataBase() {
    // Create auth user table
    appDataBase.create(
        AppSQLiteManager.AppTables.Users,
        mapOf(
            "id" to ColumnType.INTEGER to listOf(ColumnRestrict.NOTNULL, ColumnRestrict.PRIMARYKEY, ColumnRestrict.AUTOINCREMENT),
            "name" to ColumnType.TEXT to listOf(ColumnRestrict.NOTNULL),
            "password" to ColumnType.TEXT to listOf(ColumnRestrict.NOTNULL),
            "tokenWillExpire" to ColumnType.BOOLEAN to listOf(ColumnRestrict.NOTNULL),
            "token" to ColumnType.TEXT to listOf(ColumnRestrict.NOTNULL),
            "tokenExpireTime" to ColumnType.DATETIME to emptyList(),
            "applicationKeyOwned" to ColumnType.TEXT to listOf(ColumnRestrict.NOTNULL)
        )
    )

    // Create authed database table
    appDataBase.create(
        AppSQLiteManager.AppTables.Applications,
        mapOf(
            "id" to ColumnType.INTEGER to listOf(ColumnRestrict.NOTNULL, ColumnRestrict.PRIMARYKEY, ColumnRestrict.AUTOINCREMENT),
            "applicationKey" to ColumnType.TEXT to listOf(ColumnRestrict.NOTNULL),
            "database" to ColumnType.TEXT to listOf(ColumnRestrict.NOTNULL)
        )
    )
}

private fun initAdminUser() {
    // Insert admin user
    val adminUser = SysUserTable(
        name = appConfiguration.admin.username,
        password = appConfiguration.admin.password.let { password ->
            if (appConfiguration.encrypt.enabled)
                return@let AesUtil.encrypt(password)
            return@let password
        },
        token = appDataBase.getUniqueToken(),
        tokenWillExpire = false,
        applicationKeyOwned = ""
    )
    appDataBase.insertUser(adminUser)
}

private fun initApp(): HttpResponse {
    initAppDataBase()
    initAdminUser()

    appConfiguration.initialized = true
    appConfiguration.saveAppConfiguration()

    return HttpResponse.OK
}