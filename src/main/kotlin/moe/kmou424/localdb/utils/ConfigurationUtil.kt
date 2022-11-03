package moe.kmou424.localdb.utils

import moe.kmou424.Global
import moe.kmou424.localdb.dao.config.Configuration
import java.io.FileNotFoundException

object ConfigurationUtil {
    private val appConfigurationFile = AppDataUtil.ConfigDir.getFile("app.json").getSelfFile()

    fun getAppConfiguration(): Configuration {
        appConfigurationFile.run {
            return if (exists()) {
                readJsonData()
            } else {
                createNewFile()
                Configuration().also { writeJsonData(it) }
            }
        }
    }

    fun Configuration.saveAppConfiguration() {
        appConfigurationFile.writeJsonData(this)
    }

    private fun java.io.File.writeJsonData(data: Configuration) {
        if (!this.exists()) throw FileNotFoundException(""""${this.absolutePath}" is not found""")
        this.writeText(Global.GlobalPrettyObjectWriter.writeValueAsString(data), charset = Global.DefaultCharset)
    }

    private fun java.io.File.readJsonData(): Configuration {
        if (!this.exists()) throw FileNotFoundException(""""${this.absolutePath}" is not found""")
        val jsonTextData = this.readText(charset = Global.DefaultCharset)
        return Global.GlobalObjectMapper.readValue(jsonTextData, Configuration::class.java)
    }
}