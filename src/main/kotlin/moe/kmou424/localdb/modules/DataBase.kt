package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureDataBase() {
    routing {
        post("/api/db/{operation}") {
            val operation = call.parameters["operation"]

            when (operation) {
                "init" ->
                    if (call.request.queryParameters["databaseName"].isNullOrEmpty()) {
                        call.respond(mapOf("status" to "\"databaseName\" must not be empty"))
                        return@post
                    }
                else ->
                    if (call.request.queryParameters["databaseKey"].isNullOrEmpty()) {
                        call.respond(mapOf("status" to "\"databaseName\" must not be empty"))
                        return@post
                    }
            }

            call.respond(
                when (operation) {
                    // create database
                    "init" -> mapOf("status" to "ok")

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