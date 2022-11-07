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

private fun <T> toMessageMap(status: T?, message: T? = null, vararg pairs: Pair<String, T?>): Map<String, T?> {
    return HashMap<String, T?>().toMutableMap().also { map ->
        if (status != null) {
            map["status"] = status
        }
        if (message != null) {
            map["message"] = message
        }
        for (pair in pairs) {
            map[pair.first] = pair.second
        }
    }.toMap()
}

object HttpStatusText {
    const val OK = "ok"
    const val FAILED = "failed"
}

private const val OK = HttpStatusText.OK
private const val FAILED = HttpStatusText.OK

enum class HttpResponse(
    val statusCode: HttpStatusCode,
    var message: Map<String, Any?>
) {
    OK
        (HttpStatusCode.OK, toMessageMap(OK)),
    FAILED
        (HttpStatusCode.BadRequest, toMessageMap(FAILED)),

    // Auth.kt
    TOKEN_EXPIRED
        (HttpStatusCode.BadRequest, toMessageMap(FAILED, "token is expired")),

    // DataBase.kt
    PARAM_NO_TOKEN
        (HttpStatusCode.BadRequest, toMessageMap(FAILED, "\"token\" must not be empty")),
    PARAM_TOKEN_INVALID
        (HttpStatusCode.BadRequest, toMessageMap(FAILED, "\"token\" is invalid")),
    PARAM_NO_TABLE_NAME
        (HttpStatusCode.BadRequest, toMessageMap(FAILED, "\"table\" must not be empty")),
}