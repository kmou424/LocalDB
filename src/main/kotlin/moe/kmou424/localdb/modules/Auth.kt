package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import moe.kmou424.common.utils.AesUtil
import moe.kmou424.localdb.appDataBase
import moe.kmou424.localdb.entities.database.sys.SysUserTable
import moe.kmou424.localdb.entities.http.HttpResponse
import moe.kmou424.localdb.entities.http.User
import moe.kmou424.localdb.entities.http.reinsert
import moe.kmou424.localdb.entities.http.send
import moe.kmou424.sqlite.utils.TokenUtil.getUniqueToken
import java.time.LocalDateTime

fun Application.configureAuth() {
    routing {
        post("/v1/auth/{target}") {
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
    var token: String = appDataBase.getUniqueToken()
    val response = HttpResponse.OK
    var needUpdate = false
    var sysUser = SysUserTable()
    var useToken = true

    if (appDataBase.sysUsersData[user.token] != null) {
        sysUser = appDataBase.sysUsersData[user.token]!!
    } else {
        for (item in appDataBase.sysUsersData.values) {
            if (item.name == user.username && item.password == AesUtil.encrypt(user.password)) {
                sysUser = item
                break
            }
        }
        useToken = false
    }

    if (sysUser == SysUserTable()) {
        return HttpResponse.FAILED.reinsert(
            "message" to "can't authorize this user",
            "token" to null
        )
    }

    if (useToken) {
        if (sysUser.tokenExpireTime != null && LocalDateTime.now().isAfter(LocalDateTime.parse(sysUser.tokenExpireTime))) {
            return HttpResponse.TOKEN_EXPIRED.reinsert("token" to null)
        }
    } else {
        if (!sysUser.tokenWillExpire) {
            token = sysUser.token
        } else {
            if (sysUser.tokenExpireTime != null && LocalDateTime.now().isBefore(LocalDateTime.parse(sysUser.tokenExpireTime))) {
                token = sysUser.token
            } else {
                token = sysUser.token.ifEmpty {
                    needUpdate = true
                    token
                }
            }
        }
    }

    if (needUpdate) {
        sysUser.token = token
        appDataBase.update("Users", sysUser, "name=?", listOf(sysUser.name))
    }

    return response.reinsert(
        "token" to token
    )
}