package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureDataBase() {
    routing {
        // Table control
        post("/create") {
            call.respond(
                mapOf(
                "status" to "ok"
                )
            )
        }

        post("/alter") {

        }

        post("/drop") {

        }

        // Schema control
        post("/delete") {

        }

        post("/insert") {
            call.request.queryParameters
        }

        post("/query") {

        }

        post("/update") {

        }
    }
}