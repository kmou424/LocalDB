package moe.kmou424

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlin.test.*
import io.ktor.server.testing.*
import moe.kmou424.Global.DefaultObjectMapper
import moe.kmou424.localdb.appConfiguration
import moe.kmou424.localdb.modules.*
import moe.kmou424.localdb.utils.AppDataUtil
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@FixMethodOrder(MethodSorters.JVM)
class ApplicationTest {
    companion object {
        val AppHome = AppDataUtil.getApplicationDataDir(false).getSelfFile()
        val AppHomeBak = File(AppHome.parent, "${AppHome.name}_bak")
        val AppHomeTestResult = File(AppHome.parent, "${AppHome.name}_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}")
    }

    @Test
    fun initApp() {
        testApplication {
            install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT)
                }
            }
            application {
                configureApp()
                configureAuth()
            }
            client.post("/app/init")
            client.post("/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(DefaultObjectMapper.writeValueAsString(mapOf(
                    "username" to appConfiguration.admin.username,
                    "password" to appConfiguration.admin.password
                )))
            }
        }
    }
}