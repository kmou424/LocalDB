package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import moe.kmou424.Global
import moe.kmou424.localdb.appConfiguration
import moe.kmou424.localdb.appDataBase
import moe.kmou424.localdb.dao.database.SysUserSchema
import moe.kmou424.common.utils.AesUtil
import moe.kmou424.common.utils.SimpleTokenUtil
import moe.kmou424.sqlite.enums.KeyExtra
import moe.kmou424.sqlite.enums.KeyType
import moe.kmou424.sqlite.utils.TokenUtil.getUniqueTokenForUserType

fun initAppDataBase() {
    appDataBase.create(
        Global.SysTables.Users,
        listOf(
            Pair("id", KeyType.INTEGER),
            Pair("name", KeyType.TEXT),
            Pair("password", KeyType.TEXT),
            Pair("tokenWillExpire", KeyType.BOOLEAN),
            Pair("token", KeyType.TEXT),
            Pair("tokenExpireTime", KeyType.DATETIME)
        ),
        listOf(
            listOf(KeyExtra.NOTNULL, KeyExtra.AUTOINCREMENT, KeyExtra.PRIMARYKEY),
            listOf(KeyExtra.NOTNULL),
            listOf(KeyExtra.NOTNULL),
            listOf(KeyExtra.NOTNULL),
            emptyList(),
            emptyList()
        )
    )
    val adminUser = SysUserSchema(
        name = appConfiguration.admin.username,
        password = appConfiguration.admin.password.let { password ->
            if (appConfiguration.encrypt.enabled)
                return@let AesUtil.encrypt(password)
            return@let password
        },
        token = SimpleTokenUtil.getUniqueTokenForUserType<SysUserSchema>(appDataBase),
        tokenWillExpire = false
    )
    appDataBase.insert(Global.SysTables.Users, adminUser, ignoreKeys = listOf("id"))
}

fun Application.configureApp() {
    routing {
        post("/app/init") {
            initAppDataBase()
            call.respond(mapOf(
                "status" to "ok"
            ))
        }
    }
}