package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import moe.kmou424.Global
import moe.kmou424.localdb.appDataBase
import moe.kmou424.localdb.dao.database.SysUserSchema
import moe.kmou424.localdb.dao.http.AuthUser
import moe.kmou424.localdb.utils.AESUtil
import moe.kmou424.localdb.utils.SimpleTokenUtil
import moe.kmou424.sqlite.enums.KeyType
import java.time.LocalDateTime

fun Application.configureAuth() {
    routing {
        post("/auth") {
            val user = call.receive<AuthUser>()
            var token: String? = null

            var needUpdate = false

            var status = "ok"
            var message = ""

            val data = appDataBase.query<SysUserSchema>(
                Global.SysTables.Users,
                listOf(
                    Pair("id", KeyType.INTEGER),
                    Pair("name", KeyType.TEXT),
                    Pair("password", KeyType.TEXT),
                    Pair("tokenWillExpire", KeyType.BOOLEAN),
                    Pair("token", KeyType.TEXT),
                    Pair("tokenExpireTime", KeyType.DATETIME)
                ),
                "name=?",
                listOf(user.name)
            )

            if (data.size == 1) {
                val u = data[0]
                if (u.password == AESUtil.encrypt(user.password)) {
                    if (!u.tokenWillExpire) {
                        token = u.token ?: let {
                            needUpdate = true
                            SimpleTokenUtil.generate()
                        }
                    } else {
                        if (u.tokenExpireTime != null && LocalDateTime.now().isBefore(LocalDateTime.parse(u.tokenExpireTime))) {
                            token = u.token
                        } else {
                            token = u.token ?: let {
                                needUpdate = true
                                SimpleTokenUtil.generate()
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

            call.respond(mapOf(
                "status" to status,
                "message" to message,
                "token" to token
            ))
        }
    }
}