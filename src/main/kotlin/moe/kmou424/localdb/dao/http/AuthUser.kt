package moe.kmou424.localdb.dao.http

data class AuthUser(
    val username: String,
    val password: String,
    val token: String?
)
