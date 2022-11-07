package moe.kmou424.localdb.entities.http

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.send(response: HttpResponse) {
    response.reinsert(
        "status" to response.name
    )
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

private fun <T> toMessageMap(message: T? = null, vararg pairs: Pair<String, T?>): Map<String, T?> {
    return HashMap<String, T?>().toMutableMap().also { map ->
        if (message != null) {
            map["message"] = message
        }
        for (pair in pairs) {
            map[pair.first] = pair.second
        }
    }.toMap()
}

private val Success = HttpStatusCode.OK
private val BadRequest = HttpStatusCode.BadRequest

enum class HttpResponse(
    val statusCode: HttpStatusCode,
    var message: Map<String, Any?>
) {
    OK
        (Success, toMessageMap()),
    FAILED
        (BadRequest, toMessageMap()),

    // Auth.kt
    TOKEN_EXPIRED
        (BadRequest, toMessageMap("token is expired")),

    // DataBase.kt
    PARAM_NO_TOKEN
        (BadRequest, toMessageMap("\"token\" must not be empty")),
    PARAM_TOKEN_INVALID
        (BadRequest, toMessageMap("\"token\" is invalid")),
    PARAM_APPLICATION_KET_NOT_MATCH
        (BadRequest, toMessageMap("\"applicationKey\" is invalid or not match")),
    PARAM_NO_APPLICATION_KEY
        (BadRequest, toMessageMap("\"applicationKey\" must not be empty")),
    PARAM_NO_TABLE_NAME
        (BadRequest, toMessageMap("\"table\" must not be empty")),

    APPLICATION_NOT_FOUND
        (BadRequest, toMessageMap("application is not found"))
}