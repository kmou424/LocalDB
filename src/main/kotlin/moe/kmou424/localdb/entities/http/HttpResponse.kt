package moe.kmou424.localdb.entities.http

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.send(response: HttpResponse) {
    this.respond(response.statusCode, response.message)
}

fun HttpResponse.reinsert(vararg pairs: Pair<String, Any?>): HttpResponse {
    val map = this.message.toMutableMap()
    for (pair in pairs) {
        map[pair.first] = pair.second
    }
    this.message = map.toMap()
    return this
}

enum class HttpResponse(
    val statusCode: HttpStatusCode,
    var message: Map<String, Any?>
) {
    OK(HttpStatusCode.OK, mapOf("message" to "ok")),
    FAILED(HttpStatusCode.BadRequest, mapOf("message" to "failed")),

    TOKEN_EXPIRED(HttpStatusCode.BadRequest, mapOf("message" to "token expired"))
}