package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import moe.kmou424.Global
import moe.kmou424.common.utils.AesUtil
import moe.kmou424.common.utils.JsonType
import moe.kmou424.localdb.appConfiguration
import moe.kmou424.localdb.appDataBase
import moe.kmou424.localdb.dao.database.SysUserSchema
import moe.kmou424.sqlite.enums.ColumnRestrict
import moe.kmou424.sqlite.enums.ColumnType
import moe.kmou424.sqlite.utils.TokenUtil.getUniqueTokenForUserType

fun Application.configureApp() {
    routing {
        post("/app/{target}") {
            call.respond(
                when (call.parameters["target"]) {
                    "init" -> initAppDataBase()
                    else -> {}
                }
            )
        }
    }
}

fun initAppDataBase(): JsonType {
    // Create auth user table
    appDataBase.create(
        Global.SysTables.Users,
        mapOf(
            Pair("id", ColumnType.INTEGER) to listOf(ColumnRestrict.NOTNULL, ColumnRestrict.PRIMARYKEY, ColumnRestrict.AUTOINCREMENT),
            Pair("name", ColumnType.TEXT) to listOf(ColumnRestrict.NOTNULL),
            Pair("password", ColumnType.TEXT) to listOf(ColumnRestrict.NOTNULL),
            Pair("tokenWillExpire", ColumnType.BOOLEAN) to listOf(ColumnRestrict.NOTNULL),
            Pair("token", ColumnType.TEXT) to emptyList(),
            Pair("tokenExpireTime", ColumnType.DATETIME) to emptyList()
        )
    )

    // Insert admin user
    val adminUser = SysUserSchema(
        name = appConfiguration.admin.username,
        password = appConfiguration.admin.password.let { password ->
            if (appConfiguration.encrypt.enabled)
                return@let AesUtil.encrypt(password)
            return@let password
        },
        token = appDataBase.getUniqueTokenForUserType<SysUserSchema>(),
        tokenWillExpire = false
    )
    appDataBase.insert(Global.SysTables.Users, data = adminUser, ignoreKeys = listOf("id"))

    return mapOf(
        "status" to "ok"
    )
}