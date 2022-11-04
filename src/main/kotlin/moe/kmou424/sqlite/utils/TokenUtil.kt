package moe.kmou424.sqlite.utils

import moe.kmou424.Global
import moe.kmou424.common.utils.SimpleTokenUtil
import moe.kmou424.sqlite.SQLiteManager
import moe.kmou424.sqlite.dao.SQLiteUserTable
import moe.kmou424.sqlite.enums.ColumnType
import java.time.LocalDateTime

object TokenUtil {

    inline fun <reified T : SQLiteUserTable> SimpleTokenUtil.getUniqueTokenForUserType(databaseManager: SQLiteManager): String {
        var token = generate()
        var query = listOf<T>().toMutableList().also {
            it.add(T::class.java.getConstructor().newInstance())
        }.toList()

        while (query.isNotEmpty()) {
            token = generate()
            query = queryTokenForUserType(databaseManager, token)
        }

        return token
    }

    inline fun <reified T : SQLiteUserTable> SimpleTokenUtil.queryTokenForUserType(databaseManager: SQLiteManager, token: String): List<T> {
        return databaseManager.query(
            Global.SysTables.Users,
            listOf(
                Pair("id", ColumnType.INTEGER),
                Pair("name", ColumnType.TEXT),
                Pair("password", ColumnType.TEXT),
                Pair("tokenWillExpire", ColumnType.BOOLEAN),
                Pair("token", ColumnType.TEXT),
                Pair("tokenExpireTime", ColumnType.DATETIME)
            ),
            "token=?",
            listOf(token)
        )
    }

    inline fun <reified T : SQLiteUserTable> SimpleTokenUtil.verifyTokenForUserType(databaseManager: SQLiteManager, token: String): Boolean {
        val query = queryTokenForUserType<T>(databaseManager, token)
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