package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import moe.kmou424.common.utils.AesUtil
import moe.kmou424.localdb.appDataBase
import moe.kmou424.localdb.dao.AppSQLiteManager
import moe.kmou424.localdb.entities.database.sys.AppUserTable
import moe.kmou424.localdb.entities.http.HttpResponse
import moe.kmou424.localdb.entities.http.User
import moe.kmou424.localdb.entities.http.reinsert
import moe.kmou424.localdb.entities.http.send
import moe.kmou424.sqlite.enums.ColumnType
import moe.kmou424.sqlite.utils.TokenUtil.getUniqueToken
import java.time.LocalDateTime

fun Application.configureAuth() {
    routing {
        post("/auth/{target}") {
            val target = call.parameters["target"]
            call.send(
                when (target) {
                    "login" -> authLogin(call.receive())
                    else -> HttpResponse.FAILED.reinsert("status" to "unsupported operation /auth/$target")
                }
            )
        }
    }
}

private fun authLogin(user: User): HttpResponse {
    var token: String? = null

    var response = HttpResponse.OK

    var needUpdate = false

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
        if (user.token == null) "name=?" else "token=?",
        if (user.token == null) listOf(user.username) else listOf(user.token)
    )

    if (data.size == 1) {
        val u = data[0]
        if (user.token != null && u.tokenExpireTime != null && LocalDateTime.now().isAfter(LocalDateTime.parse(u.tokenExpireTime))) {
            return HttpResponse.TOKEN_EXPIRED.reinsert("token" to null)
        }
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
            response = HttpResponse.FAILED.reinsert("message" to "Password is wrong")
        }

        if (needUpdate) {
            u.token = token
            appDataBase.update("Users", u, "name=?", listOf(u.name))
        }
    }

    return response.reinsert(
        "token" to token
    )
}