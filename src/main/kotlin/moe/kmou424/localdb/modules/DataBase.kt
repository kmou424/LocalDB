package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureDataBase() {
    routing {
        // Table control
        post("/api/db/create") {
            call.respond(
                mapOf(
                "status" to "ok"
                )
            )
        }

        post("/api/db/alter") {

        }

        post("/api/db/drop") {

        }

        // Schema control
        post("/api/db/delete") {

        }

        post("/api/db/insert") {
            call.request.queryParameters
        }

        post("/api/db/query") {

        }

        post("/api/db/update") {

        }
    }
}