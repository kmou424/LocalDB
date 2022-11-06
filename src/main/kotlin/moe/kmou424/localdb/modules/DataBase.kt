package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.routing.*
import moe.kmou424.localdb.appDataBase
import moe.kmou424.localdb.entities.database.sys.AppUserTable
import moe.kmou424.localdb.entities.http.HttpResponse
import moe.kmou424.localdb.entities.http.reinsert
import moe.kmou424.localdb.entities.http.send
import moe.kmou424.sqlite.utils.TokenUtil.verifyToken

fun Application.configureDataBase() {
    routing {
        post("/api/db/{operation}") {
            val operation = call.parameters["operation"]

            val token = call.request.queryParameters["token"] ?: let {
                call.send(HttpResponse.FAILED.reinsert("message" to "\"token\" must not be empty"))
                return@post
            }

            if (!appDataBase.verifyToken<AppUserTable>(token, "token")) {
                call.send(HttpResponse.FAILED.reinsert("message" to "\"token\" is invalid"))
                return@post
            }

            when (operation) {
                "init" ->
                    if (call.request.queryParameters["databaseName"].isNullOrEmpty()) {
                        call.send(HttpResponse.FAILED.reinsert("message" to "\"databaseName\" must not be empty"))
                        return@post
                    }
            }

            call.send(
                when (operation) {
                    // table management
                    "create" -> HttpResponse.OK
                    "drop" -> HttpResponse.OK
                    "alter" -> HttpResponse.OK

                    // record management
                    "insert" -> HttpResponse.OK
                    "delete" -> HttpResponse.OK
                    "update" -> HttpResponse.OK
                    "query" -> HttpResponse.OK

                    else -> HttpResponse.FAILED.reinsert("message" to "unsupported operation /api/db/$operation")
                }
            )
        }
    }
}