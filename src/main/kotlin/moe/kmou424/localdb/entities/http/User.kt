package moe.kmou424.localdb.entities.http

data class User(
    val username: String,
    val password: String,
    val token: String?
)
