package moe.kmou424.localdb.modules

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun Application.configureStatic() {
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "static/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }

    routing {
        get("/") {
            call.respond(ThymeleafContent("index", mapOf("user" to ThymeleafUser(1, "user1"))))
        }
    }
}

data class ThymeleafUser(val id: Int, val name: String)
