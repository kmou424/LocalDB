package moe.kmou424.localdb.entities.config

import moe.kmou424.common.utils.SimpleTokenUtil

data class Configuration(
    var initialized: Boolean = false,
    val server: ServerConfigurationData = ServerConfigurationData(),
    val admin: AdminConfigurationData = AdminConfigurationData(),
    val encrypt: EncryptConfigurationData = EncryptConfigurationData(),
)

data class ServerConfigurationData(
    val host: String = "0.0.0.0",
    val port: Int = 8081
)

data class AdminConfigurationData(
    val username: String = "root",
    val password: String = "123456"
)

data class EncryptConfigurationData(
    val enabled: Boolean = true,
    val key: String = SimpleTokenUtil.generate(16)
)