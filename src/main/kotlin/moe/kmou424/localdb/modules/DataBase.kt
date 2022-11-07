package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.routing.*
import moe.kmou424.localdb.DataBaseSubDirs
import moe.kmou424.localdb.appDataBase
import moe.kmou424.localdb.appSqlConnections
import moe.kmou424.localdb.entities.http.HttpResponse
import moe.kmou424.localdb.entities.http.reinsert
import moe.kmou424.localdb.entities.http.send
import moe.kmou424.sqlite.SQLiteManager

fun Application.configureDataBase() {
    routing {
        post("/v1/database/{operation}") {
            val operation = call.parameters["operation"]

            val token = call.request.queryParameters["token"] ?: let {
                call.send(HttpResponse.PARAM_NO_TOKEN)
                return@post
            }

            if (appDataBase.sysUsersData[token] == null) {
                call.send(HttpResponse.PARAM_TOKEN_INVALID)
                return@post
            }

            call.request.queryParameters["applicationKey"].let { applicationKey ->
                if (!applicationKey.isNullOrEmpty()) {
                    if (appSqlConnections[applicationKey] != null) return@let

                    // Do database permission check

                    // try to check user owned database
                    appDataBase.sysUsersData[token]!!.let { user ->
                        if (!user.applicationKeyOwned.contains(applicationKey)) {
                            call.send(HttpResponse.PARAM_APPLICATION_KET_NOT_MATCH)
                            return@post
                        }
                    }

                    // to get database name
                    appDataBase.sysApplicationsData[applicationKey].let { application ->
                        if (application == null) {
                            call.send(HttpResponse.APPLICATION_NOT_FOUND)
                            return@post
                        }
                        appSqlConnections[applicationKey] = DataBaseSubDirs.user.getFile(application.database)
                            .getSelfFile().absolutePath.run {
                                return@run SQLiteManager(this)
                            }
                    }
                } else {
                    call.send(HttpResponse.PARAM_NO_APPLICATION_KEY)
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