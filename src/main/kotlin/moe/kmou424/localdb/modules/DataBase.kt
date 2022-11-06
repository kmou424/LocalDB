package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import moe.kmou424.localdb.appDataBase
import moe.kmou424.localdb.dao.database.sys.AppUserTable
import moe.kmou424.sqlite.utils.TokenUtil.verifyToken

fun Application.configureDataBase() {
    routing {
        post("/api/db/{operation}") {
            val operation = call.parameters["operation"]

            val token = call.request.queryParameters["token"] ?: let {
                call.respond(mapOf("status" to "\"token\" must not be empty"))
                return@post
            }

            if (!appDataBase.verifyToken<AppUserTable>(token, "token")) {
                call.respond(mapOf("status" to "\"token\" is invalid"))
                return@post
            }

            when (operation) {
                "init" ->
                    if (call.request.queryParameters["databaseName"].isNullOrEmpty()) {
                        call.respond(mapOf("status" to "\"databaseName\" must not be empty"))
                        return@post
                    }
                else ->
                    if (call.request.queryParameters["databaseKey"].isNullOrEmpty()) {
                        call.respond(mapOf("status" to "\"databaseKey\" must not be empty"))
                        return@post
                    }
            }

            call.respond(
                when (operation) {
                    // table management
                    "create" -> mapOf("status" to "ok")
                    "drop" -> mapOf("status" to "ok")
                    "alter" -> mapOf("status" to "ok")

                    // record management
                    "insert" -> mapOf("status" to "ok")
                    "delete" -> mapOf("status" to "ok")
                    "update" -> mapOf("status" to "ok")
                    "query" -> mapOf("status" to "ok")
                    else -> mapOf("status" to "unsupported operation /api/db/$operation")
                }
            )
        }
    }
}