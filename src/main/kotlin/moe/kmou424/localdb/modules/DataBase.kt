package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.routing.*
import moe.kmou424.localdb.DataBaseSubDirs
import moe.kmou424.localdb.appDataBase
import moe.kmou424.localdb.appSqlConnections
import moe.kmou424.localdb.dao.AppSQLiteManager
import moe.kmou424.localdb.entities.database.sys.AppApplicationTable
import moe.kmou424.localdb.entities.database.sys.AppUserTable
import moe.kmou424.localdb.entities.http.HttpResponse
import moe.kmou424.localdb.entities.http.reinsert
import moe.kmou424.localdb.entities.http.send
import moe.kmou424.sqlite.SQLiteManager
import moe.kmou424.sqlite.enums.ColumnType
import moe.kmou424.sqlite.utils.TokenUtil.verifyToken

fun Application.configureDataBase() {
    routing {
        post("/v1/database/{operation}") {
            val operation = call.parameters["operation"]

            val token = call.request.queryParameters["token"] ?: let {
                call.send(HttpResponse.PARAM_NO_TOKEN)
                return@post
            }

            if (!appDataBase.verifyToken<AppUserTable>(token, "token")) {
                call.send(HttpResponse.PARAM_TOKEN_INVALID)
                return@post
            }

            call.request.queryParameters["applicationKey"].let { applicationKey ->
                if (!applicationKey.isNullOrEmpty()) {
                    if (appSqlConnections[applicationKey] != null) return@let

                    // Do database permission check

                    // try to check user owned database
                    appDataBase.query<AppUserTable>(
                        AppSQLiteManager.AppTables.Users,
                        listOf(
                            "token" to ColumnType.TEXT
                        ),
                        "applicationKeyOwned LIKE ?",
                        listOf(applicationKey)
                    ).let { list ->
                        if (list.isEmpty()) {
                            call.send(HttpResponse.FAILED.reinsert("message" to "\"applicationKey\" is invalid"))
                            return@post
                        } else if (list[0].token != token) {
                            call.send(HttpResponse.FAILED.reinsert("message" to "application is not belong to this user"))
                            return@post
                        }
                    }

                    // to get database name
                    appDataBase.query<AppApplicationTable>(
                        AppSQLiteManager.AppTables.Applications,
                        listOf(
                            "applicationKey" to ColumnType.TEXT,
                            "database" to ColumnType.TEXT
                        ),
                        "applicationKey=?",
                        listOf(applicationKey)
                    ).let { list ->
                        if (list.isEmpty()) {
                            call.send(HttpResponse.FAILED.reinsert("message" to "can't find this application"))
                        } else {
                            appSqlConnections[applicationKey] = DataBaseSubDirs.user.getFile(list[0].database)
                                .getSelfFile().absolutePath.run {
                                    return@run SQLiteManager(this)
                                }
                        }
                    }
                } else {
                    call.send(HttpResponse.FAILED.reinsert("message" to "\"applicationKey\" must not be empty"))
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