package moe.kmou424.localdb.dao.config

import moe.kmou424.localdb.utils.SimpleTokenUtil

data class Configuration(
    val server: ServerConfigurationData = ServerConfigurationData(),
    val admin: AdminConfigurationData = AdminConfigurationData(),
    val database: DataBaseConfigurationData = DataBaseConfigurationData(),
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

data class DataBaseConfigurationData(
    val app: String = "app"
)

data class EncryptConfigurationData(
    val enabled: Boolean = true,
    val password: String = SimpleTokenUtil.generate(16)
)