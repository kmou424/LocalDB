package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import moe.kmou424.common.utils.AesUtil
import moe.kmou424.common.utils.JsonType
import moe.kmou424.localdb.appDataBase
import moe.kmou424.localdb.dao.database.sys.AppUserTable
import moe.kmou424.localdb.dao.http.auth.User
import moe.kmou424.localdb.services.database.sys.AppSQLiteManager
import moe.kmou424.sqlite.enums.ColumnType
import moe.kmou424.sqlite.utils.TokenUtil.getUniqueToken
import java.time.LocalDateTime

fun Application.configureAuth() {
    routing {
        post("/auth/{target}") {
            val target = call.parameters["target"]
            call.respond(
                when (target) {
                    "login" -> authLogin(call.receive())
                    else -> mapOf("status" to "unsupported operation /auth/$target")
                }
            )
        }
    }
}

private fun authLogin(user: User): JsonType {
    var token: String? = null

    var needUpdate = false

    var status = "ok"
    var message = ""

    val data = appDataBase.query<AppUserTable>(
        AppSQLiteManager.AppTables.Users,
        listOf(
            "id" to ColumnType.INTEGER,
            "name" to ColumnType.TEXT,
            "password" to ColumnType.TEXT,
            "tokenWillExpire" to ColumnType.BOOLEAN,
            "token" to ColumnType.TEXT,
            "tokenExpireTime" to ColumnType.DATETIME
        ),
        "name=?",
        listOf(user.username)
    )

    if (data.size == 1) {
        val u = data[0]
        if (u.password == AesUtil.encrypt(user.password)) {
            if (!u.tokenWillExpire) {
                token = u.token ?: run {
                    needUpdate = true
                    appDataBase.getUniqueToken<AppUserTable>()
                }
            } else {
                if (u.tokenExpireTime != null && LocalDateTime.now().isBefore(LocalDateTime.parse(u.tokenExpireTime))) {
                    token = u.token
                } else {
                    token = u.token ?: run {
                        needUpdate = true
                        appDataBase.getUniqueToken<AppUserTable>()
                    }
                }
            }
        } else {
            status = "failed"
            message = "Password is wrong"
        }

        if (needUpdate) {
            u.token = token
            appDataBase.update("Users", u, "name=?", listOf(u.name))
        }
    }

    return mapOf(
        "status" to status,
        "message" to message,
        "token" to token
    )
}