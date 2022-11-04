package moe.kmou424.localdb.utils

import java.io.*

object SystemUtil {
    object Checker {
        /**
         * 检测PATH下的文件是否存在
         * @param binaryName 二进制程序文件名
         * @return Pair<Boolean, String> 是否找到和二进制绝对路径
         */
        fun binaryInSysPath(binaryName: String): Pair<Boolean, String> {
            java.lang.System.getenv("PATH").split(Info.PATH_DELIMITER).forEach {
                File(it, binaryName + Info.EXECUTABLE_SUFFIX).run {
                    if (exists() && isFile) {
                        return Pair(true, absolutePath)
                    }
                }
            }
            return Pair(false, "")
        }
    }

    object Info {
        val EXECUTABLE_SUFFIX = if (getOSName().startsWith("win")) ".exe" else ""
        val PATH_DELIMITER = if (getOSName().startsWith("win")) ";" else ":"

        /**
         * 获取操作系统名称
         */
        private fun getOSName(): String {
            return java.lang.System.getProperty("os.name").toLowerCase()
        }
    }

    class Runtime {
        data class RuntimeOutput(
            val status: Boolean,
            val message: String,
            val error: String
        )

        private var javaRuntime: java.lang.Runtime = java.lang.Runtime.getRuntime()

        /**
         * 扩展Any静态方法: 从流中读取数据
         * @return 读取的内容
         */
        private fun Any.readSteam(): String {
            var content = ""
            var line: String?
            if (this is InputStream) {
                try {
                    val reader = BufferedReader(InputStreamReader(this))
                    while (reader.readLine().also { line = it }.run { return@run this?.let { return@let true } == true })
                        content += line + '\n'
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    try {
                        this.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return content
        }

        /**
         * 在默认终端中执行命令
         * @param binaryPath 二进制程序路径
         * @param args cmdline传入参数
         */
        fun exec(binaryPath: String, vararg args: String): RuntimeOutput {
            var arg = ""
            args.forEach {
                arg += " $it"
            }
            var exitCode: Int = -1
            var message = ""
            var error = ""
            try {
                val process: Process = javaRuntime.exec("$binaryPath $arg")
                exitCode = process.waitFor()
                message = process.inputStream.readSteam()
                error = process.errorStream.readSteam()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return RuntimeOutput(exitCode == 0, message, error)
        }
    }
}
