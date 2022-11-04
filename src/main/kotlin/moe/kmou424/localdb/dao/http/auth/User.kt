package moe.kmou424.localdb.dao.http.auth

data class User(
    val username: String,
    val password: String,
    val token: String?
)
