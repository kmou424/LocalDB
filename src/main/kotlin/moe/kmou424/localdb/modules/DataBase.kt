package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.routing.*
import moe.kmou424.localdb.DataBaseSubDirs
import moe.kmou424.localdb.appDataBase
import moe.kmou424.localdb.appSqlConnections
import moe.kmou424.localdb.dao.AppSQLiteManager
import moe.kmou424.localdb.entities.database.sys.AppAuthorizedDataBaseTable
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

            call.request.queryParameters["databaseKey"].let { databaseKey ->
                if (!databaseKey.isNullOrEmpty()) {
                    if (appSqlConnections[databaseKey] != null) return@let

                    // Do database permission check

                    // try to check user owned database
                    appDataBase.query<AppUserTable>(
                        AppSQLiteManager.AppTables.Users,
                        listOf(
                            "token" to ColumnType.TEXT
                        ),
                        "databaseKeyOwned LIKE ?",
                        listOf(databaseKey)
                    ).let { list ->
                        if (list.isEmpty()) {
                            call.send(HttpResponse.FAILED.reinsert("message" to "\"databaseKey\" is invalid"))
                            return@post
                        } else if (list[0].token != token) {
                            call.send(HttpResponse.FAILED.reinsert("message" to "database is not belong to this user"))
                            return@post
                        }
                    }

                    // to get database name
                    appDataBase.query<AppAuthorizedDataBaseTable>(
                        AppSQLiteManager.AppTables.AuthorizedDataBase,
                        listOf(
                            "databaseKey" to ColumnType.TEXT,
                            "databaseName" to ColumnType.TEXT
                        ),
                        "databaseKey=?",
                        listOf(databaseKey)
                    ).let { list ->
                        if (list.isEmpty()) {
                            call.send(HttpResponse.FAILED.reinsert("message" to "can't find this database"))
                        } else {
                            appSqlConnections[databaseKey] = DataBaseSubDirs.user.getFile(list[0].databaseName)
                                .getSelfFile().absolutePath.run {
                                    return@run SQLiteManager(this)
                                }
                        }
                    }
                } else {
                    call.send(HttpResponse.FAILED.reinsert("message" to "\"databaseKey\" must not be empty"))
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