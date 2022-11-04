package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureDataBase() {
    routing {
        post("/api/db/{operation}") {
            call.respond(
                when (call.parameters["operation"]) {
                    "alter" -> mapOf("status" to "ok")
                    "drop" -> mapOf("status" to "ok")
                    "insert" -> mapOf("status" to "ok")
                    "delete" -> mapOf("status" to "ok")
                    "update" -> mapOf("status" to "ok")
                    "query" -> mapOf("status" to "ok")
                    else -> {}
                }
            )
        }
    }
}