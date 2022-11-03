package moe.kmou424.localdb.utils

import moe.kmou424.Global
import moe.kmou424.sqlite.SQLiteManager
import moe.kmou424.sqlite.dao.SQLiteUserTable
import moe.kmou424.sqlite.enums.KeyType
import java.time.LocalDateTime

object SimpleTokenUtil {
    private const val TokenLen = 32;

    fun generate(tokenLen: Int = TokenLen): String {
        val str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,.;+=-_()"
        var token = ""
        for (i in 0 until tokenLen) {
            token += str[(1..str.length).random() - 1]
        }
        return token
    }

    inline fun <reified T : SQLiteUserTable> getUniqueToken(databaseManager: SQLiteManager): String {
        var token = generate()
        var query = listOf<T>().toMutableList().also {
            it.add(T::class.java.getConstructor().newInstance())
        }.toList()

        while (query.isNotEmpty()) {
            token = generate()
            query = queryToken(databaseManager, token)
        }

        return token
    }

    inline fun <reified T : SQLiteUserTable> queryToken(databaseManager: SQLiteManager, token: String): List<T> {
        return databaseManager.query(
            Global.SysTables.Users,
            listOf(
                Pair("id", KeyType.INTEGER),
                Pair("name", KeyType.TEXT),
                Pair("password", KeyType.TEXT),
                Pair("tokenWillExpire", KeyType.BOOLEAN),
                Pair("token", KeyType.TEXT),
                Pair("tokenExpireTime", KeyType.DATETIME)
            ),
            "token=?",
            listOf(token)
        )
    }

    inline fun <reified T : SQLiteUserTable> verifyToken(databaseManager: SQLiteManager, token: String): Boolean {
        val query = queryToken<T>(databaseManager, token)
        if (query.size == 1) {
            val user = query[0]
            return if (user.tokenWillExpire) {
                LocalDateTime.now().isBefore(LocalDateTime.parse(user.tokenExpireTime))
            } else {
                true
            }
        }
        return false
    }
}