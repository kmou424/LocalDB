package moe.kmou424.localdb

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.thymeleaf.*
import moe.kmou424.localdb.dao.AppSQLiteManager
import moe.kmou424.localdb.modules.configureApp
import moe.kmou424.localdb.modules.configureAuth
import moe.kmou424.localdb.modules.configureDataBase
import moe.kmou424.localdb.modules.configureStatic
import moe.kmou424.localdb.utils.AppDataUtil
import moe.kmou424.localdb.utils.ConfigurationUtil
import moe.kmou424.sqlite.SQLiteManager
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

object DataBaseSubDirs {
    val sys by lazy {
        AppDataUtil.DataBaseDir
            .getDir("sys", true)
    }
    val user by lazy {
        AppDataUtil.DataBaseDir
            .getDir("user", true)
    }
}

val appConfiguration = ConfigurationUtil.getAppConfiguration()

val appSqlConnections = HashMap<String, SQLiteManager>()
val appDataBase: AppSQLiteManager = DataBaseSubDirs.sys.getFile("app")
    .getSelfFile().absolutePath.let {
        return@let AppSQLiteManager(it)
    }

fun main() {
    loadSysDataBase()
    embeddedServer(Netty, port = appConfiguration.server.port, host = appConfiguration.server.host,
        module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "static/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }
    configureStatic()

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    configureApp()
    configureAuth()
    configureDataBase()
}

fun loadSysDataBase() {
    if (appConfiguration.initialized) {
        appDataBase.notifyUsersTableChanged()
        appDataBase.notifyApplicationsTableChanged()
    }
}
