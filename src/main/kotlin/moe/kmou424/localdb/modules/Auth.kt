package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import moe.kmou424.Global
import moe.kmou424.localdb.appDataBase
import moe.kmou424.localdb.dao.database.SysUserSchema
import moe.kmou424.localdb.dao.http.auth.User
import moe.kmou424.common.utils.AesUtil
import moe.kmou424.common.utils.JsonType
import moe.kmou424.common.utils.SimpleTokenUtil
import moe.kmou424.sqlite.enums.ColumnType
import moe.kmou424.sqlite.utils.TokenUtil.getUniqueTokenForUserType
import java.time.LocalDateTime

fun Application.configureAuth() {
    routing {
        post("/auth/{target}") {
            call.respond(
                when (call.parameters["target"]) {
                    "login" -> authLogin(call.receive())
                    else -> {}
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

    val data = appDataBase.query<SysUserSchema>(
        Global.SysTables.Users,
        listOf(
            Pair("id", ColumnType.INTEGER),
            Pair("name", ColumnType.TEXT),
            Pair("password", ColumnType.TEXT),
            Pair("tokenWillExpire", ColumnType.BOOLEAN),
            Pair("token", ColumnType.TEXT),
            Pair("tokenExpireTime", ColumnType.DATETIME)
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
                    SimpleTokenUtil.getUniqueTokenForUserType<SysUserSchema>(appDataBase)
                }
            } else {
                if (u.tokenExpireTime != null && LocalDateTime.now().isBefore(LocalDateTime.parse(u.tokenExpireTime))) {
                    token = u.token
                } else {
                    token = u.token ?: run {
                        needUpdate = true
                        SimpleTokenUtil.getUniqueTokenForUserType<SysUserSchema>(appDataBase)
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