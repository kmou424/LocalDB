package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.routing.*
import moe.kmou424.common.utils.AesUtil
import moe.kmou424.localdb.appConfiguration
import moe.kmou424.localdb.appDataBase
import moe.kmou424.localdb.dao.AppSQLiteManager
import moe.kmou424.localdb.entities.database.sys.AppApplicationTable
import moe.kmou424.localdb.entities.database.sys.AppUserTable
import moe.kmou424.localdb.entities.http.HttpResponse
import moe.kmou424.localdb.entities.http.reinsert
import moe.kmou424.localdb.entities.http.send
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
            "token" to ColumnType.TEXT to emptyList(),
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
    appDataBase.query<AppApplicationTable>(
        AppSQLiteManager.AppTables.Applications,
        listOf("id" to ColumnType.INTEGER),
        condition = "database=?",
        conditionArgs = listOf(appConfiguration.admin.username)
    )

    // Insert admin user
    val adminUser = AppUserTable(
        name = appConfiguration.admin.username,
        password = appConfiguration.admin.password.let { password ->
            if (appConfiguration.encrypt.enabled)
                return@let AesUtil.encrypt(password)
            return@let password
        },
        token = appDataBase.getUniqueToken<AppUserTable>(),
        tokenWillExpire = false,
        applicationKeyOwned = ""
    )
    appDataBase.insertUser(adminUser)
}

private fun initApp(): HttpResponse {
    initAppDataBase()
    initAdminUser()

    return HttpResponse.OK
}