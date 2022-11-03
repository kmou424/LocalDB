package moe.kmou424.localdb.dao.http

data class AuthUser(
    val name: String,
    val password: String,
    val token: String?
)
