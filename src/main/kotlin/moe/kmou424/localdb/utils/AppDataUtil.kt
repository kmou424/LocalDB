package moe.kmou424.localdb.utils

import java.io.File

object AppDataUtil {
    val BaseDir = getApplicationDataDir(true)
    val ConfigDir = getApplicationDataSubDir("config", true)
    val DataBaseDir = getApplicationDataSubDir("database", true)

    class FileManager {
        private var file: File

        constructor(path: String) : this(File(path))

        constructor(file: File) {
            this.file = file
        }

        fun getSelfFile() = file

        fun getDir(subdirName: String, needCreate: Boolean = false): FileManager {
            return FileManager(File(file, subdirName).also {
                if (!it.exists() && !it.isDirectory && needCreate) {
                    it.mkdirs()
                }
            })
        }

        fun getFile(fileName: String, needCreate: Boolean = false): FileManager {
            return FileManager(File(file, fileName).also {
                if (!it.exists() && !it.isFile && needCreate) {
                    it.createNewFile()
                }
            })
        }

    }

    private fun getUserHomePath(): FileManager {
        return FileManager(System.getProperty("user.home"))
    }

    private fun getApplicationDataDir(needCreate: Boolean = false): FileManager {
        val appBaseDir = File(getUserHomePath().getSelfFile(), ".LocalDB")
        if (needCreate) appBaseDir.also {
            if (!it.exists()) it.mkdirs()
        }
        return FileManager(appBaseDir)
    }

    private fun getApplicationDataSubDir(subdir: String, needCreate: Boolean = false): FileManager {
        val appBaseSubDir = File(getApplicationDataDir(needCreate).getSelfFile().absolutePath, subdir)
        if (needCreate) appBaseSubDir.also {
            if (!it.exists()) it.mkdirs()
        }
        return FileManager(appBaseSubDir)
    }
}